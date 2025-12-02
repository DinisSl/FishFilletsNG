package objects;

import interfaces.Movable;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.List;

public class BigFish extends GameCharacter {
	
	public BigFish(Point2D p) {
		super(p);
	}

    // Se tiver a apontar para a direita devolve o bigFishRight,
    // Se tiver a apontar para a esquerda devolve o bigFishLeft
    @Override
	public String getName() {
        if (super.getCurrentDirection() == Direction.RIGHT) {
            return "buoy";
        } else if (super.getCurrentDirection() == Direction.LEFT) {
            return "bigFishLeft";
        }
        return "bigFishLeft";
	}

    // Se o SmallFish bater no BigFish bloqueia se n passa
    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return true;
    }

    @Override
    public boolean processMovement(Direction direction, Room room) {
        Vector2D vector = direction.asVector();
        Point2D nextPos = getNextPosition(vector);
        List<GameObject> nextObjs = room.getGrid().getObjectsAt(nextPos);


        for (GameObject obj : nextObjs) {
            // If we find an object that blocks us AND we cannot move it (e.g., HoledWall)
            // then we are blocked, even if there is a pushable Cup on top.
            if (obj.blocksMovement(this) && !(obj instanceof Movable)) {
                checkDeadlyCollision(obj, room);
                return false;
            }
        }

        GameObject nextObj = room.getGrid().getAt(nextPos);

        // 1. Verify Exit
        if (nextObj == null) {
            return room.handleExit();
        }

        // 2. Verify blocking / pushing
        if (nextObj.blocksMovement(this)) {
            checkDeadlyCollision(nextObj, room);

            if (nextObj instanceof Movable movable) {
                if (movable.canBePushedBy(this)) {
                    room.attemptChainPush(vector);
                }
            }
            return false;
        }

        // 3. Path free
        moveSelf(vector, room);
        return false;
    }

    @Override
    public boolean canPush(Weight weight) {
        return true;
    }

    public boolean isOverloaded(int heavyFO, int lightFO) {
        return heavyFO > 1;
    }
}
