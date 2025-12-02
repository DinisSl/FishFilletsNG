package objects;

import objects.management.GameCharacter;
import objects.management.GameObject;
import pt.iscte.poo.utils.Point2D;

public class Water extends GameObject {

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

    @Override
    public boolean isFluid() {
        return true;
    }
}
