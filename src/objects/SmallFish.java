package objects;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class SmallFish extends GameCharacter {

	public SmallFish(Point2D p) {
        super(p);
	}
	
	@Override
	public String getName() {
		if (super.getCurrentDirection().equals(Direction.RIGHT)) {
            return "smallFishRight";
        } else if (super.getCurrentDirection().equals(Direction.LEFT)) {
            return "smallFishLeft";
        }
        return "smallFishLeft";
	}

    @Override
    public boolean blocksMovement(GameCharacter gameCharacter) {
        if (gameCharacter instanceof BigFish)
            return true;
        return false;
    }
}
