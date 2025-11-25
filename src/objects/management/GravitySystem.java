package objects.management;

import objects.Blood;
import objects.Explosion;
import objects.Water;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class GravitySystem {

    public void update(Room room) {
        processFalling(room);
        cleanupExplosions(room);
    }

    private void processFalling(Room room) {
        List<FallingObject> fallingObjects =
                room.getGrid().listAllObjectsOfType(FallingObject.class);

        for (FallingObject fo : fallingObjects)
            tryToFallOneStep(fo, room);
    }

    private void tryToFallOneStep(FallingObject obj, Room room) {
        Point2D currPos = obj.getPosition();
        Point2D posBelow = currPos.plus(Direction.DOWN.asVector());

        if (room.getGrid().isInBounds(posBelow)) {
            obj.setFalling(false);
            room.removeObject(obj);
            return;
        }

        GameObject objBelow = room.getGrid().getAt(posBelow);

        if ((objBelow instanceof Water || objBelow instanceof Blood)) {
            obj.setFalling(true);
            room.getMovementSystem().moveObject(obj, posBelow);
        } else {
            obj.setFalling(false);
            obj.onLand(room, currPos, posBelow);
        }
    }

    private void cleanupExplosions(Room room) {
        List<Explosion> explosions = room.getGrid().listAllObjectsOfType(Explosion.class);
        long currTime = System.currentTimeMillis();
        for (Explosion ex : explosions) {
            if (ex.getStartTime() + 1500 <= currTime)
                room.removeObject(ex);
        }
    }
}
