package pt.iscte.poo.game;

import interfaces.Movable;
import objects.Blood;
import objects.Explosion;
import objects.Water;
import objects.management.*;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

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

    /*Gere todos os movimentos quer dos Game Characters,
    quer dos Game Objects*/

    public Room(File f) {
        this.file = f;
        this.activeGC = new ArrayList<>();
        this.grid = new Grid();
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

    public void explodePoints(List<Point2D> points) {
        List<GameCharacter> toKill = new ArrayList<>();

        for (Point2D p : points) {
            GameObject original = getGrid().getAt(p);

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

    /*-----------------------------------------------------------
    MOVE OBJECT
    -----------------------------------------------------------*/

    public boolean handleMovement(int k) {
        Direction dir = Direction.directionFor(k);
        return currentGameCharacter.processMovement(dir, this);
    }

    // Lógica movida do MovementSystem para a Room (Utilitário)
    public void moveObject(GameObject obj, Point2D newPos) {
        // Atualiza Grid e GUI
        removeObject(obj);
        obj.setPosition(newPos);
        addObject(obj);
    }

    // Lógica para sair do nível
    public boolean handleExit() {
        removeObject(currentGameCharacter);
        activeGC.remove(currentGameCharacter);

        if (getActiveGCCopy().isEmpty())
            return true;

        setCurrentGameCharacter(getActiveGCCopy().getFirst());
        return false;
    }

    public void handleGravity() {
        // Obter todos os objetos do jogo (Bomba, Peixe, Explosão, etc)
        // Usamos uma cópia da lista para evitar erros se um objeto se remover a si próprio durante o update
        List<GameObject> allObjects = grid.listAllObjectsOfType(GameObject.class);

        for (GameObject obj : allObjects) {
            obj.update(this);
        }
    }

    // Lógica de empurrar em cadeia movida do MovementSystem para aqui
    public void attemptChainPush(Vector2D vector) {
        Direction dir = Direction.forVector(vector);
        // Obtém objetos na direção do movimento
        List<GameObject> lineOfObjects = this.getGrid().allObjectsAboveToSide(getCurrentGameCharacter().getPosition(), dir);

        List<Movable> pushChain = new ArrayList<>();
        boolean canMove = false;

        for (GameObject obj : lineOfObjects) {
            if (obj.isFluid()) {
                // Encontrou espaço vazio, pode empurrar tudo até aqui
                canMove = true;
                break;
            }

            if (obj instanceof Movable p && p.canBePushedBy(getCurrentGameCharacter())) {
                pushChain.add(p);
            } else {
                // Parede ou objeto imóvel
                break;
            }
        }
        // Executar o empurrão de trás para a frente
        if (canMove && !pushChain.isEmpty()) {
            chainPushObjects(vector, pushChain);
        }
    }

    public void chainPushObjects(Vector2D vector, List<Movable> pushChain) {
        for (int i = pushChain.size() - 1; i >= 0; i--) {
            Movable p = pushChain.get(i);
            GameObject obj = (GameObject) p;
            p.push(this, obj.getPosition(), obj.getPosition().plus(vector));
        }
        getCurrentGameCharacter().moveSelf(vector, this);
    }
}