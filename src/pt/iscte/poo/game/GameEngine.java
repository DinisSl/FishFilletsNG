package pt.iscte.poo.game;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import objects.base.GameCharacter;
import objects.base.GameObject;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;

public class GameEngine implements Observer {
    // Referência para o objeto singleton
    private static GameEngine instance;
    // Lista para guardar as Rooms todas do jogo
    private final List<Room> rooms;
    // Para guardar a Room que está a ser jogada
    private Room currentRoom;
    // Guarda o índice da Room atual na lista rooms
    private int currentRoomNumber;
    public int lastTickProcessed = 0;

    private GameEngine() {
        // Inicializa a lista rooms
        this.rooms = new ArrayList<>();
        // Lê os ficheiros na diretoria room e adiciona-os à lista
        gatherAllRooms(new File("rooms"));
        // Atribui a primeira Room na lista ao atributo currentRoom
        this.currentRoom = this.rooms.getFirst();
        // Atribui o currentRoomNumber a 0
        this.currentRoomNumber = 0;
    }

    public static GameEngine getInstance() { // devolve a referência para o singleton,
        if(instance == null)                 // ou cria o objeto caso este ainda não
            instance = new GameEngine();     // exista ("lazy-loading")
        return instance;
    }

     /*Pega no caminho da dirétoria e transforma todos os ficheiros em Room
     e mete adiciona a List de Room*/
    public void gatherAllRooms(File f) {
        if (!f.isDirectory())
            throw new IllegalArgumentException("Caminho não é uma dirétoria" + f.getPath());

        File[] roomsArray = f.listFiles();

        if (roomsArray == null)
            throw new IllegalArgumentException("Null error" + f.getPath());

        if (roomsArray.length == 0)
            throw new IllegalArgumentException("Diretoria vazia" + f.getPath());

        for (File file : roomsArray) {
            this.rooms.add(new Room(file));
        }

    }

    public void startLevel() {
        // Atribui a currentRoom a Room da lista rooms com o índice currentRoomNumber ++
        this.currentRoom = this.rooms.get(this.currentRoomNumber ++);
        // Inicializa a Room atual
        this.currentRoom.loadRoom();
        // Atualiza o gui para mostrar a Room
        ImageGUI.getInstance().update();
    }

    public void endGame(String message) {
        ImageGUI.getInstance().showMessage("Fim do jogo: ", message);
        // Termina a instância do gui
        ImageGUI.getInstance().dispose();
        // Termina o programa com o status 0
        System.exit(0);
    }


    @Override
    public void update(Observed source) {

        if (ImageGUI.getInstance().wasKeyPressed()) {
            int k = ImageGUI.getInstance().keyPressed();

            // Se a tecla premida for espaço troca o peixe
            if(k == KeyEvent.VK_SPACE)
                this.currentRoom.changeCurrentGameCharacterIfAllowed();

            // Se a tecla premida for o R recomeça o nível atual
            if (k == KeyEvent.VK_R)
                this.currentRoom.restartRoom();


            // Se a tecla premida for uma direção válida
            if (Direction.isDirection(k)) {
                // Vê se a room Atual já foi concluída
                boolean roomFinished = processMovement(Direction.directionFor(k));

                if (roomFinished) {
                    // Se for a ultima sala acaba o jogo
                    if (this.currentRoom == this.rooms.getLast()) {
                        endGame("Sucesso, fim do jogo!!!");
                    } else {
                        // Se não for a última sala acaba esta e começa a proxima
                        ImageGUI.getInstance().clearImages();
                        startLevel();
                    }
                }
            }
        }

        int t = ImageGUI.getInstance().getTicks();

        while (this.lastTickProcessed < t) {
            processTick();
        }

        ImageGUI.getInstance().update();
    }

    private void processTick() {
        // Obter todos os objetos do jogo (Bomba, BigFish, Parede, etc)
        List<GameObject> allObjects = this.currentRoom.getGrid().listAllObjectsOfType(GameObject.class);

        for (GameObject obj : allObjects)
            obj.update(currentRoom);

        this.lastTickProcessed++;
    }

    private boolean processMovement(Direction direction) {
        GameCharacter gc = this.currentRoom.getCurrentGameCharacter();
        boolean wantsExit = gc.processMovement(direction, this.currentRoom);

        if (!wantsExit) return false;

        currentRoom.removeObject(gc);
        if (this.currentRoom.noCharactersLeft()) return true;

        currentRoom.pickNextCharacter();
        return false;
    }
}