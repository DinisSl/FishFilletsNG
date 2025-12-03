package objects.base;

import interfaces.Movable;
import interfaces.NonBlocking;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public abstract class FloatingObject extends GameObject implements Movable {
    private boolean floatingUp;

    public FloatingObject(Point2D point) {
        super(point);
        this.floatingUp = false;
    }


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

        if (tryFallingIntoBackground(room, objAbove, posAbove)) return;

        handleLanding(room, posAbove);
    }

    private boolean tryFallingIntoBackground(Room room, GameObject objAbove, Point2D posAbove) {
        if (objAbove instanceof NonBlocking) {
            if (!this.isMoving()) onStartMovement();

            room.moveObject(this, posAbove);
            return true;
        }
        return false;
    }

    private void handleLanding(Room room, Point2D posAbove) {
        if (this.floatingUp) {
            onFinishedMovement(room, posAbove);
            this.floatingUp = false; // Parou de subir
        }
    }

    // Movable interface
    @Override
    public void onStartMovement() {
        this.floatingUp = true;
    }

    @Override
    public boolean isMoving() {
        return this.floatingUp;
    }

    // Metodo abstrato da classe abstrata GameObject
    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return true;
    }

    @Override
    public int getLayer() {
        return super.LAYER_ITEMS;
    }

}
