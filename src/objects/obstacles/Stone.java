package objects.obstacles;

import interfaces.Destroyable;
import interfaces.NonBlocking;
import interfaces.Movable;
import objects.base.SinkingObject;
import objects.base.GameCharacter;
import objects.base.GameObject;
import objects.attributes.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Stone extends SinkingObject implements Movable {

    public Stone(Point2D p) {
        super(p);
    }

    @Override
    public void onFinishedMovement(Room room, Point2D landedOn) {
        GameObject destObj = room.getGrid().getAt(landedOn);

        if (destObj instanceof Destroyable destroyable) {
            /*Verifica se este Sinking Object é pesado o suficiente para
            destruir 'destroyable'*/

            if (destroyable.canBeDestroyedBy(this))
                // Destrói o destroyable
                destroyable.onDestroyed(room);
        }
    }

//   ImageTile interface
    @Override
    public String getName() {
        return "stone";
    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }

    @Override
    public boolean canBePushedBy(GameCharacter character, Direction direction) {
        return character.canPush(this.getWeight());
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        GameCharacter gc = room.getCurrentGameCharacter();
        GameObject objInNextPos = room.getGrid().getAt(to);
        if (objInNextPos instanceof NonBlocking && gc.canPush(Weight.HEAVY)) {
            room.moveObject(this, to);
            return true;
        }
        return false;
    }
}