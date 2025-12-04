package objects.characters;

import interfaces.markerInterfaces.FitsInHole;
import interfaces.Movable;
import objects.attributes.Size;
import objects.attributes.Weight;
import objects.base.*;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class SmallFish extends GameCharacter implements FitsInHole {

	public SmallFish(Point2D p) {
        super(p);
	}

    public boolean isOverloaded(int heavyFO, int lightFO) {
        return heavyFO > 0 || lightFO > 1;
    }

    @Override
    public Size getSize() {
        return Size.SMALL;
    }

    @Override
    public boolean processMovement(Direction direction, Room room) {
        Vector2D vector = direction.asVector();
        Point2D nextPos = getNextPosition(vector);
        GameObject nextObj = room.getGrid().getAt(nextPos);

        if (nextObj == null)
            return room.handleExit();

        if (checkCommonCollisions(nextObj, room)) return true;

        // Verificar se está bloqueado e interagir
        if (nextObj.blocksMovement(this)) {
            // Verificar se pode empurrar (Lógica simples do SmallFish)
            if (nextObj instanceof Movable movable && movable.canBePushedBy(this, direction)) {
                Point2D pushTo = nextPos.plus(vector);

                // Tenta empurrar o objeto. Se conseguir, o peixe move-se atrás.
                if (movable.push(room, nextPos, pushTo))
                    moveSelf(vector, room);
            }

            return false; // Estava bloqueado (mesmo que tenha empurrado, o turno de input acaba)
        }

        // Caminho livre (Água ou Background)
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
    public String getBaseName() {
        return "smallFish";
    }
}
