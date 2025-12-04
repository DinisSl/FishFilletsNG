package objects.characters;

import interfaces.Movable;
import objects.base.GameCharacter;
import objects.base.GameObject;
import objects.attributes.Size;
import objects.attributes.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.List;

public class BigFish extends GameCharacter {
	
	public BigFish(Point2D p) {
		super(p);
	}

    public boolean isOverloaded(int heavyFO, int lightFO) {
        return heavyFO > 1;
    }

    @Override
    public Size getSize() {
        return Size.BIG;
    }

    /**
     * Processa o movimento da entidade numa direção específica.
     *
     * Calcula a nova posição e verifica interações com a grelha da sala.
     * Se o destino for inválido, tenta sair da sala. Caso contrário, verifica
     * colisões com obstáculos, interações mortais ou tenta empurrar objetos movíveis.
     * Se o caminho estiver livre, move a entidade.
     *
     * @param direction A direção do movimento.
     * @param room A sala onde ocorre a interação.
     * @return true se o objeto for removido ou houver uma mudança de nível, false caso contrário.
     */
    @Override
    public boolean processMovement(Direction direction, Room room) {
        Vector2D vector = direction.asVector();
        Point2D nextPos = getNextPosition(vector);

        GameObject nextObj = room.getGrid().getAt(nextPos);
        if (nextObj == null) {
            return room.handleExit();
        }

        List<GameObject> nextObjs = room.getGrid().getObjectsAt(nextPos);

        for (GameObject obj : nextObjs) {
            if (obj.blocksMovement(this) && !(obj instanceof Movable)) {
                checkDeadlyCollision(obj, room);
                return !room.getActiveGC().contains(this);
            }
        }

        if (checkCommonCollisions(nextObj, room)) {
            return true;
        }

        if (nextObj.blocksMovement(this)) {
            if (nextObj instanceof Movable movable &&
                    movable.canBePushedBy(this, direction)) {
                attemptChainPush(vector, room);
            }
            return false;
        }

        moveSelf(vector, room);
        return false;
    }

    @Override
    public boolean canPush(Weight weight) {
        return true;
    }

     /*Se tiver a apontar para a direita devolve o bigFishRight,
     Se tiver a apontar para a esquerda devolve o bigFishLeft*/
    @Override
	public String getBaseName() {
        return "bigFish";
	}
}
