package objects;

import objects.management.GameCharacter;
import objects.management.GameObject;
import pt.iscte.poo.utils.Point2D;

public class Trunk extends GameObject {

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
}