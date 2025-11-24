package interfaces;

import objects.management.GameObject;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public interface GravityAffected {
    void onStartFall(Point2D from);
    void setFalling(boolean falling);
    void onLand(Room room, Point2D currPos, Point2D posBelow);
    boolean isFalling();
}
