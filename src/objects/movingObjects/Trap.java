package objects.movingObjects;

import interfaces.*;
import interfaces.markerInterfaces.FitsInHole;
import objects.base.SinkingObject;
import objects.base.GameCharacter;
import objects.base.GameObject;
import objects.attributes.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class Trap extends SinkingObject implements Deadly {

    public Trap(Point2D p) { super(p); }

    @Override
    public String getName() { return "trap"; }

    @Override
    public Weight getWeight() { return Weight.HEAVY; }

    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return !(gameCharacter instanceof FitsInHole);
    }

    @Override
    public void onCharacterContact(GameCharacter character, Room room) {
        if (!(character instanceof FitsInHole))
            room.killGameCharacter(List.of(character), false);
    }
}