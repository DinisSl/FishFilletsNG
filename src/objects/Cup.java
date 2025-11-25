package objects;

import objects.management.FallingObject;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Cup extends FallingObject {

    public Cup(Point2D p) {
        super(p);
    }

    @Override
    public boolean moveIfPossible(Room room, Point2D currPos, Point2D whereToGo) {
        GameObject objInNextPos = room.getGameObject(whereToGo);
        if (objInNextPos instanceof Water) {
            room.getMovementSystem().moveObject(this, whereToGo);
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "cup";
    }

    @Override
    public void onLand(Room room, Point2D currPos, Point2D posBelow) {
        // Não acontece nada
    }

    @Override
    public Weight getWeight() {
        return Weight.LIGHT;
    }
}
