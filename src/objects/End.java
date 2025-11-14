package objects;

import pt.iscte.poo.utils.Point2D;

//Esta classe serve para controlar o comportamento do jogo
// quando um peixe acaba a Room, ou seja, passa pelo buraco na Wall
public class End extends GameObject{
    public End(Point2D p) {
        super(p);
    }

    @Override
    public String getName() {
        return "End";
    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public boolean blocksMovement(GameCharacter gameCharacter) {
        return true;
    }
}
