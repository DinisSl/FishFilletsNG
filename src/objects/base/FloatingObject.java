package objects.base;

import interfaces.Movable;
import interfaces.NonBlocking;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public abstract class FloatingObject extends GameObject implements Movable {
    private boolean floating;

    public FloatingObject(Point2D point) {
        super(point);
        this.floating = false;
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

        if (tryFloatingIntoBackground(room, objAbove, posAbove)) return;

        handleLanding(room, posAbove);
    }

    private boolean tryFloatingIntoBackground(Room room, GameObject objAbove, Point2D posAbove) {
        if (objAbove instanceof NonBlocking) {
            if (!this.isMoving()) onStartMovement();

            room.moveObject(this, posAbove);
            return true;
        }
        return false;
    }

    private void handleLanding(Room room, Point2D posAbove) {
        if (this.floating) {
            onFinishedMovement(room, posAbove);
            this.floating = false; // Parou de subir
        }
    }

    // Movable interface
    @Override
    public void onStartMovement() {
        this.floating = true;
    }

    @Override
    public boolean isMoving() {
        return this.floating;
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
