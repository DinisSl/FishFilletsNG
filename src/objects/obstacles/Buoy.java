package objects.obstacles;

import interfaces.LoadBearer;
import interfaces.Movable;
import interfaces.NonBlocking;
import objects.attributes.Size;
import objects.attributes.Weight;
import objects.base.*;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;


public class Buoy extends FloatingObject implements Movable, LoadBearer {

    public Buoy(Point2D point) {
        super(point);
    }


    @Override
    public void update(Room room) {
        boolean sank = processLoadBearing(room);

        if (!sank)
            super.update(room);
    }

    @Override
    public boolean isOverloaded(int heavyFO, int lightFO){
        return heavyFO > 0 || lightFO > 0;
    }

    @Override
    public void onOverload(Room room) {
        Point2D pointBelow = super.getPosition().plus(Direction.DOWN.asVector());

        if (!room.getGrid().isInBounds(pointBelow)) return;

        GameObject objBelow = room.getGrid().getAt(pointBelow);

        if (objBelow instanceof NonBlocking)
            room.moveObject(this, pointBelow);
    }

    @Override
    public void onFinishedMovement(Room room, Point2D landedOn) {
        // Não acontece nada
    }

    @Override
    public boolean canBePushedBy(GameCharacter character, Direction direction) {
        if (character.getSize() == Size.SMALL) {
//            System.out.println(direction.isHorizontal());
            return direction.isHorizontal();
        }

//        System.out.println("TRUE");
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
        return "buoy";
    }

    @Override
    public Weight getWeight() {
        return Weight.LIGHT;
    }

}
