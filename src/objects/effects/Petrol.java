package objects.effects;

import interfaces.Deadly;
import objects.attributes.Size;
import objects.base.GameCharacter;
import objects.base.GameObject;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class Petrol extends GameObject implements Deadly {

    public Petrol(Point2D position) {
        super(position);
    }

    @Override
    public String getName() {
        return "petrol";
    }

    @Override
    public int getLayer() {
        return super.LAYER_EFFECTS;
    }

    @Override
    public void onCharacterContact(GameCharacter character, Room room) {
        if (character.getSize() == Size.BIG) {
            room.killGameCharacter(List.of(character), false);
        } else if(character.getSize() == Size.SMALL) {
            room.moveObject(character, this.getPosition());
            character.setBlocked(true);
        }
    }
}
