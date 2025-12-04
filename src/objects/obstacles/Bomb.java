package objects.obstacles;

import interfaces.NonBlocking;
import objects.base.SinkingObject;
import objects.base.GameCharacter;
import objects.base.GameObject;
import objects.attributes.Weight;
import objects.effects.Explosion;
import objects.enviroment.Water;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Bomb extends SinkingObject {
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
    public boolean canBePushedBy(GameCharacter character, Direction direction) {
        if (direction.isHorizontal())
            return character.canPush(this.getWeight());

        return false;
    }

    /**
     * Tenta empurrar a bomba de uma posição para outra.
     *
     * Uma bomba só pode ser empurrada horizontalmente (LEFT ou RIGHT).
     *
     * Obtemos o objeto na posição para a qual a bomba está a ser
     * empurrada e o objeto por baixo da bomba.
     *
     * A bomba só pode ser empurrada se o próximo Point2D estiver vazio
     *
     * Se o objeto por baixo for um Game Character atualiza a originalY para não explodir
     *
     * Por fim move a bomba e devolve true
     *
     * @param room a sala respetiva
     * @param from a posição atual da bomba
     * @param to a posição de destino para onde a bomba será empurrada
     * @return "true" se a bomba foi empurrada com sucesso, "false" se não foi
     */
    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
//        System.out.println("PUSH()");
        GameObject objInNextPos = room.getGrid().getAt(to);

        Point2D posObjBelow = from.plus(Direction.DOWN.asVector());
        GameObject objBelow = room.getGrid().getAt(posObjBelow);

        if (!(objInNextPos instanceof NonBlocking)) return false;

        if (objBelow instanceof GameCharacter)
            this.originalY = objInNextPos.getPosition().getY();

        room.moveObject(this, to);

        return true;
    }

    @Override
    public void onFinishedMovement(Room room, Point2D landedOn) {
        GameObject objBelow = room.getGrid().getAt(landedOn);
        if (this.getPosition().getY() != this.originalY) {
            if (!(objBelow instanceof GameCharacter)) {
                room.removeObject(this);
                List<Point2D> boomBoomPoints = this.getPosition().getNeighbourhoodPoints();
                explodePoints(boomBoomPoints, room);
            }
        }
    }

    public void explodePoints(List<Point2D> points, Room room) {
        List<GameCharacter> toKill = new ArrayList<>();

        for (Point2D p : points) {
            GameObject original = room.getGrid().getAt(p);

            // Se a explosão for em cima da água não a removemos
            if (!(original instanceof Water)) {
                /*Se um Game Character morreu adicionamo-lo à lista para matar
                Se não for um Game Character apenas removemos o Game Object*/
                if (original instanceof GameCharacter gc) {
                    toKill.add(gc);
                } else {
                    room.removeObject(original);
                }
            }
            room.addObject(new Explosion(p, System.currentTimeMillis()));
        }

        ImageGUI.getInstance().update();

         /*Se pelo menos um Game Character morreu devido à bomba mata
        esse/s Game Character/s*/
        if (!toKill.isEmpty()) {
            room.killGameCharacter(toKill, true);
        }
    }
}