package objects.management;

import objects.*;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class MovementSystem {
    private final Room room;

    public MovementSystem(Room room) {
        this.room = room;
    }

    // MAIN METHOD HANDLES ALL THE MOVEMENT LOGIC
    public boolean handleMovement(int k) {
        Vector2D vector = Direction.directionFor(k).asVector();
        Point2D nextPointGC = room.getCurrentGameCharacter().getNextPosition(vector);
        GameObject nextGameObject = room.getGameObject(nextPointGC);

        if (nextGameObject == null)
            return handleExit();

        if (!handleTraps(nextGameObject))
            return false;

        if (!handlePushableObjs(nextGameObject, nextPointGC, vector))
            return false;

        moveGameCharacter(vector);
        return false;
    }


    // SUBMETHODS, ONLY CALLED BY handleMovement(int k, Room room){}
    // Este metodo só é chamado quando um peixe sai do jogo
    private boolean handleExit() {
        // Remove o currentGameCharacter da Room e da List
        room.removeObject(room.getCurrentGameCharacter());
        room.getActiveGC().remove(room.getCurrentGameCharacter());

        // Se a lista estiver vazia significa que o último GameCharacter
        // já passou logo já completou o nível e devolve true
        if (room.getActiveGC().isEmpty())
            return true;

        // Se ainda não passaram os GameCharacter todos então troca o peixe
        room.setCurrentGameCharacter(room.getActiveGC().getFirst());
        return false;
    }

    private boolean handleTraps(GameObject nextGameObject) {
        // TRAP LOGIC
        if (nextGameObject instanceof Trap && room.getCurrentGameCharacter() instanceof BigFish bf) {
            List<GameCharacter> toKill = new ArrayList<>();
            toKill.add(bf);
            room.killGameCharacter(toKill, false);
            return false; // PARAR A EXECUÇÃO IMEDIATAMENTE APÓS A MORTE
        }
        return true;
    }

    private boolean handlePushableObjs(GameObject nextGameObject, Point2D nextPointGC, Vector2D vector) {
        // PUSH OBJECT LOGIC
        if (nextGameObject.blocksMovement(room.getCurrentGameCharacter())) {
            // SE FOR UM GC A EMPURRAR UM OBJETO QUE BLOQUEIA A SUA PASSAGEM
            // VAI VER SE É UM OBJETO QUE PODE CAIR, OU SEJA, PODE SER EMPURRADO
            if (nextGameObject instanceof FallingObject fo) {
                // SE FOR POSSÍVEL MEXE O OBJETO E DEPOIS ESTA FUNÇÃO
                // MEXE O GC
                boolean canMove =
                        fo.moveIfPossible(
                                room,
                                nextPointGC,
                                nextPointGC.plus(vector)
                        );
                if (canMove) {
                    moveGameCharacter(vector);
                    return false;
                }
            }
            return false;
        }
        return true;
    }

    private void moveGameCharacter(Vector2D vector) {
        GameCharacter gc = room.getCurrentGameCharacter();
        room.removeObject(gc);
        gc.move(vector);
        room.addObject(gc);
    }

    public void moveObject(GameObject obj, Point2D newPos) {
        room.removeObject(obj);           // Remove from board + GUI
        obj.setPosition(newPos);     // Update object's position
        room.addObject(obj);              // Add to board + GUI at new position
    }
}
