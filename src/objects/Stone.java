package objects;

import interfaces.Destroyable;
import interfaces.Fluid;
import interfaces.Movable;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Stone extends FallingObject implements Movable {

    public Stone(Point2D p) {
        super(p);
    }

    @Override
    public void onLanded(Room room, Point2D landedOn) {
        GameObject destObj = room.getGrid().getAt(landedOn);

        if (destObj instanceof Destroyable d) {
            // 1. Check if "this" falling object is heavy enough to destroy "d"
            if (d.canBeDestroyedBy(this)) {
                // 2. Perform the destruction
                d.onDestroyed(room);
            }
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

    @Override
    public boolean canBePushedBy(GameCharacter character) {
        return character.canPush(this.getWeight());
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        GameCharacter gc = room.getCurrentGameCharacter();
        GameObject objInNextPos = room.getGrid().getAt(to);
        if (objInNextPos instanceof Fluid && gc.canPush(Weight.HEAVY)) {
            room.moveObject(this, to);
            return true;
        }
        return false;
    }
}