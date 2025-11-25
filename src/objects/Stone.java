package objects;

import interfaces.Pushable;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Stone extends FallingObject implements Pushable {

    public Stone(Point2D p) {
        super(p);
    }

    @Override
    public void onLanded(Room room, Point2D landedOn) {
        GameObject destObj = room.getGameObject(landedOn);
        List<GameCharacter> toKill = new ArrayList<>();
        if (destObj instanceof SmallFish sf) {
            toKill.add(sf);
            room.killGameCharacter(toKill, false);
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
}