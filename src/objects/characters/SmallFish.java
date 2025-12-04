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

    /**
     * Processa o movimento da personagem numa dada direção dentro da sala.
     *
     * Este metodo calcula a próxima posição com base na direção recebida e verifica
     * o que existe nessa posição. Dependendo do tipo de Game Object encontrado pode mover
     * ambos o Game Object e o Game Character ou apenas o Game Character, ou sair da Room atual
     *
     * 1 - Verifica se o próximo objeto é null se for verifica as condições de saída
     * com room.handleExit();
     * 
     * 2 - Verifica as colisões comuns aos objetos que implementam PhysicsObject
     * 'checkCommonCollisions(nextObj, room)'
     * 
     * 3 - Depois verifica se o próximo Game Object bloqueia o movimento do Game Character  
     *  3A - Se bloquear, depois verifica se é Movable e se pode ser empurrado por este Game
     *  Character nesta direção 'canBePushedBy(this, direction)'
     *  3B - Por fim verifica se o objeto reune as condições para ser empurrado 'push(room,
     *  nextPos, pushTo)' se reunir é movido e o mesmo acontece ao Game Character 
     *  movido com 'moveSelf(vector, room)'
     *  
     *  4 - Se não bloquear é porque se está a querer mover para um espaço vazio, ou seja,
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

        if (checkCommonCollisions(nextObj, room)) return true;

        if (nextObj.blocksMovement(this)) {
            if (nextObj instanceof Movable movable && movable.canBePushedBy(this, direction)) {
                Point2D pushTo = nextPos.plus(vector);

                if (movable.push(room, nextPos, pushTo))
                    moveSelf(vector, room);
            }

            return false;
        }

        moveSelf(vector, room);
        return false;
    }

    @Override
    public boolean canPush(Weight weight) {
        return weight == Weight.LIGHT;
    }

    @Override
    public String getBaseName() {
        return "smallFish";
    }
}
