package objects;

import interfaces.Pushable;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class SmallFish extends GameCharacter {

	public SmallFish(Point2D p) {
        super(p);
	}

    /*Se tiver a apontar para a direita devolve o smallFishRight
    Se tiver a apontar para a esquerda devolve o smallFishLeft*/
	@Override
	public String getName() {
		if (super.getCurrentDirection() == Direction.RIGHT) {
            return "smallFishRight";
        } else if (super.getCurrentDirection() == Direction.LEFT) {
            return "smallFishLeft";
        }
        return "smallFishLeft";
	}
//    Se o BigFish bater no SmallFish bloqueia se n passa
    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return true;
    }
}
