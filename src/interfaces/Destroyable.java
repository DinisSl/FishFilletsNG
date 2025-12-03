package interfaces;

import objects.base.SinkingObject;
import pt.iscte.poo.game.Room;

public interface Destroyable {
    void onDestroyed(Room room);
    boolean canBeDestroyedBy(SinkingObject object);
}
