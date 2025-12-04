package objects.enviroment;

import interfaces.markerInterfaces.NonBlocking;
import objects.base.GameObject;
import pt.iscte.poo.utils.Point2D;

public class Water extends GameObject implements NonBlocking {

	public Water(Point2D p) {
		super(p);
	}

    @Override
	public String getName() {
		return "water";
	}

	@Override
	public int getLayer() {
		return super.LAYER_WATER;
	}

    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return false;
    }

}
