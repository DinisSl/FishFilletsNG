package objects;

import pt.iscte.poo.utils.Point2D;

public class Water extends GameObject{

	public Water(Point2D p) {
		super(p);
	}

    @Override
	public String getName() {
		return "water";
	}

	@Override
	public int getLayer() {
		return 0;
	}

    @Override
    public boolean blocksMovement(GameCharacter gameCharacter) {
        return false;
    }
}
