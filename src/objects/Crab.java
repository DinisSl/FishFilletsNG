package objects;

import interfaces.*;
import interfaces.markerInterfaces.FitsInHole;
import interfaces.markerInterfaces.NonBlocking;
import interfaces.markerInterfaces.Passable;
import objects.attributes.Size;
import objects.attributes.Weight;
import objects.base.GameCharacter;
import objects.base.GameObject;
import objects.base.SinkingObject;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class Crab extends SinkingObject implements Deadly, FitsInHole, Destroyable {

    private int lastTurnMoved = -1;

    public Crab(Point2D point) {
        super(point);
    }

    @Override
    public void update(Room room) {
        super.update(room);

        // Se estiver a cair não se mexe horizontalmente
        if(isMoving()) return;

        // Verificar se os Game Characters se mexeram (Turno mudou)
        if (room.getTurnCount() > this.lastTurnMoved) {
            // Se for true mexe o crab
            moveRandomly(room);
            this.lastTurnMoved = room.getTurnCount();
        }

    }

    // Deadly Interface
    @Override
    public void onCharacterContact(GameCharacter character, Room room) {
        if (character.getSize() == Size.BIG) {
            room.removeObject(this);
        } else if (character.getSize() == Size.SMALL) {
            room.killGameCharacter(List.of(character), false);
        }
    }

    // Destroyable Interface
    @Override
    public void onDestroyed(Room room) {
        room.removeObject(this);
    }

    @Override
    public boolean canBeDestroyedBy(SinkingObject object) {
        return object instanceof Deadly;
    }

    // Movable Interface
    @Override
    public void onFinishedMovement(Room room, Point2D landedOn) {
        // Não acontece nada quando cai
    }

    @Override
    public boolean canBePushedBy(GameCharacter character, Direction direction) {
        return false;
    }

    @Override
    public Weight getWeight() {
        return Weight.LIGHT;
    }

    // ImageTile Interface
    @Override
    public String getName() {
        return "krab";
    }

    public void moveRandomly(Room room) {
        // Decide para que lado o crab se vai mover
        Direction dir = Math.random() < 0.5 ? Direction.LEFT : Direction.RIGHT;
        Point2D nextPos = getPosition().plus(dir.asVector());

        // Se essa posição estiver fora do mapa não faz nada
        if (!room.getGrid().isInBounds(nextPos)) return;

        GameObject objNextPos = room.getGrid().getAt(nextPos);

        // Se sair da room já não volta
        if (objNextPos == null) room.removeObject(this);

        handleInteraction(room, objNextPos, nextPos);
    }

    private void handleInteraction(Room room, GameObject objNextPos, Point2D nextPos) {
        // Vê se esse objeto é um Game Character
        if (objNextPos instanceof GameCharacter gc) {
            // Se for um Game Character grande morre
            if (gc.getSize() == Size.BIG) {
                room.removeObject(this);
                // Se for um Game Character pequeno mata
            } else if (gc.getSize() == Size.SMALL) {
                gc.checkDeadlyCollision(this, room);
            }
            return;
        }

        // Se for deadly mata (Trap)
        if (objNextPos instanceof Deadly) {
            room.removeObject(this);
            return;
        }

        // Vê se pode passar para o próximo ponto
        // Se puder passa
        if ((objNextPos instanceof NonBlocking) || (objNextPos instanceof Passable))
            room.moveObject(this, nextPos);
    }
}
