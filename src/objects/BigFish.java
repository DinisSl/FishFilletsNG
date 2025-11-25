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

public class BigFish extends GameCharacter {
	
	public BigFish(Point2D p) {
		super(p);
	}



    //    Se tiver a apontar para a direita devolve o bigFishRight,
    // se tiver a apontar para a esquerda devolve o bigFishLeft
    @Override
	public String getName() {
        if (super.getCurrentDirection().equals(Direction.RIGHT)) {
            return "bigFishRight";
        } else if (super.getCurrentDirection().equals(Direction.LEFT)) {
            return "bigFishLeft";
        }
        return "bigFishLeft";
	}

    // Se o SmallFish bater no BigFish bloqueia se n passa
    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return true;
    }
}
