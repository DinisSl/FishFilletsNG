package objects.effects;

import interfaces.markerInterfaces.NonBlocking;
import objects.base.GameObject;
import pt.iscte.poo.utils.Point2D;

public class Blood extends GameObject implements NonBlocking {

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
