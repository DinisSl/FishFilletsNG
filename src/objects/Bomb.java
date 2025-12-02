package objects;

import interfaces.Movable;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.List;

public class Bomb extends FallingObject implements Movable {
    private int originalY;


    public Bomb(Point2D p) {
        super(p);
        this.originalY = p.getY();
    }

    @Override
    public String getName() {
        return "bomb";
    }

    @Override
    public Weight getWeight() {
        return Weight.LIGHT;
    }
    
    @Override
    public boolean canBePushedBy(GameCharacter character) {
        return character.canPush(this.getWeight());
    }

    /**
     * Tenta empurrar a bomba de uma posição para outra.
     *
     * Uma bomba só pode ser empurrada horizontalmente (LEFT ou RIGHT).
     *
     * Obtemos o objeto na posição para a qual a bomba está a ser
     * empurrada e o objeto por baixo da bomba.
     *
     * A bomba só pode ser empurrada se o próximo Point2D estiver vazio
     *
     * Se o objeto por baixo for um Game Character atualiza a originalY para não explodir
     *
     * Por fim move a bomba e devolve true
     *
     * @param room a sala respetiva
     * @param from a posição atual da bomba
     * @param to a posição de destino para onde a bomba será empurrada
     * @return "true" se a bomba foi empurrada com sucesso, "false" se não foi
     */
    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        Vector2D objMovVector = Vector2D.movementVector(from, to);
        Direction possibleObjDir = Direction.forVector(objMovVector);

        if (possibleObjDir == Direction.UP || possibleObjDir == Direction.DOWN)
            return false;

        GameObject objInNextPos = room.getGrid().getAt(to);

        Point2D posObjBelow = from.plus(Direction.DOWN.asVector());
        GameObject objBelow = room.getGrid().getAt(posObjBelow);

        if (!objInNextPos.isFluid()) return false;

        if (objBelow instanceof GameCharacter)
            this.originalY = objInNextPos.getPosition().getY();

        room.moveObject(this, to);

        return true;
    }

    @Override
    public void onLanded(Room room, Point2D landedOn) {
        GameObject objBelow = room.getGrid().getAt(landedOn);
        if (this.getPosition().getY() != this.originalY) {
            if (!(objBelow instanceof GameCharacter)) {
                room.removeObject(this);
                List<Point2D> boomBoomPoints = this.getPosition().getNeighbourhoodPoints();
                room.explodePoints(boomBoomPoints);
            }
        }
    }
}