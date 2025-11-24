package objects;

import objects.management.GameCharacter;
import objects.management.GameObject;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class SmallFish extends GameCharacter {

	public SmallFish(Point2D p) {
        super(p);
	}

//    Se tiver a apontar para a direita devolve o smallFishRight,
//    se tiver a apontar para a esquerda devolve o smallFishLeft
	@Override
	public String getName() {
		if (super.getCurrentDirection().equals(Direction.RIGHT)) {
            return "smallFishRight";
        } else if (super.getCurrentDirection().equals(Direction.LEFT)) {
            return "smallFishLeft";
        }
        return "smallFishLeft";
	}
//    Se o BigFish bater no SmallFish bloquea se n passa
    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        if (gameCharacter instanceof BigFish)
            return true;
        return false;
    }
}
