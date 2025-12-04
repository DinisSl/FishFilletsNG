package objects.obstacles;

import interfaces.*;
import objects.base.SinkingObject;
import objects.base.GameCharacter;
import objects.base.GameObject;
import objects.attributes.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class Trap extends SinkingObject implements Deadly {

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

        if (destObj instanceof Destroyable destroyable) {
            /*Verifica se este Sinking Object é pesado o suficiente para
            destruir 'destroyable'*/

            if (destroyable.canBeDestroyedBy(this))
                // Destrói o destroyable
                destroyable.onDestroyed(room);
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

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }
}