package objects;

import interfaces.Movable;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Stone extends FallingObject implements Movable {

    public Stone(Point2D p) {
        super(p);
    }

    @Override
    public void onLanded(Room room, Point2D landedOn) {
        GameObject destObj = room.getGrid().getAt(landedOn);

        if (destObj.canBeCrushed())
            destObj.onCrushed(room);
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
        if (objInNextPos.isFluid() && gc.canPush(Weight.HEAVY)) {
            room.moveObject(this, to);
            return true;
        }
        return false;
    }
}