package objects.effects;

import interfaces.markerInterfaces.NonBlocking;
import objects.base.GameObject;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Explosion extends GameObject implements NonBlocking {
    private static final int EXPLOSION_DURATION_MS = 1500;
    long startTime;

    public Explosion(Point2D p, long startTime) {
        super(p);
        this.startTime = startTime;
    }

    @Override
    public void update(Room room) {
        long currTime = System.currentTimeMillis();
        // Se passou EXPLOSION_DURATION_MS, remove-se a si própria
        if (this.startTime + EXPLOSION_DURATION_MS <= currTime) {
            room.removeObject(this);
        }
    }

    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return false;
    }

    @Override
    public String getName() {
        return "explosion";
    }

    @Override
    public int getLayer() {
        return super.LAYER_EFFECTS;
    }

}
