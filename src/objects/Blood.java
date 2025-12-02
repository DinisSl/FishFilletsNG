package objects;

import interfaces.Fluid;
import objects.management.GameObject;
import pt.iscte.poo.utils.Point2D;

public class Blood extends GameObject implements Fluid {

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
