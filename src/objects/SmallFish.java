package objects;

import interfaces.Movable;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class SmallFish extends GameCharacter {

	public SmallFish(Point2D p) {
        super(p);
	}


    @Override
    public boolean processMovement(Direction direction, Room room) {
        Vector2D vector = direction.asVector();
        Point2D nextPos = getNextPosition(vector);
        GameObject nextObj = room.getGrid().getAt(nextPos);

        // 1. Verificar Saída
        if (nextObj == null) {
            return room.handleExit();
        }

        // 2. Verificar se está bloqueado e interagir
        if (nextObj.blocksMovement(this)) {
            // Verificar colisão mortal
            checkDeadlyCollision(nextObj, room);

            // Verificar se pode empurrar (Lógica simples do SmallFish)
            if (nextObj.isPushable(this)) {
                Movable movable = (Movable) nextObj;
                Point2D pushTo = nextPos.plus(vector);
                // Tenta empurrar o objeto. Se conseguir, o peixe move-se atrás.
                boolean pushed = movable.push(room, nextPos, pushTo);
                if (pushed) {
                    moveSelf(vector, room);
                }

            }
            return false; // Estava bloqueado (mesmo que tenha empurrado, o turno de input acaba)
        }

        // 3. Caminho livre (Água ou Background)
        moveSelf(vector, room);
        return false;
    }

    @Override
    public boolean canPush(Weight weight) {
        return weight == Weight.LIGHT;
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
    // Se o BigFish bater no SmallFish bloqueia se n passa
    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return true;
    }

    @Override
    public boolean canBeCrushed() {
        return true;
    }

    @Override
    public void onCrushed(Room room) {
        // The fish defines its own death logic!
        List<GameCharacter> self = new ArrayList<>();
        self.add(this);
        room.killGameCharacter(self, false);
    }

    @Override
    public boolean fitsInHoles() {
        return true;
    }

    public boolean isOverloaded(int heavyFO, int lightFO) {
        return heavyFO > 0 || lightFO > 1;
    }
}
