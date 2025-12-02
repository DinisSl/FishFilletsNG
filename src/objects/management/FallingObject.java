package objects.management;

import interfaces.Destroyable;
import interfaces.Movable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public abstract class FallingObject extends GameObject implements Movable {
    private boolean falling;

    public FallingObject(Point2D point) {
        super(point);
        this.falling = false;
    }

    /**
     * É chamado por Room a cada tick de jogo. Verifica o Game Object
     * por baixo do Falling Object que está a cair. Consequentemente
     * aplica o tipo de comportamento correspondente conforme o Game Object
     *
     * @param room Serve para usar os métodos disponíveis na classe Room
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

        if (tryFallingIntoBackground(room, objBelow, posBelow)) return;

        if (tryFallingIntoHole(room, objBelow, posBelow)) return;

        if (tryDestroyObject(room, objBelow, posBelow)) return;

        handleLanding(room, posBelow);
    }

    private boolean tryFallingIntoBackground(Room room, GameObject objBelow, Point2D posBelow) {
        if (canFallThrough(objBelow)) {
            if (!this.isFalling()) onStartFall();

            room.moveObject(this, posBelow);
            return true;
        }
        return false;

        // Lógica geral de cair
//        if (objBelow.isFluid()) {
//            if (!this.falling) onStartFall();
//
//            room.moveObject(this, posBelow);
//            return true;
//        }
//        return false;
    }

    private boolean tryFallingIntoHole(Room room, GameObject objBelow, Point2D posBelow) {
        if (this.fitsInHoles() && objBelow.hasHole()) {
            room.moveObject(this, posBelow);
            return true;
        }
        return false;
    }

    private boolean tryDestroyObject(Room room, GameObject objBelow, Point2D posBelow) {
        if (objBelow instanceof Destroyable destroyable) {
            // Tenta destruir o objeto abaixo (ex: Trunk)
            if (destroyable.canBeDestroyedBy(this)) {
                destroyable.onDestroyed(room);
                // Nota: O objeto cai no próximo tick, não instantaneamente
            }
            return true;
        }
        return false;
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
    public boolean isPushable(GameCharacter gc) {
        return gc.canPush(this.getWeight());
    }

    protected boolean canFallThrough(GameObject objBelow) {
        return objBelow.isFluid();
    }

    @Override
    public int getLayer() {
        return super.LAYER_ITEMS; // para ficar visível por cima
    }
}