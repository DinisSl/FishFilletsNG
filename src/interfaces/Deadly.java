package interfaces;

import objects.base.GameCharacter;
import pt.iscte.poo.game.Room;

public interface Deadly {
    void onCharacterContact(GameCharacter character, Room room);
}
