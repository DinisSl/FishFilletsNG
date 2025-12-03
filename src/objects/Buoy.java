package objects;

import interfaces.Movable;
import interfaces.NonBlocking;
import objects.management.*;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class Buoy extends FloatingObject implements Movable {

    public Buoy(Point2D point) {
        super(point);
    }


    @Override
    public void update(Room room) {
        // 1. Identify objects on top to populate the supportedObjects list
        clearSupportedObjects();
        List<GameObject> objsAbove = room.getGrid().allObjectsAboveToSide(this.getPosition(), Direction.UP);

        for (GameObject go : objsAbove) {
            if (go instanceof FallingObject fo) {
                addSupportedObject(fo);
            } else {
                // If we hit a wall or something static, stop counting
                break;
            }
        }

        // 2. Check if overloaded
        int[] fallingObjects = super.checkSupportOverload(room);
        int heavyFO = fallingObjects[0];
        int lightFO = fallingObjects[1];

        if (isOverloaded(heavyFO, lightFO)) {
            // If overloaded, execute sink logic
            checkSupportOverloadAndExecute(room);
        } else {
            // If NOT overloaded, execute standard FloatingObject logic (Float Up)
            super.update(room);
        }
    }

    public void checkSupportOverloadAndExecute(Room room) {
        int[] fallingObjects = super.checkSupportOverload(room);
        int heavyFO = fallingObjects[0];
        int lightFO = fallingObjects[1];

        if (isOverloaded(heavyFO, lightFO)) {
            Point2D pointBelow = super.getPosition().plus(Direction.DOWN.asVector());

            if (!room.getGrid().isInBounds(pointBelow)) return;

            GameObject objBelow = room.getGrid().getAt(pointBelow);

            if (objBelow instanceof NonBlocking)
                room.moveObject(this, pointBelow);
        }
    }

    public boolean isOverloaded(int heavyFO, int lightFO){
        return heavyFO > 0 || lightFO > 0;
    }

    @Override
    public void onFinishedMovement(Room room, Point2D landedOn) {
        // Não acontece nada
    }

    @Override
    public boolean canBePushedBy(GameCharacter character, Direction direction) {
        if (character.getSize() == Size.SMALL) {
//            System.out.println(direction.isHorizontal());
            return direction.isHorizontal();
        }

//        System.out.println("TRUE");
        return true;
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        GameObject objInNextPos = room.getGrid().getAt(to);
        if (objInNextPos instanceof NonBlocking) {
            room.moveObject(this, to);
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "buoy";
    }

    @Override
    public Weight getWeight() {
        return Weight.LIGHT;
    }

}
