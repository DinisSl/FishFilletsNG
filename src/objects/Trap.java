package objects;

import interfaces.*;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class Trap extends FallingObject implements Deadly, Movable {

    public Trap(Point2D p) {
        super(p);
    }

    @Override
    public String getName() {
        return "trap";
    }

    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        // Pequeno passa não bloqueia
        // Grande bloqueia e morre (morte tratada no engine
        return !(gameCharacter instanceof FitsInHole);
    }

    @Override
    public void onFinishedMovement(Room room, Point2D landedOn) {
        GameObject destObj = room.getGrid().getAt(landedOn);

        if (destObj instanceof FitsInHole)
            return;

        if (destObj instanceof Destroyable d) {
            // 1. Check if "this" falling object is heavy enough to destroy "d"
            if (d.canBeDestroyedBy(this)) {
                // 2. Perform the destruction
                d.onDestroyed(room);
            }
        }
    }

    @Override
    public void onCharacterContact(GameCharacter character, Room room) {
        if (!(character instanceof FitsInHole))
            room.killGameCharacter(List.of(character), false);
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

//    @Override
//    protected boolean canFallThrough(GameObject objBelow) {
//        if (objBelow instanceof NonBlocking) return true;
//
//        return objBelow instanceof FitsInHole;
//    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }
}