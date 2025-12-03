package interfaces;

import pt.iscte.poo.game.Room;

public interface LoadBearer {
    boolean isOverloaded(int heavyCount, int lightCount);
    void onOverload(Room room);
}
