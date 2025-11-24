package objects;

import objects.management.FallingObject;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Trap extends FallingObject {

    public Trap(Point2D p) {
        super(p);
    }

    @Override
    public boolean moveIfPossible(Room room, Point2D currPos, Point2D whereToGo) {
        return false;
    }

    @Override
    public String getName() {
        return "trap";
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        // Pequeno passa não bloqueia
        // Grande bloqueia e morre (morte tratada no engine
        return (gameCharacter instanceof BigFish);
    }

    @Override
    public void onLand(Room room, Point2D currPos, Point2D posBelow) {
    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }
}