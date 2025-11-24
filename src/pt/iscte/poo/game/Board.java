package pt.iscte.poo.game;

import objects.Explosion;
import objects.Water;
import objects.management.FallingObject;
import objects.management.GameObject;
import pt.iscte.poo.utils.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private final int WIDTH = 10;
    private final int LENGTH = 10;
    private Map<Point2D, List<GameObject>> board;

    public Board() {
        this.board = new HashMap<>();
    }

    public void initializeArrayOfList() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < LENGTH; y++) {
                Point2D p = new Point2D(x, y);
                board.put(p, new ArrayList<>());
            }
        }
    }

    public GameObject getAt(Point2D position) {
        if (!isInBounds(position))
            return null;

        List<GameObject> objsInPos = this.board.get(position);
        GameObject gameObjectFinal = null;
        for(GameObject gameObject : objsInPos) {
//           Procura o gameObjectFinal que tem a maior camada no point2D
            if (gameObjectFinal == null || gameObject.getLayer() > gameObjectFinal.getLayer())
                gameObjectFinal = gameObject;

        }
        return gameObjectFinal;
    }

    public void removeAt(GameObject obj) {
        Point2D p = obj.getPosition();
        List<GameObject> list = this.board.get(p);
        if (!(obj instanceof Water)){
            list.remove(obj);
        }
    }

    public void setAt(GameObject obj) {
        int x = obj.getPosition().getX();
        int y = obj.getPosition().getY();
        Point2D p = new Point2D(x, y);
        this.board.computeIfAbsent(p, k -> new ArrayList<>()).add(obj);
    }

    public boolean isInBounds(Point2D p) {
        int x = p.getX();
        int y = p.getY();
        return !(x < 0 || x >= LENGTH || y < 0 || y >= WIDTH);
    }

    public List<Explosion> explosions() {
        List<Explosion> explosions = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < LENGTH; j++) {
                if (getAt(new Point2D(j, i)) instanceof Explosion ex) {
                    explosions.add(ex);
                }
            }
        }
        return explosions;
    }

    public List<FallingObject> FallingObjects() {
        List<FallingObject> fos = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < LENGTH; j++) {
                if (getAt(new Point2D(j, i)) instanceof FallingObject fo) {
                    fos.add(fo);
                }
            }
        }
        return fos;
    }
}
