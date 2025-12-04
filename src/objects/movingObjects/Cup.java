package objects.movingObjects;

import interfaces.markerInterfaces.FitsInHole;
import objects.base.SinkingObject;
import objects.attributes.Weight;
import pt.iscte.poo.utils.Point2D;

public class Cup extends SinkingObject implements FitsInHole {

    public Cup(Point2D p) {
        super(p);
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
