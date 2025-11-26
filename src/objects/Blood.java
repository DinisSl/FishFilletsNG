package objects;

import objects.management.GameCharacter;
import objects.management.GameObject;
import pt.iscte.poo.utils.Point2D;

public class Blood extends GameObject {

    public Blood(Point2D p) {
        super(p);
    }

    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return false;
    }

    @Override
    public String getName() {
        return "blood";
    }

    @Override
    public int getLayer() {
        return super.LAYER_EFFECTS;
    }
}
