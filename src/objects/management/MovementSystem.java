package objects.management;

import interfaces.Deadly;
import interfaces.Pushable;
import objects.BigFish;
import objects.Water;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class MovementSystem {
    private final Room room;

    public MovementSystem(Room room) {
        this.room = room;
    }

    // MAIN METHOD HANDLES ALL THE MOVEMENT LOGIC
    public boolean handleMovement(int k) {
        Vector2D vector = Direction.directionFor(k).asVector();
        Point2D nextPointGC = room.getCurrentGameCharacter().getNextPosition(vector);
        GameObject nextGameObject = room.getGameObject(nextPointGC);

        if (nextGameObject == null) {
            return handleExit();
        }

        if (nextGameObject.blocksMovement(room.getCurrentGameCharacter())) {
            handleBlockedMovement(nextGameObject, nextPointGC, vector);
            return false;
        }

        moveGameCharacter(vector);
        return false;
    }

    private void handleBlockedMovement(GameObject nextGameObject, Point2D nextPointGC, Vector2D vector) {
        GameCharacter currentGC = room.getCurrentGameCharacter();
        if (nextGameObject instanceof Pushable pushable) {
            handlePushableObject(pushable, nextPointGC, vector);
        }

        if (nextGameObject instanceof Deadly deadly) {
            deadly.onCharacterContact(currentGC, room);
        }
    }

    private void handlePushableObject(Pushable pushable, Point2D nextPointGC, Vector2D vector) {
        GameCharacter currentGC = room.getCurrentGameCharacter();

        if (!pushable.canBePushedBy(currentGC)) return;

        if (currentGC instanceof BigFish) {
            // Pedir à Grid todos os objetos naquela direção
            Direction dir = Direction.forVector(vector);
            List<GameObject> lineOfObjects = room.getGrid().allObjectsAboveToSide(room.getCurrentGameCharacter().getPosition(), dir);

            List<Pushable> pushChain = new ArrayList<>();
            boolean canMove = false;

            // Analisar a linha para ver o que vamos empurrar
            for (GameObject obj : lineOfObjects) {
                if (obj instanceof Water) {
                    // Encontrámos um buraco vazio, podemos empurrar tudo até aqui!
                    canMove = true;
                    break;
                }

                if (obj instanceof Pushable p && p.canBePushedBy(currentGC)) {
                    pushChain.add(p);
                } else {
                    // Encontrámos uma Parede ou algo que o peixe não empurra.
                    // Paramos imediatamente. Não é possível mover.
                    break;
                }
            }

            // Executar o empurrão (de trás para a frente)
            if (canMove && !pushChain.isEmpty()) {
                for (int i = pushChain.size() - 1; i >= 0; i--) {
                    Pushable p = pushChain.get(i);
                    GameObject obj = (GameObject) p;
                    p.push(room, obj.getPosition(), obj.getPosition().plus(vector));
                }
                moveGameCharacter(vector);
            }

        } else {
            // Lógica do SmallFish (apenas 1 objeto)
            boolean pushed = pushable.push(room, nextPointGC, nextPointGC.plus(vector));
            if (pushed) {
                moveGameCharacter(vector);
            }
        }
    }

    // Este metodo só é chamado quando um peixe sai do jogo
    private boolean handleExit() {
        // Remove o currentGameCharacter da Room e da List
        room.removeObject(room.getCurrentGameCharacter());
        room.getActiveGC().remove(room.getCurrentGameCharacter());

        // Se a lista estiver vazia significa que o último GameCharacter
        // já passou logo já completou o nível e devolve true
        if (room.getActiveGCCopy().isEmpty())
            return true;

        // Se ainda não passaram os GameCharacter todos então troca o peixe
        room.setCurrentGameCharacter(room.getActiveGCCopy().getFirst());
        return false;
    }

    /*Obtém o Game Character atual
    Remove da grid e do GUI
    Atualiza a sua posição
    Adiciona de volta à grid e adiciona ao GUI*/
    private void moveGameCharacter(Vector2D vector) {
        GameCharacter gc = room.getCurrentGameCharacter();
        gc.clearSupportedObjects();
        room.removeObject(gc);
        gc.move(vector);
        room.addObject(gc);
    }

    public void moveObject(GameObject obj, Point2D newPos) {
        /*Remove o Falling Object da lista de objetos suportados do Game Character
        no caso de um Game Object ser removido de cima de um Game Character por
        uma bomba ou por outro GameCharacter, sem que o próprio Game Character
        se tenha movimentado*/
        if (obj instanceof FallingObject fo) {
            Point2D oldPosBelow = obj.getPosition().plus(Direction.DOWN.asVector());
            GameObject oldSupporter = room.getGameObject(oldPosBelow);
            if (oldSupporter instanceof GameCharacter gc)
                gc.removeSupportedObject(fo);
        }

        /*Remove da grid e do GUI
        Atualiza a posição do Game Object
        Adiciona de volta à grid e adiciona ao GUI*/
        room.removeObject(obj);
        obj.setPosition(newPos);
        room.addObject(obj);
    }
}
