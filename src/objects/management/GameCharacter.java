package objects.management;

import interfaces.Deadly;
import interfaces.Movable;
import objects.BigFish;
import objects.SmallFish;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

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

    /*-----------------------------------------------------------
    FUNÇÕES RELACIONADAS COM O MOVIMENTO DO GAME CHARACTER
    -----------------------------------------------------------*/
    public void move(Vector2D dir) {
        // Calcula o Point2D de destino do peixe
        Point2D destination = getNextPosition(dir);
        updateCurrentDirection(super.getPosition(), destination);
        setPosition(destination);
    }

    public Point2D getNextPosition(Vector2D dir) {
        // Calcula a próxima posição do peixe somando um Vector2D a um Point2D
        Point2D currentPosition = super.getPosition();
        return currentPosition.plus(dir);
    }

    private void updateCurrentDirection(Point2D a, Point2D b) {
        // Calcula um Vector2D da proxima posição do peixe
        Vector2D vector2D = Vector2D.movementVector(a, b);
        // Passa o Vector2D para uma Direction
        Direction potentialDir = Direction.forVector(vector2D);
        // Se a Direction for LEFT ou RIGHT atribui-a ao atributo currentDirection
        if (potentialDir == Direction.LEFT || potentialDir == Direction.RIGHT)
            this.currentDirection = potentialDir;
    }

    /*-----------------------------------------------------------
    DIREÇÃO DO GAME CHARACTER
    -----------------------------------------------------------*/
    public Direction getCurrentDirection() {
        return currentDirection;
    }

    /*-----------------------------------------------------------
    MÉTODOS PARA MANIPULAR O SET
    -----------------------------------------------------------*/
    public void addSupportedObject(FallingObject fo) {
        this.supportedObjects.add(fo);
    }

    public void removeSupportedObject(FallingObject fo) {
        this.supportedObjects.remove(fo);
    }

    public void clearSupportedObjects() {
        this.supportedObjects.clear();
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

        if (shouldKill(heavyFO, lightFO)) {
            room.killGameCharacter(List.of(this), false);
        }
    }

    public boolean shouldKill(int heavyFO, int lightFO) {
        boolean shouldDie = false;

        if (lightFO > 0 && heavyFO > 0) {
            shouldDie = true;
        } else if (this instanceof BigFish && heavyFO > 1) {
            shouldDie = true;
        } else if (this instanceof SmallFish && lightFO > 1) {
            shouldDie = true;
        }

        return shouldDie;
    }

    public abstract boolean processMovement(Direction direction, Room room);

    // Metodo utilitário comum para realizar o movimento final se estiver livre
    protected void moveSelf(Vector2D vector, Room room) {
        // Limpa objetos que estava a segurar
        this.clearSupportedObjects();

        // Remove da Room, Atualiza Posição interna, Adiciona à Room
        room.removeObject(this);

        // Lógica interna de atualização de direção (existente)
        Point2D destination = getNextPosition(vector);
        updateCurrentDirection(super.getPosition(), destination);
        setPosition(destination);

        room.addObject(this);
    }

    // Lógica comum para interagir com objetos mortais (Armadilhas, Inimigos)
    protected boolean checkDeadlyCollision(GameObject obj, Room room) {
        if (obj instanceof Deadly deadly) {
            deadly.onCharacterContact(this, room);
            return true; // Houve colisão mortal (ou interação mortal)
        }
        return false;
    }

    // --- NOVA IMPLEMENTAÇÃO ---
    @Override
    public void update(Room room) {
        // 1. Limpar lista antiga de objetos suportados
        clearSupportedObjects();

        // 2. Verificar o que está diretamente acima (Lógica movida do GravitySystem)
        List<GameObject> objsAbove = room.getGrid().allObjectsAboveToSide(this.getPosition(), Direction.UP);

        for (GameObject go : objsAbove) {
            if (go instanceof FallingObject fo) {
                addSupportedObject(fo);
            } else {
                // Se encontrar algo que não cai (parede), para de procurar para cima
                break;
            }
        }

        // 3. Verificar se morre com o peso atual
        checkSupportOverload(room);
    }

    @Override
    public int getLayer() {
        return super.LAYER_GAME_CHARACTER;
    }

}