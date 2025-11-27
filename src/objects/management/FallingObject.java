package objects.management;

import interfaces.Destroyable;
import interfaces.Movable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import objects.*;

public abstract class FallingObject extends GameObject implements Movable {
    private boolean falling;

    public FallingObject(Point2D point) {
        super(point);
        this.falling = false;
    }

    // --- NOVA IMPLEMENTAÇÃO: Lógica movida do GravitySystem ---
    @Override
    public void update(Room room) {
        Point2D currPos = getPosition();
        Point2D posBelow = currPos.plus(Direction.DOWN.asVector());

        // Se estiver fora do mapa, remove (safety check)
        if (!room.getGrid().isInBounds(posBelow)) {
            room.removeObject(this);
            return;
        }

        GameObject objBelow = room.getGameObject(posBelow);

        // Interações específicas
        if (this instanceof Cup && objBelow instanceof HoledWall) {
            room.moveObject(this, posBelow);
            return;
        }
        if (this instanceof Trap && objBelow instanceof SmallFish) {
            room.moveObject(this, posBelow);
            return;
        }

        // Lógica geral de cair
        if (objBelow instanceof Water || objBelow instanceof Blood) {
            if (!this.falling) {
                onStartFall();
            }
            room.moveObject(this, posBelow);
        }
        else if (objBelow instanceof Destroyable destroyable) {
            // Tenta destruir o objeto abaixo (ex: Trunk)
            if (destroyable.canBeDestroyedBy(this)) {
                destroyable.onDestroyed(room);
                // Nota: O objeto cai no próximo tick, não instantaneamente
            } else {
                handleLanding(room, posBelow);
            }
        }
        else {
            handleLanding(room, posBelow);
        }
    }

    private void handleLanding(Room room, Point2D posBelow) {
        if (this.falling) {
            onLanded(room, posBelow);
            this.falling = false; // Parou de cair
        }
    }


    // GravityAffected interface
    @Override
    public void onStartFall() {
        this.falling = true;
    }
    @Override
    public boolean isFalling() {
        return this.falling;
    }

    // GameObject metodo abstrato da classe abstrata
    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return true;
    }

    @Override
    public int getLayer() {
        return super.LAYER_OBSTACLES; // para ficar visível por cima
    }
}
