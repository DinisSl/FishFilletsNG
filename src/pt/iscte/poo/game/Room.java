package pt.iscte.poo.game;

import objects.effects.Blood;
import objects.fixedObjects.Water;
import objects.base.*;
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
    private int turnCount;

    /*Gere todos os movimentos quer dos Game Characters,
    quer dos Game Objects*/

    public Room(File f) {
        this.file = f;
        this.activeGC = new ArrayList<>();
        this.grid = new Grid();
        this.turnCount = 0;
    }


    /*-----------------------------------------------------------
    GETTERS/SETTERS PARA OS DIFERENTES SISTEMAS
    -----------------------------------------------------------*/
    public List<GameCharacter> getActiveGC() {
        return List.copyOf(this.activeGC);
    }
    public Grid getGrid() {
        return this.grid;
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

    public void addObject(GameObject obj) {
        this.grid.setAt(obj);
        ImageGUI.getInstance().addImage(obj);

    }

    public void removeObject(GameObject obj) {
        this.grid.removeAt(obj);
        ImageGUI.getInstance().removeImage(obj);
    }

    public void moveObject(GameObject obj, Point2D newPos) {
        removeObject(obj);
        obj.setPosition(newPos);
        addObject(obj);
    }

    /*-----------------------------------------------------------
    MANAGE ROOM STATE
    -----------------------------------------------------------*/
    /**
     * Carrega o mapa da sala a partir de um arquivo.
     *
     * Este metodo inicializa um HashMap de listas de Game Objects e lê o file "roomN.txt".
     * Se uma linha tiver menos de 10 caracteres são adicionados espaços vazios
     * até os ter. Isto pode acontecer numa linha que tenha um buraco na parede ("W    W    ")
     * Por fim cada character da linha é passado a processPosition().
     *
     * @throws FileNotFoundException Se o arquivo especificado não for encontrado.
     */
    public void loadRoom() {
        this.grid.initializeHashMapOfLists();

        try (Scanner s = new Scanner(this.file)) {
            while (s.hasNextLine()) {
                for (int y = 0; y < 10; y++) {
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

    /**
     * Processa uma posição no mapa.
     *
     * Este metodo recebe as coordenadas (x, y) e um caracter que representa o respetivo
     * Game Object.
     * Adiciona água a todas as posições por defeito.
     * Se o caracter for um espaço vazio, a função termina, pois já adicionou àgua.
     * Caso contrário, passa o caracter a createGameObject() que cria o objeto correspondente.
     * Se o objeto for um Game Character, adiciona-o à lista de personagens ativas.
     *
     * @param x Coordenada x do Game Object.
     * @param y Coordenada y do Game Object.
     * @param c Caracter que representa o respetivo Game Object.
     */
    private void processPosition(int x, int y, char c) {
        Point2D p = new Point2D(x, y);
        addObject(new Water(p));

        if (c == ' ') return;

        GameObject gameObject = GameObject.createGameObject(c, p);
        this.addObject(gameObject);

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

    /*-----------------------------------------------------------
    MOVE OBJECT
    -----------------------------------------------------------*/

    // Lógica para sair do nível
    public boolean handleExit() {
        removeObject(currentGameCharacter);
        activeGC.remove(currentGameCharacter);

        if (getActiveGC().isEmpty())
            return true;

        setCurrentGameCharacter(getActiveGC().getFirst());
        return false;
    }

    public boolean noCharactersLeft() {
        return activeGC.isEmpty();
    }

    public void pickNextCharacter() {
        if (!activeGC.isEmpty()) setCurrentGameCharacter(activeGC.getFirst());
    }

    /*-----------------------------------------------------------
    TURN COUNT (CADA VEZ QUE UM GAME CHARACTER SE MEXE)
    -----------------------------------------------------------*/

    public void incrementTurn() {
        this.turnCount++;
    }
    
    public int getTurnCount() {
        return this.turnCount;
    }
}