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
    public int getLayer() { return 2; }

    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return true;
    }
}
