package objects;

import pt.iscte.poo.utils.Point2D;

public class End extends GameObject{
    public End(Point2D p) {
        super(p);
    }

    @Override
    public String getName() {
        return "End";
    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public boolean blocksMovement(GameCharacter gameCharacter) {
        return true;
    }
}
