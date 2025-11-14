package objects;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class BigFish extends GameCharacter {
	
	public BigFish(Point2D p) {
		super(p);
	}

//    Se tiver a apontar para a direita devolve o bigFishRight,
//    se tiver a apontar para a esquerda devolve o bigFishLeft
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
//    Se o SmallFish bater no BigFish bloquea se n passa
    @Override
    public boolean blocksMovement(GameCharacter gameCharacter) {
        if (gameCharacter instanceof SmallFish)
            return true;
        return false;
    }

}
