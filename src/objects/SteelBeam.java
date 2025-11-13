package objects;

import pt.iscte.poo.utils.Point2D;

public class SteelBeam extends GameObject{
    private final boolean isVertical;
    public SteelBeam(Point2D p, boolean isVertical) {
        super(p);
        this.isVertical = isVertical;
    }

    @Override
    public String getName() {
        if (this.isVertical)
            return "steelVertical";
        return "steelHorizontal"; }
    @Override
    public int getLayer() { return 1; }

    @Override
    public boolean blocksMovement(GameCharacter gameCharacter) {
        return true;
    }
}
