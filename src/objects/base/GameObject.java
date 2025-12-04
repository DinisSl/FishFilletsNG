package objects.base;

import interfaces.LoadBearer;
import interfaces.Movable;
import objects.attributes.Weight;
import objects.characters.BigFish;
import objects.characters.SmallFish;
import objects.enviroment.HoledWall;
import objects.enviroment.Wall;
import objects.obstacles.*;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GameObject implements ImageTile {
    public final int LAYER_WATER = 0;
    public final int LAYER_EFFECTS = 1;
    public final int LAYER_OBSTACLES = 2;
    public final int LAYER_ITEMS = 3;
    public final int LAYER_GAME_CHARACTER = 4;

    private Point2D position;

    /*Set para guardar os Floating Objects que o Game Character está
    a suportar, utilizamos um set, pois por defeito não permite duplicados*/
    private final Set<Movable> supportedObjects;

    public GameObject(Point2D position) {
        this.position = position;
        this.supportedObjects = new HashSet<>();
    }

    // Metodo estático que devolve um GameObject passando um a letra do objeto como um char
    public static GameObject createGameObject(char character, Point2D point) {
        return switch (character) {
            case 'B' -> new BigFish(point);
            case 'S' -> new SmallFish(point);
            case 'W' -> new Wall(point);
            case 'H' -> new SteelBeam(point, false);
            case 'V' -> new SteelBeam(point, true);
            case 'X' -> new HoledWall(point);
            case 'R' -> new Stone(point);
            case 'b' -> new Bomb(point);
            case 'T' -> new Trap(point);
            case 'C' -> new Cup(point);
            case 'A' -> new Anchor(point);
            case 'Y' -> new Trunk(point);
            case 'Q' -> new Buoy(point);

            default -> throw new IllegalArgumentException("Character inválido: " + character + " na posição " + point);
        };
    }

    /*-----------------------------------------------------------
    POSITION
    -----------------------------------------------------------*/

    @Override
    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    /*Metodo que controla o que acontece a cada GameCharacter,
    por default é true, cada vez que bate num GameObject*/
    public boolean blocksMovement(GameObject gameCharacter) {
        return true;
    }

    /*Metodo chamado no Game Engine a cada tick para lidar
    com o comportamento de todos os objetos, por defeito não
    faz nada*/
    public void update(Room room) {}

    /*-----------------------------------------------------------
    MÉTODOS PARA MANIPULAR O SET
    -----------------------------------------------------------*/
    public void addSupportedObject(Movable mo) {
        this.supportedObjects.add(mo);
    }

    public void clearSupportedObjects() {
        this.supportedObjects.clear();
    }

    /*-----------------------------------------------------------
    MÉTODOS PARA LIDAR COM OS OBJETOS SUPORTADOS
    -----------------------------------------------------------*/
    /**
     * Analisa objetos diretamente acima, calcula o peso e aciona a interface LoadBearer
     * se o objeto estiver sobrecarregado.
     *
     * @param room A sala onde o objeto se encontra.
     * @return true se o objeto ficou sobrecarregado e foi executada uma ação.
     */
    protected boolean processLoadBearing(Room room) {
        if (!(this instanceof LoadBearer loadBearer)) {
            return false;
        }

        updateSupportedLoad(room);
        return evaluateAndTriggerOverload(room, loadBearer);
    }

    /**
     * Limpa o estado anterior e analisa a grelha à procura de novos SinkingObjects
     * que estejam diretamente acima do Game Object.
     */
    private void updateSupportedLoad(Room room) {
        clearSupportedObjects();

        List<GameObject> objectsAbove = room.getGrid()
                .allObjectsAboveToSide(this.getPosition(), Direction.UP);

        for (GameObject obj : objectsAbove) {
            if (obj instanceof Movable movable) {
                addSupportedObject(movable);
            } else {
                // Interrompe a análise ao atingir uma parede ou teto
                break;
            }
        }
    }

    /**
     * Calcula os pesos atuais e aciona a ação de sobrecarga se os limites forem excedidos.
     *
     * @param loadBearer A instância da interface para verificar os limites.
     * @return true se o limite foi excedido e a ação onOverload foi chamada.
     */
    private boolean evaluateAndTriggerOverload(Room room, LoadBearer loadBearer) {
        int[] weights = checkSupportOverload();
        int heavyWeight = weights[0];
        int lightWeight = weights[1];

        if (loadBearer.isOverloaded(heavyWeight, lightWeight)) {
            loadBearer.onOverload(room);
            return true;
        }

        return false;
    }

    /**
     * Conta todos os Sinking Objects em cima do Game Object
     * e devolve-os em forma de um int[]
     *
     * @return Devolve um array de inteiros.
     * Posição [0] - Sinking Objects Pesados
     * Posição [1] - Sinking Objects Leves
     */
    private int[] checkSupportOverload() {
        int heavyFO = 0;
        int lightFO = 0;

        for (Movable fo : this.supportedObjects) {
            if (fo.getWeight() == Weight.HEAVY) {
                heavyFO++;
            } else if (fo.getWeight() == Weight.LIGHT) {
                lightFO++;
            }
        }

        return new int[] {heavyFO, lightFO};
    }

}