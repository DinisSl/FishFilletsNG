package interfaces;

import objects.management.GameCharacter;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public interface Movable {
    void onStartFall();
    void onLanded(Room room, Point2D landedOn);
    boolean isFalling();

    boolean canBePushedBy(GameCharacter character);
    boolean push(Room room, Point2D from, Point2D to);
    Weight getWeight();
}
