package interfaces;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public interface GravityAffected {
    void onStartFall();
    void onLanded(Room room, Point2D landedOn);
    boolean isFalling();
}
