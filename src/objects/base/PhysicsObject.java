package objects.base;

import interfaces.Movable;
import interfaces.markerInterfaces.NonBlocking;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class PhysicsObject extends GameObject implements Movable {

    // sinking floating etc.
    private boolean isMoving = false;

    public PhysicsObject(Point2D point) {
        super(point);
    }

    // Comportamento partilhado da Movable Interface
    @Override
    public void onStartMovement() {
        this.isMoving = true;
    }

    @Override
    public boolean isMoving() {
        return this.isMoving;
    }

    @Override
    public void onFinishedMovement(Room room, Point2D landedOn) {
        // comportamento base, não acontece nada
        this.isMoving = false;
    }

    @Override
    public boolean canBePushedBy(GameCharacter character, Direction direction) {
        return character.canPush(this.getWeight());
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        Vector2D moveVec = Vector2D.movementVector(from, to);
        Direction dir = Direction.forVector(moveVec);


        GameCharacter gc = room.getCurrentGameCharacter();
        GameObject objInNextPos = room.getGrid().getAt(to);

        if (objInNextPos instanceof NonBlocking && canBePushedBy(gc, dir)) {
            room.moveObject(this, to);
            return true;
        }
        return false;
    }

    @Override
    public int getLayer() {
        return super.LAYER_ITEMS;
    }

    protected boolean tryMovingIntoBackground(Room room, GameObject objBelow, Point2D posBelow) {
        if (objBelow instanceof NonBlocking) {
            if (!this.isMoving()) onStartMovement();

            room.moveObject(this, posBelow);
            return true;
        }
        return false;
    }

    protected void handleLanding(Room room, Point2D posBelow) {
        if (isMoving()) {
            onFinishedMovement(room, posBelow);
        }
    }
}