package objects;

import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Stone extends FallingObject{

    public Stone(Point2D p) {
        super(p);
    }

    @Override
    public boolean moveIfPossible(Room room, Point2D currPos, Point2D whereToGo) {
        GameCharacter gc = room.getCurrentGameCharacter();
        GameObject objInNextPos = room.getGameObject(whereToGo);
        if (objInNextPos instanceof Water && gc instanceof BigFish) {
            room.getMovementSystem().moveObject(room, this, whereToGo);
            return true;
        }
        return false;
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

//   ImageTile interface
    @Override
    public String getName() {
        return "stone";
    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }
}