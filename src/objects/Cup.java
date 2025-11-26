package objects;

import interfaces.Pushable;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Cup extends FallingObject implements Pushable {

    public Cup(Point2D p) {
        super(p);
    }

    @Override
    public String getName() {
        return "cup";
    }

    @Override
    public void onLanded(Room room, Point2D posBelow) {
        // Não acontece nada
    }

    @Override
    public Weight getWeight() {
        return Weight.LIGHT;
    }


    @Override
    public boolean canBePushedBy(GameCharacter character) {
        return true;
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        GameObject objInNextPos = room.getGameObject(to);
        if (objInNextPos instanceof Water) {
            room.getMovementSystem().moveObject(this, to);
            return true;
        }

        return false;
    }
}
