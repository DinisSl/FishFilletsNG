package pt.iscte.poo.game;

import objects.base.GameObject;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grid {
    private final int WIDTH = 10;
    private final int LENGTH = 10;
    private final Map<Point2D, List<GameObject>> grid;

    public Grid() {
        // Inicializa o Hash Map
        this.grid = new HashMap<>();
    }

    // Verifica se um ponto está dentro da grid
    public boolean isInBounds(Point2D p) {
        int x = p.getX();
        int y = p.getY();
        return x >= 0 && x < LENGTH && y >= 0 && y < WIDTH;
    }

    // Inicializa o Hash Map com listas vazias nos respetivos pontos
    public void initializeHashMapOfLists() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < LENGTH; y++) {
                Point2D p = new Point2D(x, y);
                grid.put(p, new ArrayList<>());
            }
        }
    }

    /**
     * Metodo que recebe um ponto e devolve o Game Object que
     * tem a maior layer e que a Key é o Point2D
     * @param point Recebe um ponto do tipo Point2D que é a Key
     * do objeto.
     * @return GameObject
     */
    public GameObject getAt(Point2D point) {
        if (!isInBounds(point)) return null;

        List<GameObject> objsInPos = this.grid.get(point);
        GameObject gameObjectFinal = null;
        for (GameObject gameObject : objsInPos) {
            // Procura o gameObjectFinal que tem a maior camada no point2D
            if (gameObjectFinal == null || gameObject.getLayer() > gameObjectFinal.getLayer())
                gameObjectFinal = gameObject;

        }
        return gameObjectFinal;
    }

    public List<GameObject> getObjectsAt(Point2D point) {
        if (!isInBounds(point)) return new ArrayList<>();
        return this.grid.get(point);
    }

    public void setAt(GameObject obj) {
        Point2D p = obj.getPosition();
        List<GameObject> list = this.grid.get(p);

        if (list == null) {
            list = new ArrayList<>();
            grid.put(p, list);
        }

        list.add(obj);
    }

    public void removeAt(GameObject obj) {

        Point2D p = obj.getPosition();
        List<GameObject> list = this.grid.get(p);

        if (list != null) list.remove(obj);
    }

    /**
     * Percorre todos os Game Objects do HashMap e seleciona os que são do tipo
     * da classe passada como argumento.
     *
     * @param <T> Tipo genérico dos objetos a serem retornados
     * @param classe Classe para identificar os objetos do mesmo tipo
     * @return Lista de objetos do tipo especificado
     */
    public <T> List<T> listAllObjectsOfType(Class<T> classe) {
        List<T> list = new ArrayList<>();

        for (List<GameObject> lista : this.grid.values()) {
            for (GameObject obj : lista) {
                if (classe.isInstance(obj)) list.add(classe.cast(obj));
            }
        }

        return list;
    }

    /**
     * Devolve todos os Game Objects numa dada direção a partir do ponto inicial,
     * até ao final do mapa. Não inclui o objeto na posição inicial.
     *
     * @param point Ponto inicial onde começa a busca
     * @param dir Direção para a qual deve recolher os Game Objects
     * @return Lista de Game Objects na direção especificada
     */
    public List<GameObject> allObjectsAboveToSide(Point2D point, Direction dir) {
        List<GameObject> objsInDir = new ArrayList<>();

        Point2D currPos = point.plus(dir.asVector());

        while (isInBounds(currPos)) {
            GameObject object = getAt(currPos);

            objsInDir.add(object);

            currPos = currPos.plus(dir.asVector());
        }
        return objsInDir;
    }
}