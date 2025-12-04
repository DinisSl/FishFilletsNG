package objects.obstacles;

import interfaces.Destroyable;
import interfaces.NonBlocking;
import interfaces.Passable;
import objects.Crab;
import objects.base.SinkingObject;
import objects.base.GameCharacter;
import objects.base.GameObject;
import objects.attributes.Weight;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Stone extends SinkingObject {

    private boolean hasCrabSpawned;

    public Stone(Point2D p) {
        super(p);
        this.hasCrabSpawned = false;
    }

    @Override
    public void onFinishedMovement(Room room, Point2D landedOn) {
        GameObject destObj = room.getGrid().getAt(landedOn);

        if (destObj instanceof Destroyable destroyable) {
            /*Verifica se este Sinking Object é pesado o suficiente para
            destruir 'destroyable'*/

            if (destroyable.canBeDestroyedBy(this))
                // Destrói o destroyable
                destroyable.onDestroyed(room);
        }
    }

//   ImageTile interface
    @Override
    public String getName() {
        return "stone";
    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
    }

    @Override
    public boolean canBePushedBy(GameCharacter character, Direction direction) {
        return character.canPush(this.getWeight());
    }

    @Override
    public boolean push(Room room, Point2D from, Point2D to) {
        GameCharacter gc = room.getCurrentGameCharacter();
        GameObject objInNextPos = room.getGrid().getAt(to);
        if (objInNextPos instanceof NonBlocking && gc.canPush(Weight.HEAVY)) {
            room.moveObject(this, to);

            // Lógica para da spawn ao Crab
            trySpawnCrab(room, from, to);

            return true;
        }
        return false;
    }

    private void trySpawnCrab(Room room, Point2D from, Point2D to) {
        if (hasCrabSpawned) return;

        // Verifica se o movimento foi horizontal
        Vector2D moveVector = Vector2D.movementVector(from, to);
        Direction moveDir = Direction.forVector(moveVector);
        if (!moveDir.isHorizontal()) return;

        // Verificar a posição acima da nova posição da pedra
        Point2D pointAbove = to.plus(Direction.UP.asVector());

        // Verifica se esse ponto está no mapa
        if (!room.getGrid().isInBounds(pointAbove)) return;

        GameObject objAbove = room.getGrid().getAt(pointAbove);

        // Se o objeto em cima permitir o crab spawnar ele dá spwan
        if (objAbove instanceof NonBlocking || objAbove instanceof Passable) {
            Crab crab = new Crab(pointAbove);
            room.addObject(crab);

            // Só permite a criação de um Crab
            hasCrabSpawned = true;
        }
    }
}