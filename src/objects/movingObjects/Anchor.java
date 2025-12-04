package objects.movingObjects;

import objects.base.SinkingObject;
import objects.base.GameCharacter;
import objects.attributes.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;


public class Anchor extends SinkingObject {
    boolean pushedOnce;

    public Anchor(Point2D p) {
        super(p);
        pushedOnce = false;
    }

    @Override
    public String getName() {
        return "anchor";
    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }


    @Override
    public boolean canBePushedBy(GameCharacter character, Direction direction) {
        if (direction.isVertical()) return false;
        return character.canPush(this.getWeight()) && !this.pushedOnce;
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        Vector2D v = Vector2D.movementVector(from, to);
        Direction nextDir = Direction.forVector(v);

        if (nextDir.isVertical())
            return false;

        // Só pode ser puxada uma vez
        if (this.pushedOnce)
            return false;

        boolean succesfullPush = super.push(room, from, to);

        // Se se mexeu altera a flag
        if (succesfullPush)
            this.pushedOnce = true;

        return succesfullPush;
    }
}