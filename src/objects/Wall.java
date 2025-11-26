package objects;

import objects.management.GameCharacter;
import objects.management.GameObject;
import pt.iscte.poo.utils.Point2D;

public class Wall extends GameObject {

    public Wall(Point2D p) { super(p); }

    @Override
    public String getName() {
        return "wall";
    }
    @Override
    public int getLayer() { return super.LAYER_OBSTACLES; }

    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return true;
    }
}
