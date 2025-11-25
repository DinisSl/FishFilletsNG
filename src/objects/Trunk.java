package objects;

import interfaces.Destroyable;
import interfaces.Pushable;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
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
        return 1;
    }

    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        // tronco bloqueia os peixes
        return true;
    }

    @Override
    public void onDestroyed(Room room) {
        room.removeObject(this);
    }

    @Override
    public boolean canBeDestroyedBy(FallingObject object) {
        if (object instanceof Pushable pushable)
            return pushable.getWeight() == Weight.HEAVY;
        return false;
    }
}