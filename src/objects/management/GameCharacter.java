package objects.management;

import interfaces.Deadly;
import interfaces.NonBlocking;
import interfaces.Movable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GameCharacter extends GameObject {

    /*Set para guardar os Falling Objects que o Game Character está
    a suportar, utilizamos um set, pois por defeito não permite duplicados*/
    private final Set<FallingObject> supportedObjects;

    // Serve para guardar o lado para que o peixe está a olhar (RIGHT OU LEFT)
    private Direction currentDirection;

    public GameCharacter(Point2D p) {
        super(p);
        // Ele olha inicialmente para a esquerda
        this.currentDirection = Direction.LEFT;
        this.supportedObjects = new HashSet<>();
    }

    // Métodos abstratos que as subclasses (BigFish, SmallFish) devem implementar
    public abstract boolean processMovement(Direction direction, Room room);

    public abstract boolean canPush(Weight weight);

    public abstract boolean isOverloaded(int heavyFO, int lightFO);

    @Override
    public void update(Room room) {
        // Limpar lista antiga de objetos suportados
        clearSupportedObjects();

        // Verificar o que está diretamente acima (Lógica movida do GravitySystem)
        List<GameObject> objsAbove = room.getGrid().allObjectsAboveToSide(this.getPosition(), Direction.UP);

        for (GameObject go : objsAbove) {
            if (go instanceof FallingObject fo) {
                if (fo.canFallThrough(this)) continue;
                addSupportedObject(fo);
            } else {
                // Se encontrar algo que não cai (parede), para de procurar para cima
                break;
            }
        }

        // Verificar se morre com o peso atual
        checkSupportOverload(room);
    }

    @Override
    public int getLayer() {
        return super.LAYER_GAME_CHARACTER;
    }

    /*----------------------------------------------------------------
    VERIFICA SE UM GAME CHARACTER MORRE DEVIDO AOS OBJETOS QUE SUPORTA
    ------------------------------------------------------------------*/
    public void checkSupportOverload(Room room) {
        int heavyFO = 0;
        int lightFO = 0;

        for (FallingObject fo : this.supportedObjects) {
            if (fo instanceof Movable movable) {
                if (movable.getWeight() == Weight.HEAVY) {
                    heavyFO++;
                } else if (movable.getWeight() == Weight.LIGHT) {
                    lightFO++;
                }
            }
        }

        if (isOverloaded(heavyFO, lightFO)) {
            room.killGameCharacter(List.of(this), false);
        }
    }

    /*-----------------------------------------------------------
    MÉTODOS PARA MANIPULAR O SET
    -----------------------------------------------------------*/
    public void addSupportedObject(FallingObject fo) {
        this.supportedObjects.add(fo);
    }

    public void clearSupportedObjects() {
        this.supportedObjects.clear();
    }

    /*-----------------------------------------------------------
    FUNÇÕES RELACIONADAS COM O MOVIMENTO DO GAME CHARACTER
    -----------------------------------------------------------*/

    // Metodo utilitário comum para realizar o movimento final se estiver livre
    public void moveSelf(Vector2D vector, Room room) {
        // Limpa objetos que estava a segurar
        this.clearSupportedObjects();

        Point2D destination = getPosition().plus(vector);

        updateCurrentDirection(vector);

        room.moveObject(this, destination);
    }

    public Point2D getNextPosition(Vector2D dir) {
        // Calcula a próxima posição do peixe somando um Vector2D a um Point2D
        Point2D currentPos = super.getPosition();
        return currentPos.plus(dir);
    }

    private void updateCurrentDirection(Vector2D vector) {
        // Passa o Vector2D para uma Direction
        Direction potentialDir = Direction.forVector(vector);
        // Se a Direction for LEFT ou RIGHT atribui-a ao atributo currentDirection
        if (potentialDir == Direction.LEFT || potentialDir == Direction.RIGHT)
            this.currentDirection = potentialDir;
    }

    // Lógica comum para interagir com objetos mortais (Armadilhas, Inimigos)
    public void checkDeadlyCollision(GameObject obj, Room room) {
        if (obj instanceof Deadly deadly) {
            // Houve colisão mortal (ou interação mortal)
            deadly.onCharacterContact(this, room);
        }
    }

    /*-----------------------------------------------------------
    DIREÇÃO DO GAME CHARACTER
    -----------------------------------------------------------*/
    public Direction getCurrentDirection() {
        return this.currentDirection;
    }

    public void attemptChainPush(Vector2D vector, Room room) {
        Direction dir = Direction.forVector(vector);
        // Obtém objetos na direção do movimento
        List<GameObject> lineOfObjects = room.getGrid().allObjectsAboveToSide(this.getPosition(), dir);

        List<Movable> pushChain = new ArrayList<>();
        boolean canMove = false;

        for (GameObject obj : lineOfObjects) {
            if (obj instanceof NonBlocking) {
                // Encontrou espaço vazio, pode empurrar tudo até aqui
                canMove = true;
                break;
            }

            if (obj instanceof Movable p && p.canBePushedBy(this)) {
                pushChain.add(p);
            } else {
                // Parede ou objeto imóvel
                break;
            }
        }
        // Executar o empurrão de trás para a frente
        if (canMove && !pushChain.isEmpty()) {
            chainPushObjects(vector, pushChain, room);
        }
    }

    public void chainPushObjects(Vector2D vector, List<Movable> pushChain, Room room) {
        for (int i = pushChain.size() - 1; i >= 0; i--) {
            Movable p = pushChain.get(i);
            GameObject obj = (GameObject) p;
            p.push(room, obj.getPosition(), obj.getPosition().plus(vector));
        }
       this.moveSelf(vector, room);
    }
}