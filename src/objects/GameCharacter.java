package objects;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends GameObject {
//    Serve para guardar o lado para que o peixe está a olhar (RIGHT OU LEFT)
    private Direction currentDirection;

    public GameCharacter(Point2D p) {
		super(p);
//        Ele inicialmente está a olha para a esquerda
        currentDirection = Direction.LEFT;
	}


	public void move(Vector2D dir) {
//        Calcula o Point2D de destino do peixe
        Point2D destination = getNextPosition(dir);
        updateCurrentDirection(super.getPosition(), destination);
		setPosition(destination);
	}

    public Point2D getNextPosition(Vector2D dir) {
//        Calcula a próxima posição do peixe somando um Vector2D a um Point2D
        Point2D currentPosition = super.getPosition();
		return currentPosition.plus(dir);
    }
    private void updateCurrentDirection(Point2D a, Point2D b) {
//        Calcula um Vector2D da proxima posição do peixe
        Vector2D vector2D = Vector2D.movementVector(a, b);
//        Passa o Vector2D para uma Direction
        Direction potentialDir = Direction.forVector(vector2D);
//        Se a Direction for LEFT ou RIGHT atribui-a ao atributo currentDirection
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