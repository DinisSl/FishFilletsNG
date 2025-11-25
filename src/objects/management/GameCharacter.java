package objects.management;

import interfaces.Pushable;
import objects.BigFish;
import objects.SmallFish;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;

public abstract class GameCharacter extends GameObject {
//    Serve para guardar o lado para que o peixe está a olhar (RIGHT OU LEFT)
    private Direction currentDirection;
    private final List<FallingObject> supportedObjects;

    public GameCharacter(Point2D p) {
		super(p);
//        Ele olha inicialmente para a esquerda
        this.currentDirection = Direction.LEFT;
        this.supportedObjects = new ArrayList<>();
	}

	public void move(Vector2D dir) {
//        Calcula o Point2D de destino do peixe
        Point2D destination = getNextPosition(dir);
        updateCurrentDirection(super.getPosition(), destination);
		setPosition(destination);
	}

    public Point2D getNextPosition(Vector2D dir) {
//        Calcula a próxima posição do peixe somando um Vector2D a um Point2D
        Point2D currentPosition = super.getPosition();
		return currentPosition.plus(dir);
    }
    private void updateCurrentDirection(Point2D a, Point2D b) {
//        Calcula um Vector2D da proxima posição do peixe
        Vector2D vector2D = Vector2D.movementVector(a, b);
//        Passa o Vector2D para uma Direction
        Direction potentialDir = Direction.forVector(vector2D);
//        Se a Direction for LEFT ou RIGHT atribui-a ao atributo currentDirection
        if (potentialDir == Direction.LEFT || potentialDir == Direction.RIGHT)
            this.currentDirection = potentialDir;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void addSupportedObject(FallingObject fo) {
        boolean existsEqual = false;
        for (FallingObject obj : this.supportedObjects) {
            if (fo.equals(obj)) {
                existsEqual = true;
                break;
            }
        }
        if (!existsEqual)
            this.supportedObjects.add(fo);
    }
    public void removeSupportedObject(FallingObject fo) {
        this.supportedObjects.remove(fo);
    }
    public List<FallingObject> getSupportedObjects() {
        return List.copyOf(this.supportedObjects);
    }
    public void clearSupportedObjects() {
        this.supportedObjects.clear();
    }
    public void checkSupportOverload(Room room) {
        int heavyFO = 0;
        int lightFO = 0;

        for (FallingObject fo : getSupportedObjects()) {
            if (fo instanceof Pushable pushable) {
                if (pushable.getWeight() == Weight.HEAVY) {
                    heavyFO++;
                } else if (pushable.getWeight() == Weight.LIGHT) {
                    lightFO++;
                }
            }
        }

        List<GameCharacter> toKill = new ArrayList<>();

        if (this instanceof BigFish) {
            if (heavyFO > 1 || (lightFO > 0 && heavyFO > 0) )
                toKill.add(this);

        } else if (this instanceof SmallFish) {
            if (lightFO > 1 || (lightFO > 0 && heavyFO > 0))
                toKill.add(this);

        }

        if (!toKill.isEmpty())
            room.killGameCharacter(toKill, false);
    }

	@Override
	public int getLayer() {
		return 3;
	}

}