package objects.obstacles;

import interfaces.FitsInHole;
import interfaces.NonBlocking;
import interfaces.Movable;
import objects.base.SinkingObject;
import objects.base.GameCharacter;
import objects.base.GameObject;
import objects.attributes.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Cup extends SinkingObject implements Movable, FitsInHole {

    public Cup(Point2D p) {
        super(p);
    }

    @Override
    public void onFinishedMovement(Room room, Point2D posBelow) {
        // Não acontece nada
    }

    @Override
    public boolean canBePushedBy(GameCharacter character, Direction direction) {
        return true;
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        GameObject objInNextPos = room.getGrid().getAt(to);
        if (objInNextPos instanceof NonBlocking) {
            room.moveObject(this, to);
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "cup";
    }

    @Override
    public Weight getWeight() {
        return Weight.LIGHT;
    }
}
