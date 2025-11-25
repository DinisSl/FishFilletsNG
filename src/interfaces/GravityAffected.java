package interfaces;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public interface GravityAffected {
    void onStartFall(Point2D from);
    void onLanded(Room room, Point2D landedOn);
    boolean isFalling();
}
