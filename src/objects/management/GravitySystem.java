package objects.management;

import interfaces.Destroyable;
import objects.Blood;
import objects.Explosion;
import objects.Water;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class GravitySystem {
    private final Room room;

    public GravitySystem(Room room) {
        this.room = room;
    }
    public void update() {
        processFalling();
        cleanupExplosions();
        checkOverloadedGC();
    }

    public void checkOverloadedGC() {
        countFallingObjectsAboveGC();
        List<GameCharacter> activeGC = this.room.getActiveGCCopy();

        for (GameCharacter gc : activeGC) {
            gc.checkSupportOverload(this.room);
        }
    }

    public void countFallingObjectsAboveGC() {
        List<GameCharacter> activeGC = this.room.getActiveGCCopy();

        for (GameCharacter gc : activeGC) {
            List<GameObject> objsAbove = this.room.getGrid().allObjectsAbove(gc.getPosition());
            for (GameObject go : objsAbove) {
                if (go instanceof FallingObject fo) {
                    gc.addSupportedObject(fo);
                } else {
                    break;
                }
            }
        }
    }

    private void processFalling() {
        List<FallingObject> fallingObjects =
                room.getGrid().listAllObjectsOfType(FallingObject.class);

        for (FallingObject fo : fallingObjects)
            tryToFallOneStep(fo);
    }

    private void tryToFallOneStep(FallingObject obj) {
        Point2D currPos = obj.getPosition();
        Point2D posBelow = currPos.plus(Direction.DOWN.asVector());

        if (!room.getGrid().isInBounds(posBelow)) {
            room.removeObject(obj);
            return;
        }

        GameObject objBelow = room.getGrid().getAt(posBelow);

        if ((objBelow instanceof Water || objBelow instanceof Blood)) {
            if (!obj.isFalling())
                obj.onStartFall(currPos);
            room.getMovementSystem().moveObject(obj, posBelow);
        } else if (objBelow instanceof Destroyable destroyable){
            if (destroyable.canBeDestroyedBy(obj))
                destroyable.onDestroyed(room);
        } else {
            obj.onLanded(room, posBelow);
        }
    }

    private void cleanupExplosions() {
        List<Explosion> explosions = room.getGrid().listAllObjectsOfType(Explosion.class);
        long currTime = System.currentTimeMillis();
        for (Explosion ex : explosions) {
            if (ex.getStartTime() + 1500 <= currTime)
                room.removeObject(ex);
        }
    }


}
