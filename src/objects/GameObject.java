package objects;

import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Direction;
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

//    Metodo estatico que devolve um GameObject passando um a letra do objeto como um char
    public static GameObject createGameObject(char character, Point2D point2D) {
        return switch (character) {
            case 'B' -> new BigFish(point2D);
            case 'S' -> new SmallFish(point2D);
            case 'W' -> new Wall(point2D);
            case 'H' -> new SteelBeam(point2D, false);
            case 'V' -> new SteelBeam(point2D, true);
            case 'X' -> new HoledWall(point2D);

            default ->
                throw new IllegalArgumentException("Caractere inválido: " + character + " na posição " + point2D);
        };
    }
//    Metodo abstrato que controla o que acontece a cada GameCharacter cada vex que bate num GameObject
    public abstract boolean blocksMovement(GameCharacter gameCharacter);
}
