package objects.obstacles;

import objects.base.GameObject;
import pt.iscte.poo.utils.Point2D;

public class SteelBeam extends GameObject {
//    Guarda se o SteelBeam é vertical ou horizontal
    private final boolean isVertical;

    public SteelBeam(Point2D p, boolean isVertical) {
        super(p);
        this.isVertical = isVertical;
    }
//    Se o SteelBeam for vertical devolve steelVertical se não devolve steelHorizontal
    @Override
    public String getName() {
        if (this.isVertical)
            return "steelVertical";
        return "steelHorizontal"; }
    @Override
    public int getLayer() { return super.LAYER_OBSTACLES; }

    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return true;
    }
}
