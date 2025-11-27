package objects;

import objects.management.GameObject;
import pt.iscte.poo.utils.Point2D;

public class HoledWall extends GameObject {

    public HoledWall(Point2D p) {
        super(p);
    }

    @Override
    public String getName() {
        return "holedWall";
    }

    @Override
    public int getLayer() {
        return super.LAYER_OBSTACLES;
    }

    // Se o GameCharacter for o SmallFish não bloqueia o movimento se for o BigFish bloqueia
    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return !(gameCharacter instanceof SmallFish || gameCharacter instanceof Cup);
    }

}
