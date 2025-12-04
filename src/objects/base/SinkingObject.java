package objects.base;

import interfaces.*;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public abstract class SinkingObject extends GameObject implements Movable {
    private boolean sinking;

    public SinkingObject(Point2D point) {
        super(point);
        this.sinking = false;
    }

    /**
     * É chamado por Room a cada tick de jogo. Verifica o Game Object
     * por baixo do Sinking Object que está a cair. Consequentemente
     * aplica o tipo de comportamento correspondente conforme o Game Object
     *
     * @param room A sala onde o objeto se encontra
     */
    @Override
    public void update(Room room) {
        Point2D currPos = getPosition();
        Point2D posBelow = currPos.plus(Direction.DOWN.asVector());

        // Se estiver fora do mapa, remove (safety check)
        if (!room.getGrid().isInBounds(posBelow)) {
            room.removeObject(this);
            return;
        }

        GameObject objBelow = room.getGrid().getAt(posBelow);

        if (trySinkingIntoBackground(room, objBelow, posBelow)) return;

        if (trySinkingIntoHole(room, objBelow, posBelow)) return;

        if (tryDestroyObject(room, objBelow)) return;

        handleLanding(room, posBelow);
    }

    private boolean trySinkingIntoBackground(Room room, GameObject objBelow, Point2D posBelow) {
        if (objBelow instanceof NonBlocking) {
            if (!this.isMoving()) onStartMovement();

            room.moveObject(this, posBelow);
            return true;
        }
        return false;
    }

    private boolean trySinkingIntoHole(Room room, GameObject objBelow, Point2D posBelow) {
        if (this instanceof FitsInHole && objBelow instanceof Passable) {
            room.moveObject(this, posBelow);
            return true;
        }
        return false;
    }

    private boolean tryDestroyObject(Room room, GameObject objBelow) {
        if (objBelow instanceof Destroyable destroyable) {
            // Tenta destruir o objeto abaixo ex. Trunk
            if (destroyable.canBeDestroyedBy(this)) {
                destroyable.onDestroyed(room);
                // O objeto cai no próximo tick, não instantaneamente
            }
            return true;
        }
        return false;
    }

    private void handleLanding(Room room, Point2D posBelow) {
        if (this.sinking) {
            onFinishedMovement(room, posBelow);
            this.sinking = false; // Parou de cair
        }
    }

    // Movable interface
    @Override
    public void onStartMovement() {
        this.sinking = true;
    }

    @Override
    public boolean isMoving() {
        return this.sinking;
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