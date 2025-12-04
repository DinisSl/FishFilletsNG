package objects.obstacles;

import interfaces.Destroyable;
import interfaces.NonBlocking;
import objects.base.SinkingObject;
import objects.base.GameCharacter;
import objects.base.GameObject;
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
    public void onFinishedMovement(Room room, Point2D posBelow) {
        GameObject destObj = room.getGrid().getAt(posBelow);

        if (destObj instanceof Destroyable destroyable) {
            /*Verifica se este Sinking Object é pesado o suficiente para
            destruir 'destroyable'*/

            if (destroyable.canBeDestroyedBy(this))
                // Destrói o destroyable
                destroyable.onDestroyed(room);
        }
    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }


    @Override
    public boolean canBePushedBy(GameCharacter character, Direction direction) {
        return character.canPush(this.getWeight()) && !this.pushedOnce;
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        GameCharacter gc = room.getCurrentGameCharacter();
        GameObject objInNextPos = room.getGrid().getAt(to);

        Vector2D v = Vector2D.movementVector(from, to);
        Direction nextDir = Direction.forVector(v);
        if (nextDir == Direction.UP || nextDir == Direction.DOWN)
            return false;

        if (objInNextPos instanceof NonBlocking && gc.canPush(Weight.HEAVY) && !this.pushedOnce) {
            room.moveObject(this, to);
            this.pushedOnce = true;
            return true;
        }
        return false;
    }
}