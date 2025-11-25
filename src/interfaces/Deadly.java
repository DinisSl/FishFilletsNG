package interfaces;

import objects.management.GameCharacter;
import pt.iscte.poo.game.Room;

public interface Deadly {
    void onCharacterContact(GameCharacter character, Room room);
}
