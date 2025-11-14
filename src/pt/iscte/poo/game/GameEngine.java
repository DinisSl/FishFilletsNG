package pt.iscte.poo.game;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;

public class GameEngine implements Observer {

    private final List<Room> rooms;
	private Room currentRoom;
	private int currentRoomNumber;
	private int lastTickProcessed = 0;
	
	public GameEngine() {
        this.rooms = new ArrayList<>();
        gatherAllRooms(new File("rooms"));
        this.currentRoom = this.rooms.getFirst();
        this.currentRoomNumber = 0;
    }
//    Pega no caminho da dirétoria e transforma todos os ficheiros em Room
//    e mete adiciona a List de Room
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
        this.currentRoom = this.rooms.get(this.currentRoomNumber ++);
        this.currentRoom.loadRoom();
        this.currentRoom.initializeRoom();
        ImageGUI.getInstance().update();
	}
    public void endGame(String message) {
        ImageGUI.getInstance().showMessage("Fim do jogo: ", message);
        ImageGUI.getInstance().dispose();
        System.exit(0);
    }
    public void restartLevel() {
        ImageGUI.getInstance().clearImages();
        this.currentRoom.restartRoom();
        ImageGUI.getInstance().update();
    }

	@Override
	public void update(Observed source) {

		if (ImageGUI.getInstance().wasKeyPressed()) {

			int k = ImageGUI.getInstance().keyPressed();

            if(k == KeyEvent.VK_SPACE)
                this.currentRoom.changeCurrentFishIfAllowed();

            if (Direction.isDirection(k)) {
                boolean levelPassed = this.currentRoom.handleMovement(k);

                if (levelPassed) {
                    if (this.currentRoom == this.rooms.getLast()) {
                        endGame("Sucesso, fim do jogo!!!");
                    } else {
                        ImageGUI.getInstance().clearImages();
                        startLevel();
                    }
                }

            }

            if (k == KeyEvent.VK_R)
                this.restartLevel();

		}

		int t = ImageGUI.getInstance().getTicks();

		while (this.lastTickProcessed < t) {
			processTick();
		}
		
		ImageGUI.getInstance().update();
	}

	private void processTick() {		
		this.lastTickProcessed++;
	}	
}
