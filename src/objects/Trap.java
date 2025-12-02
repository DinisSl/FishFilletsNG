package objects;

import interfaces.Deadly;
import interfaces.Movable;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
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
        return !gameCharacter.fitsInHoles();
    }

    @Override
    public void onLanded(Room room, Point2D landedOn) {
        GameObject destObj = room.getGrid().getAt(landedOn);

        if (destObj.fitsInHoles())
            return;

        if (destObj.canBeCrushed())
            destObj.onCrushed(room);
    }

    @Override
    public void onCharacterContact(GameCharacter character, Room room) {
        if (!character.fitsInHoles())
            room.killGameCharacter(List.of(character), false);
    }

    @Override
    public boolean canBePushedBy(GameCharacter character) {
        return character.canPush(this.getWeight());
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        GameCharacter gc = room.getCurrentGameCharacter();
        GameObject objInNextPos = room.getGrid().getAt(to);
        if (objInNextPos.isFluid() && gc.canPush(Weight.HEAVY)) {
            room.moveObject(this, to);
            return true;
        }
        return false;
    }

    @Override
    protected boolean canFallThrough(GameObject objBelow) {
        if (super.canFallThrough(objBelow)) return true;

        return objBelow.fitsInHoles();
    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }
}