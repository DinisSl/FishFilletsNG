package pt.iscte.poo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import objects.*;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Room {
//    Guar uma lista de peixes ativos na Room
    private List<GameCharacter> activeGC;
//    Guarda o peixe que está a ser controlado
    private GameCharacter currentGameCharacter;
//    Guarda o estado da Room
    private List<GameObject>[][] room;
    private final File file;

    public Room(File f) {
        this.file = f;
        this.activeGC = new ArrayList<>();
        this.room = new ArrayList[10][10];
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
    public GameObject getGameObject(Point2D point2D) {
        int x = point2D.getX();
        int y = point2D.getY();
        if (x < 0 || x >= this.room.length || y < 0 || y >= this.room[0].length) {
            return null;
        }
        List<GameObject> objsInPos = this.room[x][y];
        GameObject gameObjectFinal = null;
        for(GameObject gameObject : objsInPos) {
//                Procura o gameObjectFinal que tem a maior camada no point2D
            if (gameObjectFinal == null || gameObject.getLayer() > gameObjectFinal.getLayer())
                gameObjectFinal = gameObject;

        }
        return gameObjectFinal;
    }

    public void addObject(GameObject obj) {
        int x = obj.getPosition().getX();
        int y = obj.getPosition().getY();
        this.room[x][y].add(obj);
        ImageGUI.getInstance().addImage(obj);
    }

    public void removeObject(GameObject obj) {
        int x = obj.getPosition().getX();
        int y = obj.getPosition().getY();
        this.room[x][y].remove(obj);
        ImageGUI.getInstance().removeImage(obj);
    }

// MANAGE ROOM STATE
    public void loadRoom() {
        initializeArrayOfList();
        try (Scanner s = new Scanner(this.file)){
            while (s.hasNextLine()) {
                for (int i = 0; i < this.room.length; i++) {
                    StringBuilder sb = new StringBuilder(s.nextLine());
    //                Preencher a linha que não tem muro "W" com espaços
                    while (sb.length() < 10)
                        sb.append(" ");
                    String line = sb.toString();
                    for (int j = 0; j < this.room[i].length; j++) {
                        char letra = line.charAt(j);
                        if (letra == ' ')
                            continue;

                        Point2D p = new Point2D(j, i);
                        GameObject gameObject = GameObject.createGameObject(letra, p);

                        System.out.println(gameObject.getName() + p);

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

    private void initializeArrayOfList() {
        for (int i = 0; i < this.room.length; i++) {
            for (int j = 0; j < this.room[i].length; j++) {
                this.room[i][j] = new ArrayList<>();
                addObject(new Water(new Point2D(i, j)));
            }
        }
    }

    public void restartRoom() {
        this.activeGC.clear();
//        Dá load à Room de volta para o estado passado pelo ficheiro
        loadRoom();
    }

// HANDLES MOVEMENT/COLLISIONS/EXIT
    public boolean handleMovement(int k) {
//        Pega no int da tecla premida e passa para um Vector2D
//        e depois por fim para um GameObject
        Vector2D vector = Direction.directionFor(k).asVector();
        Point2D nextPoint = getCurrentGameCharacter().getNextPosition(vector);
        GameObject nextGameObject = getGameObject(nextPoint);

//        Se "nextGameObject" for null então passa para handleExit
        if (nextGameObject == null)
            return handleExit(nextGameObject);
//        Se "nextGameObject" bloquear a passagem do currentFish devolve falso
        if (nextGameObject.blocksMovement(getCurrentGameCharacter()))
            return false;
//        Se nenhuma das condições acima se verificar então é porque se pode mover
        moveFish(vector);

        return false;
    }
    
    private void moveFish(Vector2D vector) {
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
}