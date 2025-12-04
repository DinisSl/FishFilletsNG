package objects.characters;

import interfaces.Movable;
import objects.base.GameCharacter;
import objects.base.GameObject;
import objects.attributes.Size;
import objects.attributes.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.List;

public class BigFish extends GameCharacter {
	
	public BigFish(Point2D p) {
		super(p);
	}

    public boolean isOverloaded(int heavyFO, int lightFO) {
        return heavyFO > 1;
    }

    @Override
    public Size getSize() {
        return Size.BIG;
    }

    @Override
    public boolean processMovement(Direction direction, Room room) {
        Vector2D vector = direction.asVector();
        Point2D nextPos = getNextPosition(vector);
        GameObject nextObj = room.getGrid().getAt(nextPos);
        List<GameObject> nextObjs = room.getGrid().getObjectsAt(nextPos);


        for (GameObject obj : nextObjs) {
            // If we find an object that blocks us AND we cannot move it (e.g., HoledWall)
            // then we are blocked, even if there is a pushable Cup on top.
            if (obj.blocksMovement(this) && !(obj instanceof Movable)) {
                checkDeadlyCollision(obj, room);
                if (!room.getActiveGC().contains(this)) return false;
                return false;
            }
        }

        if (checkCommonCollisions(nextObj, room)) return true;

        // 2. Verify blocking / pushing
        if (nextObj != null && nextObj.blocksMovement(this)) {
            if (nextObj instanceof Movable movable && movable.canBePushedBy(this, direction))
                    attemptChainPush(vector, room);

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

     /*Se tiver a apontar para a direita devolve o bigFishRight,
     Se tiver a apontar para a esquerda devolve o bigFishLeft*/
    @Override
	public String getBaseName() {
        return "bigFish";
	}
}
