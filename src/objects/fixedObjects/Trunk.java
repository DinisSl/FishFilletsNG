package objects.fixedObjects;

import interfaces.Destroyable;
import objects.base.SinkingObject;
import objects.base.GameObject;
import objects.attributes.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Trunk extends GameObject implements Destroyable {

    public Trunk(Point2D p) {
        super(p);
    }

    @Override
    public String getName() {
        return "trunk";
    }

    @Override
    public int getLayer() {
        return super.LAYER_OBSTACLES;
    }

    @Override
    public void onDestroyed(Room room) {
        room.removeObject(this);
    }

    @Override
    public boolean canBeDestroyedBy(SinkingObject object) {
        return object.getWeight() == Weight.HEAVY;
    }
}