package objects.movingObjects;

import interfaces.LoadBearer;
import interfaces.markerInterfaces.NonBlocking;
import objects.attributes.Weight;
import objects.base.*;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;


public class Buoy extends FloatingObject implements LoadBearer {

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
    public String getName() {
        return "buoy";
    }

    @Override
    public Weight getWeight() {
        return Weight.LIGHT;
    }

}
