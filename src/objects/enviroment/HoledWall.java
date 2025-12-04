package objects.enviroment;

import interfaces.markerInterfaces.FitsInHole;
import interfaces.markerInterfaces.Passable;
import objects.base.GameObject;
import pt.iscte.poo.utils.Point2D;

public class HoledWall extends GameObject implements Passable {

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

    // Se o GameCharacter couber num buraco não bloqueia o movimento
    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return !(gameCharacter instanceof FitsInHole);
    }
}
