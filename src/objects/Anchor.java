package objects;

import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class Anchor extends FallingObject {
    boolean pushedOnce;

    public Anchor(Point2D p) {
        super(p);
        pushedOnce = false;
    }

    @Override
    public boolean moveIfPossible(Room room, Point2D currPos, Point2D whereToGo) {
        GameCharacter gc = room.getCurrentGameCharacter();
        GameObject objInNextPos = room.getGameObject(whereToGo);

        Vector2D v = Vector2D.movementVector(currPos, whereToGo);
        Direction nextDir = Direction.forVector(v);
        if (nextDir == Direction.UP || nextDir == Direction.DOWN)
            return false;

        if (objInNextPos instanceof Water && gc instanceof BigFish && !this.pushedOnce) {
            room.moveObject(this, whereToGo);
            this.pushedOnce = true;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "anchor";
    }

    @Override
    public void onLand(Room room, Point2D currPos, Point2D posBelow) {
        GameObject destObj = room.getGameObject(posBelow);
        List<GameCharacter> toKill = new ArrayList<>();
        if (destObj instanceof SmallFish sf) {
            toKill.add(sf);
            room.killGameCharacter(toKill, false);
        } else if (destObj instanceof Trunk) {
            room.removeObject(destObj);
        }
    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }
}