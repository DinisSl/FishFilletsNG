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
     * Processa o movimento da personagem numa dada direção dentro da sala.
     *
     * Este metodo calcula a próxima posição com base na direção recebida e verifica
     * o que existe nessa posição. Dependendo do tipo de objeto encontrado, podem
     * ocorrer várias situações:
     *
     * 1 - Verifica se o próximo objeto é null se for verifica as condições de saída
     * com room.handleExit();
     *
     * 2 - Obtem a lista de todos os Game Objects presentes na posição calculada
     *
     * 3 - Verifica se o próximo Game Object bloqueia o movimento do Game Character e
     * se o mesmo não é uma instância de Movable.
     *  3A - Se assim for verifica as colisões com objetos do tipo Deadly
     *  'checkDeadlyCollision(obj, room)'. Se o Game Character tiver morrido devolve
     *  true se contrário false '!room.getActiveGC().contains(this)'
     *
     * 4 - Verifica as colisões comuns aos objetos que implementam PhysicsObject
     * 'checkCommonCollisions(nextObj, room)'
     *
     * 5 - Depois verifica se o próximo Game Object bloqueia o movimento do Game Character
     *  5A - Se bloquear, depois verifica se é Movable e se pode ser empurrado por este Game
     *  Character nesta direção 'canBePushedBy(this, direction)'
     *  5B - Por fim tenta mover os objetos na direção 'direction' porque o BigFish pode empurrar vários
     *  objetos. Chama 'attemptChainPush(direction, vector, room)' implementado na classe Game Character
     *
     *  6 - Se não bloquear é porque se está a querer mover para um espaço vazio, ou seja,
     *  só contem água, sangue ou uma explosão. Move apenas o próprio Game Character com
     *  'moveSelf(vector, room)'
     *
     * @param direction A direção para onde a personagem quer mover.
     * @param room A sala onde o movimento está a ser processado.
     * @return true se o objeto for removido ou houver uma mudança de nível, false caso contrário.
     */
    @Override
    public boolean processMovement(Direction direction, Room room) {
        Vector2D vector = direction.asVector();
        Point2D nextPos = getNextPosition(vector);

        GameObject nextObj = room.getGrid().getAt(nextPos);
        if (nextObj == null)
            return room.handleExit();

        List<GameObject> nextObjs = room.getGrid().getObjectsAt(nextPos);

        for (GameObject obj : nextObjs) {
            if (obj.blocksMovement(this) && !(obj instanceof Movable)) {
                checkDeadlyCollision(obj, room);
                return !room.getActiveGC().contains(this);
            }
        }

        if (checkCommonCollisions(nextObj, room)) return true;

        if (nextObj.blocksMovement(this)) {
            if (nextObj instanceof Movable movable && movable.canBePushedBy(this, direction)) {
                attemptChainPush(direction, vector, room);
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

    @Override
	public String getBaseName() {
        return "bigFish";
	}
}
