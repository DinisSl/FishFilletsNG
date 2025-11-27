package objects;

import interfaces.Movable;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.List;

public class Bomb extends FallingObject implements Movable {
    private int originalY;


    public Bomb(Point2D p) {
        super(p);
        this.originalY = p.getY();
    }

    @Override
    public String getName() {
        return "bomb";
    }

    @Override
    public Weight getWeight() {
        return Weight.LIGHT;
    }
    
    @Override
    public boolean canBePushedBy(GameCharacter character) {
        return true;
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        // OBTEMOS A DIREÇÃO EM QUE O GAME CHARACTER EMPURROU O GAME OBJECT
        Vector2D objMovVector = Vector2D.movementVector(from, to);
        Direction possibleObjDir = Direction.forVector(objMovVector);

        // SE A DIREÇÃO FOR UP OU DOWN DEVOLVE FALSO, POIS UMA BOMBA NÃO PODE SER
        // EMPURRADA PARA CIMA OU PARA BAIXO
        if (possibleObjDir == Direction.UP || possibleObjDir == Direction.DOWN)
            return false;

        // OBTEMOS O OBJETO NA PRÓXIMA POSIÇÃO
        GameObject objInNextPos = room.getGameObject(to);

        // VERIFICAMOS SE O OBJETO QUE DE MOMENTO SUPORTA A BOMBA É
        // UM GAME CHARACTER
        Point2D posObjBelow = from.plus(Direction.DOWN.asVector());
        GameObject objBelow = room.getGameObject(posObjBelow);

        // SE A BOMBA ESTIVER SUPORTADA POR UM GAME CHARACTER OU A
        // POSIÇÃO PARA A QUAL ELA VAI SER EMPURRADA SÓ CONTEM ÁGUA
        if (objInNextPos instanceof Water || objBelow instanceof GameCharacter) {
            // SE ESTIVER A ESR SUPORTADA POR UM GAME CHARACTER
            // ATUALIZAMOS A originalY PARA A CAMADA ATUAL
            if (objBelow instanceof GameCharacter)
                this.originalY = objInNextPos.getPosition().getY();
            // DEPOIS MOVEMOS O OBJETO PARA ONDE ESTÁ A SER EMPURRADO
            room.moveObject(this, to);
            return true;
        }
        return false;
    }

    @Override
    public void onLanded(Room room, Point2D landedOn) {
        GameObject objBelow = room.getGameObject(landedOn);
        if (this.getPosition().getY() != this.originalY) {
            if (!(objBelow instanceof GameCharacter)) {
                room.removeObject(this);
                List<Point2D> boomBoomPoints = this.getPosition().getNeighbourhoodPoints();
                room.explodePoints(boomBoomPoints);
            }
        }
    }
}