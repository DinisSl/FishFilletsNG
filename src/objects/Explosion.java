package objects;

import interfaces.Fluid;
import objects.management.GameObject;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Explosion extends GameObject implements Fluid {
    long startTime;

    public Explosion(Point2D p, long startTime) {
        super(p);
        this.startTime = startTime;
    }

    // --- NOVA IMPLEMENTAÇÃO ---
    @Override
    public void update(Room room) {
        long currTime = System.currentTimeMillis();
        // Se passou 1500ms, remove-se a si própria
        if (this.startTime + 1500 <= currTime) {
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
