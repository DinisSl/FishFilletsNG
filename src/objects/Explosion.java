package objects;

import objects.management.GameCharacter;
import objects.management.GameObject;
import pt.iscte.poo.utils.Point2D;

public class Explosion extends GameObject {
    long startTime;

    public Explosion(Point2D p, long startTime) {
        super(p);
        this.startTime = startTime;
    }
    public long getStartTime() {
        return this.startTime;
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
        return 1;
    }
}
