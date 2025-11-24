package objects;

import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.List;

public class Bomb extends FallingObject {
    private int originalY;


    public Bomb(Point2D p) {
        super(p);
        this.originalY = p.getY();
    }

    @Override
    public boolean moveIfPossible(Room room, Point2D currPosObj, Point2D nextObjPos) {
        // OBTEMOS A DIREÇÃO EM QUE O GAME CHARACTER EMPURROU O GAME OBJECT
        Vector2D objMovVector = Vector2D.movementVector(currPosObj, nextObjPos);
        Direction possibleObjDir = Direction.forVector(objMovVector);

        // SE A DIREÇÃO FOR UP OU DOWN DEVOLVE FALSO, POIS UMA BOMBA NÃO PODE SER
        // EMPURRADA PARA CIMA OU PARA BAIXO
        if (possibleObjDir == Direction.UP || possibleObjDir == Direction.DOWN)
            return false;

        // OBTEMOS O OBJETO NA PRÓXIMA POSIÇÃO
        GameObject objInNextPos = room.getGameObject(nextObjPos);

        // VERIFICAMOS SE O OBJETO QUE DE MOMENTO SUPORTA A BOMBA É
        // UM GAME CHARACTER
        Point2D posObjBelow = currPosObj.plus(Direction.DOWN.asVector());
        GameObject objBelow = room.getGameObject(posObjBelow);

        // SE A BOMBA ESTIVER SUPORTADA POR UM GAME CHARACTER OU A
        // POSIÇÃO PARA A QUAL ELA VAI SER EMPURRADA SÒ CONTEM ÁGUA
        if (objInNextPos instanceof Water || objBelow instanceof GameCharacter) {
            // SE ESTIVER A ESR SUPORTADA POR UM GAME CHARACTER
            // ATUALIZAMOS A originalY PARA A CAMADA ATUAL
            if (objBelow instanceof GameCharacter)
                this.originalY = objInNextPos.getPosition().getY();
            // DEPOIS MOVEMOS O OBJETO PARA ONDE ESTÁ A SER EMPURRADO
            room.getMovementSystem().moveObject(room, this, nextObjPos);
            return true;
        }
        return false;
    }

    @Override
    public void onLand(Room room, Point2D currPos, Point2D posBelow) {
        GameObject objBelow = room.getGameObject(posBelow);

        if (room.getGameObject(currPos).getPosition().getY() != this.originalY) {
            if (!(objBelow instanceof GameCharacter)) {
                room.removeObject(this);
                List<Point2D> boomBoomPoints = currPos.getNeighbourhoodPoints();
                room.explodePoints(boomBoomPoints);
            }
        }
    }

    @Override
    public String getName() {
        return "bomb";
    }

    @Override
    public Weight getWeight() {
        return Weight.LIGHT;
    }

}