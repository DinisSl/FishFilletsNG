package objects;

import pt.iscte.poo.utils.Point2D;

public class Wall extends GameObject {

    public Wall(Point2D p) { super(p); }

    @Override
    public String getName() {
        return "wall";
    }
    @Override
    public int getLayer() { return 1; }

    @Override
    public boolean blocksMovement(GameCharacter gameCharacter) {
        return true;
    }
}
