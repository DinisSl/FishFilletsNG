package interfaces;

import objects.management.FallingObject;
import pt.iscte.poo.game.Room;

public interface Destroyable {
    void onDestroyed(Room room);
    boolean canBeDestroyedBy(FallingObject object);
}
