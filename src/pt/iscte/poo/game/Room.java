package pt.iscte.poo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import objects.*;
import objects.management.FallingObject;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.GravitySystem;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Room {
//    Guar uma lista de peixes ativos na Room
    private final List<GameCharacter> activeGC;
//    Guarda o peixe que está a ser controlado
    private GameCharacter currentGameCharacter;
//    Guarda o estado da Room
    private final Board board;
    private GravitySystem gravitySystem;
//    private Map<Point2D, List<GameObject>> room;
    private final File file;

    public Room(File f) {
        this.file = f;
        this.activeGC = new ArrayList<>();
        this.board = new Board();
        this.gravitySystem = new GravitySystem();
//        this.room = new HashMap<>();
    }
    public Board getBoard() { return this.board; }

    public List<GameCharacter> getActiveGC() {
        return activeGC;
    }

    // NAME
    public String getName() {
        return this.file.getName();
    }

// CURRENT GAME CHARACTER
    public GameCharacter getCurrentGameCharacter() {
        return currentGameCharacter;
    }
    public void setCurrentGameCharacter(GameCharacter currentGameCharacter) {
        this.currentGameCharacter = currentGameCharacter;
    }

//    Muda o Game Character que está a ser controlado
    private void changeCurrentGameCharacter() {
//        Vai buscar o índice do currentGameCharacter na lista
        int currIndex = this.activeGC.indexOf(this.getCurrentGameCharacter());

//        Calcula o índice do próximo Game Character se o currentGameCharacter
//        for o ultimo na lista, o índice vai dar 0, ou seja, volta para o início
        int nextIndex = (currIndex + 1) % this.activeGC.size();

//        Por fim atualiza o currentGameCharacter
        setCurrentGameCharacter(this.activeGC.get(nextIndex));
    }

    //    Se ainda nenhum peixe passou muda o peixe que está a ser controlado
    public void changeCurrentGameCharacterIfAllowed() {
        if (this.activeGC.size() > 1)
            changeCurrentGameCharacter();
    }

// GAME OBJECT
    public GameObject getGameObject(Point2D position) {
        return this.board.getAt(position);
    }

    public void addObject(GameObject obj) {
        this.board.setAt(obj);
        ImageGUI.getInstance().addImage(obj);
    }

    public void removeObject(GameObject obj) {
        this.board.removeAt(obj);
        ImageGUI.getInstance().removeImage(obj);
    }

// MANAGE ROOM STATE
    public void loadRoom() {
        this.board.initializeArrayOfList();
        try (Scanner s = new Scanner(this.file)){
            while (s.hasNextLine()) {
                for (int i = 0; i < 10; i++) {
                    StringBuilder sb = new StringBuilder(s.nextLine());
    //                Preencher a linha que não tem muro "W" com espaços
                    while (sb.length() < 10)
                        sb.append(" ");
                    String line = sb.toString();
                    for (int j = 0; j < 10; j++) {
                        char letra = line.charAt(j);

                        Point2D p = new Point2D(j, i);
                        addObject(new Water(p));
                        if (letra == ' ')
                            continue;
                        GameObject gameObject = GameObject.createGameObject(letra, p);

                        if (gameObject instanceof GameCharacter) {
                            this.activeGC.add((GameCharacter) gameObject);
                            this.addObject(gameObject);
//                       Se For GameCharacter dar set e adicioná-lo à lista
                        } else {
//                             Se não for um Game Character basta adicioná-o
                            this.addObject(gameObject);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        }
        setCurrentGameCharacter(this.activeGC.getFirst());
    }

    public void restartRoom() {
//        Limpa o gui do nível atual
        ImageGUI.getInstance().clearImages();
        this.activeGC.clear();
        this.gravitySystem = new GravitySystem();
//        Dá load à Room de volta para o estado passado pelo ficheiro
        loadRoom();
//        Dá update ao gui para mostrar a sala atualizada
        ImageGUI.getInstance().update();
    }

// HANDLES MOVEMENT/COLLISIONS/EXIT
    public void applyGravity() {
        gravitySystem.update(this, this.board);
    }

    public boolean handleMovement(int k) {
        Vector2D vector = Direction.directionFor(k).asVector();
        Point2D nextPointGC = getCurrentGameCharacter().getNextPosition(vector);
        GameObject nextGameObject = getGameObject(nextPointGC);

        if (nextGameObject == null)
            return handleExit(nextGameObject);

        // TRAP LOGIC
        if (nextGameObject instanceof Trap && getCurrentGameCharacter() instanceof BigFish bf) {
            List<GameCharacter> toKill = new ArrayList<>();
            toKill.add(bf);
            killGameCharacter(toKill, false);
            return false; // PARAR A EXECUÇÃO IMEDIATAMENTE APÓS A MORTE
        }
        // PUSH OBJECT LOGIC
        if (nextGameObject.blocksMovement(getCurrentGameCharacter())) {
            // SE FOR UM GC A EMPURRAR UM OBJETO QUE BLOQUEIA A SUA PASSAGEM
            // VAI VER SE É UM OBJETO QUE PODE CAIR, OU SEJA, PODE SER EMPURRADO
            if (nextGameObject instanceof FallingObject fo) {
                // SE FOR POSSÍVEL MEXE O OBJETO E DEPOIS ESTA FUNÇÃO
                // MEXE O GC
                boolean canMove =
                        fo.moveIfPossible(
                                this,
                                nextPointGC,
                                nextPointGC.plus(vector)
                        );
                if (canMove) {
                    moveGameCharacter(vector);
                    return false;
                }
            }
            return false;
        }

        moveGameCharacter(vector);
        return false;
    }

    public void moveObject(GameObject obj, Point2D newPos) {
        removeObject(obj);           // Remove from board + GUI
        obj.setPosition(newPos);     // Update object's position
        addObject(obj);              // Add to board + GUI at new position
    }

    private void moveGameCharacter(Vector2D vector) {
        GameCharacter gc = getCurrentGameCharacter();
        removeObject(gc);
        getCurrentGameCharacter().move(vector);
        addObject(getCurrentGameCharacter());
    }

//    Este metodo só é chamado quando um peixe sai do jogo
    private boolean handleExit(GameObject nextGameObject) {
//        Remove o currentGameCharacter da Room e da List
        removeObject(getCurrentGameCharacter());
        this.activeGC.remove(getCurrentGameCharacter());

//       Se a lista estiver vazia significa que o último GameCharacter
//       já passou logo já completou o nível e devolve true
        if (this.activeGC.isEmpty())
            return true;

//       Se ainda não passaram os GameCharacter todos então troca o peixe
        setCurrentGameCharacter(this.activeGC.getFirst());
        return false;
    }

    public void killGameCharacter(List<GameCharacter> toKill, boolean byExplosion) {
//        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//        String callerClassName = stackTraceElements[2].getClassName(); // Get the class name of the caller
//        String callerMethodName = stackTraceElements[2].getMethodName(); // Get the method name of the caller
//        System.out.println("killGameCharacter called by: " + callerClassName + "." + callerMethodName);

        for (GameCharacter gc : toKill) {
            System.out.println(gc.getName());
            removeObject(gc);
            if (!byExplosion)
                addObject(new Blood(gc.getPosition()));
        }
        ImageGUI.getInstance().update();
        ImageGUI.getInstance().showMessage("GANDA BANANA",
                "Your fishlet just got slimed!!");
        restartRoom();
    }

    public void explodePoints(List<Point2D> points) {
        boolean fishSlimed = false;
        List<GameCharacter> toKill = new ArrayList<>();

        // 1. Place visuals and identify victims
        for (Point2D p : points) {
            GameObject original = getGameObject(p);

            // Don't overwrite Water, but add explosion visual
            if (!(original instanceof Water)) {
                if (original instanceof GameCharacter gc) {
                    fishSlimed = true;
                    toKill.add(gc);
                } else {
                    removeObject(original); // Destroy crates/items
                }
            }
            addObject(new Explosion(p, System.currentTimeMillis()));
        }

        ImageGUI.getInstance().update();

        // 2. Handle Death vs Cleanup
        if (fishSlimed) {
            // If we die, we don't care about removing the explosion sprites
            // because the restartRoom() inside killGameCharacter will clear them.
            killGameCharacter(toKill, true);
        }
    }
}