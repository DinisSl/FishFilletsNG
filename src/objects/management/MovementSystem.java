package objects.management;

import interfaces.Deadly;
import interfaces.Pushable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

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

        if (nextGameObject == null)
            return handleExit();

        if (!handleSpecialObjs(nextGameObject, nextPointGC, vector))
            return false;

        moveGameCharacter(vector);
        return false;
    }


    // SUBMETHODS, ONLY CALLED BY handleMovement(int k, Room room){}
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

    private boolean handleSpecialObjs(GameObject nextGameObject, Point2D nextPointGC, Vector2D vector) {
        GameCharacter currentGC = room.getCurrentGameCharacter();
        // PUSH OBJECT LOGIC
        if (nextGameObject.blocksMovement(currentGC)) {
            // SE FOR UM GC A EMPURRAR UM OBJETO QUE BLOQUEIA A SUA PASSAGEM
            // VAI VER SE É UM OBJETO QUE PODE CAIR, OU SEJA, PODE SER EMPURRADO
            if (nextGameObject instanceof Pushable pushable) {
                // SE FOR POSSÍVEL MEXE O OBJETO E DEPOIS ESTA FUNÇÃO
                // MEXE O GC
                if (pushable.canBePushedBy(currentGC)) {
                    boolean pushed =
                            pushable.push(
                                    room,
                                    nextPointGC,
                                    nextPointGC.plus(vector)
                            );
                    if (pushed) {
                        moveGameCharacter(vector);
                        return false;
                    }
                }
            }

            if (nextGameObject instanceof Deadly deadly) {
                deadly.onCharacterContact(currentGC, room);
                return false;
            }
            return false;
        }
        return true;
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
