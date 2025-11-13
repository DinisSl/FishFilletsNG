package objects;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class BigFish extends GameCharacter {
	
	public BigFish(Point2D p) {
		super(p);
	}


    @Override
	public String getName() {
        if (super.getCurrentDirection().equals(Direction.RIGHT)) {
            return "bigFishRight";
        } else if (super.getCurrentDirection().equals(Direction.LEFT)) {
            return "bigFishLeft";
        }
        return "bigFishLeft";
	}

	@Override
	public int getLayer() {
		return 2;
	}

    @Override
    public boolean blocksMovement(GameCharacter gameCharacter) {
        if (gameCharacter instanceof SmallFish)
            return true;
        return false;
    }

}
