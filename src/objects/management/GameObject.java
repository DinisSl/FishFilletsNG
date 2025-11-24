package objects.management;

import objects.*;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

public abstract class GameObject implements ImageTile{
	
	private Point2D position;

    public GameObject(Point2D position) {
		this.position = position;
	}
// POSITION
	public void setPosition(int i, int j) {
		position = new Point2D(i, j);
	}
	public void setPosition(Point2D position) {
		this.position = position;
	}
	@Override
	public Point2D getPosition() {
		return position;
	}

//    Metodo estático que devolve um GameObject passando um a letra do objeto como um char
    public static GameObject createGameObject(char character, Point2D point) {
        return switch (character) {
            case 'B' -> new BigFish(point);
            case 'S' -> new SmallFish(point);
            case 'W' -> new Wall(point);
            case 'H' -> new SteelBeam(point, false);
            case 'V' -> new SteelBeam(point, true);
            case 'X' -> new HoledWall(point);
            case 'R' -> new Stone(point);
            case 'b' -> new Bomb(point);
            case 'T' -> new Trap(point);
            case 'C' -> new Cup(point);
            case 'A' -> new Anchor(point);
            case 'L' -> new Trunk(point);

            default ->
                throw new IllegalArgumentException("Character inválido: " + character + "na posição " + point);
        };
    }
//    Metodo abstrato que controla o que acontece a cada GameCharacter cada vez que bate num GameObject
    public abstract boolean blocksMovement(GameObject gameCharacter);
}
