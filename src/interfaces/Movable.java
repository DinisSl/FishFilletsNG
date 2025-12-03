package interfaces;

import objects.management.GameCharacter;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public interface Movable {
    void onStartMovement();
    void onFinishedMovement(Room room, Point2D landedOn);
    boolean isMoving();

    boolean canBePushedBy(GameCharacter character, Direction direction);
    boolean push(Room room, Point2D from, Point2D to);
    Weight getWeight();
}
