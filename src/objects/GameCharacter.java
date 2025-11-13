package objects;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends GameObject {
    private Direction currentDirection;

    public GameCharacter(Point2D p) {
		super(p);
        currentDirection = Direction.LEFT;
	}


	public void move(Vector2D dir) {
        Point2D destination = getNextPosition(dir);
        updateCurrentDirection(super.getPosition(), destination);
		setPosition(destination);
	}

    public Point2D getNextPosition(Vector2D dir) {
        Point2D currentPosition = super.getPosition();
		return currentPosition.plus(dir);
    }
    private void updateCurrentDirection(Point2D a, Point2D b) {
        Vector2D vector2D = Vector2D.movementVector(a, b);
        Direction potentialDir = Direction.forVector(vector2D);
        if (potentialDir == Direction.LEFT || potentialDir == Direction.RIGHT)
            this.currentDirection = potentialDir;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

	@Override
	public int getLayer() {
		return 2;
	}
	
}