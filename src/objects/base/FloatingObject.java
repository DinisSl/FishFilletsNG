package objects.base;

import interfaces.Movable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public abstract class FloatingObject extends PhysicsObject implements Movable {

    public FloatingObject(Point2D point) {
        super(point);
    }

    /**
     * É chamado por Room a cada tick de jogo. Verifica o Game Object
     * por cima do Floating Object que está a cair. Consequentemente
     * aplica o tipo de comportamento correspondente conforme o Game Object
     *
     * @param room A sala onde o objeto se encontra
     */
    @Override
    public void update(Room room) {
        Point2D currPos = getPosition();
        Point2D posAbove = currPos.plus(Direction.UP.asVector());

        // Se estiver fora do mapa, remove (safety check)
        if (!room.getGrid().isInBounds(posAbove)) {
            room.removeObject(this);
            return;
        }

        GameObject objAbove = room.getGrid().getAt(posAbove);

        if (tryMovingIntoBackground(room, objAbove, posAbove)) return;

        handleLanding(room, posAbove);
    }
}
