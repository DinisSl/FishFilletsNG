package objects;

import interfaces.Movable;
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

public class Anchor extends FallingObject implements Movable {
    boolean pushedOnce;

    public Anchor(Point2D p) {
        super(p);
        pushedOnce = false;
    }

    @Override
    public String getName() {
        return "anchor";
    }

    @Override
    public void onLanded(Room room, Point2D posBelow) {
        GameObject destObj = room.getGrid().getAt(posBelow);

        if (destObj.canBeCrushed())
            destObj.onCrushed(room);
    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }


    @Override
    public boolean canBePushedBy(GameCharacter character) {
        return character.canPush(this.getWeight()) && !this.pushedOnce;
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        GameCharacter gc = room.getCurrentGameCharacter();
        GameObject objInNextPos = room.getGrid().getAt(to);

        Vector2D v = Vector2D.movementVector(from, to);
        Direction nextDir = Direction.forVector(v);
        if (nextDir == Direction.UP || nextDir == Direction.DOWN)
            return false;

        if (objInNextPos.isFluid() && gc.canPush(Weight.HEAVY) && !this.pushedOnce) {
            room.moveObject(this, to);
            this.pushedOnce = true;
            return true;
        }
        return false;
    }
}