package objects.management;

import objects.Blood;
import objects.Bomb;
import objects.Explosion;
import objects.Water;
import pt.iscte.poo.game.Board;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class GravitySystem {

    public void update(Room room, Board board) {
        processFalling(room, board);
        cleanupExplosions(room);
    }

    private void processFalling(Room room, Board board) {
        for (int y = 9; y >= 0; y--) {
            for (int x = 0; x < 10; x++) {
                GameObject obj = board.getAt(new Point2D(x, y));

                if (obj instanceof FallingObject) {
                    tryToFallOneStep((FallingObject) obj, room, board);
                }
            }
        }
    }

//    private void processFalling(Room room) {
//        // All falling object logic here
//        for (FallingObject obj : room.getBoard().FallingObjects()) {
//            tryToFallOneStep(obj, room);
//        }
//    }

    private void tryToFallOneStep(FallingObject obj, Room room, Board board) {
        Point2D currPos = obj.getPosition();
        Point2D posBelow = currPos.plus(Direction.DOWN.asVector());

        if (!board.isInBounds(posBelow)) {
            obj.setFalling(false);
            room.removeObject(obj);
            return;
        }

        GameObject objBelow = board.getAt(posBelow);

        if ((objBelow instanceof Water || objBelow instanceof Blood)) {
            obj.setFalling(true);
            room.moveObject(obj, posBelow);
        } else {
            obj.setFalling(false);
            obj.onLand(room, currPos, posBelow);
        }
    }

    private void cleanupExplosions(Room room) {
        List<Explosion> explosions = room.getBoard().explosions();
        long currTime = System.currentTimeMillis();
        for (Explosion ex : explosions) {
            if (ex.getStartTime() + 1500 <= currTime)
                room.removeObject(ex);
        }
    }
}
