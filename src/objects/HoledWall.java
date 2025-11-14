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

//    Se o GameCharacter for o SmallFish não bloqueia o movimento se for o BigFish bloqueia
    @Override
    public boolean blocksMovement(GameCharacter gameCharacter) {
        if (gameCharacter instanceof SmallFish)
            return false;
        return true;
    }

}
