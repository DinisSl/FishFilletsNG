package pt.iscte.poo.game;

import objects.Blood;
import objects.Explosion;
import objects.Water;
import objects.management.GameCharacter;
import objects.management.GameObject;
import objects.management.GravitySystem;
import objects.management.MovementSystem;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Point2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Room {
    // Guarda uma lista de Game Characters ativos na Room
    private final List<GameCharacter> activeGC;
    // Serve para guardar os Game Objects
    private final Grid grid;
    private final File file;
    // Guarda o Game Character que está a ser controlado
    private GameCharacter currentGameCharacter;
    // Gere a gravidade do jogo
    private GravitySystem gravitySystem;
    /*Gere todos os movimentos quer dos Game Characters
    quer dos Game Objects*/
    private final MovementSystem movementSystem;

    public Room(File f) {
        this.file = f;
        this.activeGC = new ArrayList<>();
        this.grid = new Grid();
        this.gravitySystem = new GravitySystem(this);
        this.movementSystem = new MovementSystem(this);
    }


    /*-----------------------------------------------------------
    GETTERS/SETTERS PARA OS DIFERENTES SISTEMAS
    -----------------------------------------------------------*/
    public List<GameCharacter> getActiveGCCopy() {
        return List.copyOf(this.activeGC);
    }
    public List<GameCharacter> getActiveGC() {
        return this.activeGC;
    }
    public Grid getGrid() {
        return this.grid;
    }

    public GravitySystem getGravitySystem() {
        return this.gravitySystem;
    }

    public MovementSystem getMovementSystem() {
        return this.movementSystem;
    }


    /*-----------------------------------------------------------
    CURRENT GAME CHARACTER
    -----------------------------------------------------------*/
    public GameCharacter getCurrentGameCharacter() {
        return this.currentGameCharacter;
    }

    public void setCurrentGameCharacter(GameCharacter currentGameCharacter) {
        this.currentGameCharacter = currentGameCharacter;
    }

    // Muda o Game Character que está a ser controlado
    private void changeCurrentGameCharacter() {
        // Vai buscar o índice do currentGameCharacter na lista
        int currIndex = this.activeGC.indexOf(this.getCurrentGameCharacter());

         /*Calcula o índice do próximo Game Character se o currentGameCharacter
         for o último na lista, o índice vai dar 0, ou seja, volta para o início*/
        int nextIndex = (currIndex + 1) % this.activeGC.size();

        // Por fim atualiza o currentGameCharacter
        setCurrentGameCharacter(this.activeGC.get(nextIndex));
    }

    // Se ainda nenhum peixe passou muda o peixe que está a ser controlado
    public void changeCurrentGameCharacterIfAllowed() {
        if (this.activeGC.size() > 1) changeCurrentGameCharacter();
    }

    /*-----------------------------------------------------------
    GAME OBJECT
    -----------------------------------------------------------*/
    public GameObject getGameObject(Point2D position) {
        return this.grid.getAt(position);
    }

    public void addObject(GameObject obj) {
        this.grid.setAt(obj);
        ImageGUI.getInstance().addImage(obj);
    }

    public void removeObject(GameObject obj) {
        this.grid.removeAt(obj);
        ImageGUI.getInstance().removeImage(obj);
    }

    /*-----------------------------------------------------------
    MANAGE ROOM STATE
    -----------------------------------------------------------*/
    public void loadRoom() {
        // Inicializa o Hash Map
        this.grid.initializeHashMapOfLists();

        try (Scanner s = new Scanner(this.file)) {
            while (s.hasNextLine()) {
                for (int y = 0; y < 10; y++) {
                    // Preencher a linha que não tem muro "W" com espaços
                    String line = String.format("%-10s", s.nextLine());

                    for (int x = 0; x < 10; x++) {
                        processPosition(x, y, line.charAt(x));
                    }
                }
            }
            if (!this.activeGC.isEmpty()) setCurrentGameCharacter(this.activeGC.getFirst());

        } catch (FileNotFoundException e) {
            System.err.println("Ficheiro não encontrado");
        }
    }

    private void processPosition(int x, int y, char c) {
        Point2D p = new Point2D(x, y);
        System.out.println(p);
        // Adiciona água a todos os pontos
        addObject(new Water(p));

        /*Se for só um espaço vazio terminamos a função, pois
        é uma posição que apenas tem água*/
        if (c == ' ') return;

        GameObject gameObject = GameObject.createGameObject(c, p);
        // Se não for um Game Character basta adicioná-o
        this.addObject(gameObject);

        // Se for GameCharacter adicioná-lo à lista de GC Ativos
        if (gameObject instanceof GameCharacter) this.activeGC.add((GameCharacter) gameObject);
    }

    public void restartRoom() {
        // Limpa o GUI do nível atual
        ImageGUI.getInstance().clearImages();
        // Limpa a lista de Game Characters ativos
        this.activeGC.clear();
        // Dá load à Room de volta para o estado passado pelo ficheiro
        loadRoom();
    }

    /*-----------------------------------------------------------
    HANDLES MOVEMENT/COLLISIONS/EXIT
    -----------------------------------------------------------*/
    public void handleGravity() {
        gravitySystem.update();
    }

    public boolean handleMovement(int k) {
        return this.movementSystem.handleMovement(k);
    }

    public void killGameCharacter(List<GameCharacter> toKill, boolean byExplosion) {
        for (GameCharacter gc : toKill) {
            removeObject(gc);
            /*Se o Game Character não morreu devido a uma explosão podemos
            adicionar o sangue*/
            if (!byExplosion) addObject(new Blood(gc.getPosition()));
        }
        ImageGUI.getInstance().update();
        ImageGUI.getInstance().showMessage("GANDA BANANA", "Your fishlet just got slimed!!");
        // Recomeça o nível
        restartRoom();
    }

    public void explodePoints(List<Point2D> points) {
        List<GameCharacter> toKill = new ArrayList<>();

        for (Point2D p : points) {
            GameObject original = getGameObject(p);

            // Se a explosão for em cima da água não a removemos
            if (!(original instanceof Water)) {
                /*Se um Game Character morreu adicionamo-lo à lista para matar
                Se não for um Game Character apenas removemos o Game Object*/
                if (original instanceof GameCharacter gc) {
                    toKill.add(gc);
                } else {
                    removeObject(original);
                }
            }
            addObject(new Explosion(p, System.currentTimeMillis()));
        }

        ImageGUI.getInstance().update();

         /*Se pelo menos um Game Character morreu devido à bomba mata
        esse/s Game Character/s*/
        if (!toKill.isEmpty()) {
            killGameCharacter(toKill, true);
        }
    }
}