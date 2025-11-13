package objects;

import pt.iscte.poo.utils.Point2D;

public class HoledWall extends GameObject{

    public HoledWall(Point2D p) {
        super(p);
    }

    @Override
    public String getName() {
        return "holedWall";
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean blocksMovement(GameCharacter gameCharacter) {
        if (gameCharacter instanceof SmallFish)
            return false;
        return true;
    }

}
