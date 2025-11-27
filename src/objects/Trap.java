package objects;

import interfaces.Deadly;
import interfaces.Pushable;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class Trap extends FallingObject implements Deadly, Pushable {

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
        return (gameCharacter instanceof BigFish);
    }

    @Override
    public void onLanded(Room room, Point2D posBelow) {
        // Não acontece nada
    }

    @Override
    public void onCharacterContact(GameCharacter character, Room room) {
        if (character instanceof BigFish)
            room.killGameCharacter(List.of(character), false);
    }

    @Override
    public boolean canBePushedBy(GameCharacter character) {
        return character instanceof BigFish;
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        GameCharacter gc = room.getCurrentGameCharacter();
        GameObject objInNextPos = room.getGameObject(to);
        if (objInNextPos instanceof Water && gc instanceof BigFish) {
            room.getMovementSystem().moveObject(this, to);
            return true;
        }
        return false;
    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }
}