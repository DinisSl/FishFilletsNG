package objects.management;

import interfaces.GravityAffected;
import pt.iscte.poo.utils.Point2D;

public abstract class FallingObject extends GameObject implements GravityAffected {
    private boolean falling;

    public FallingObject(Point2D point) {
        super(point);
        this.falling = false;
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
