package pt.iscte.poo.game;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import objects.End;
import objects.GameCharacter;
import objects.GameObject;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class GameEngine implements Observer {

    private final List<Room> rooms;
	private Room currentRoom;
	private int currentRoomNumber;
    private GameCharacter currentFish;
	private int lastTickProcessed = 0;
	
	public GameEngine() {
        this.rooms = new ArrayList<>();
        gatherAllRooms(new File("rooms"));
        this.currentRoom = this.rooms.getFirst();
        this.currentRoomNumber = 0;
    }

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
        this.currentFish = this.currentRoom.getBigFish();
        ImageGUI.getInstance().update();
	}
    public void endGame(String message) {
        ImageGUI.getInstance().showMessage("Fim do jogo: ", message);
        ImageGUI.getInstance().dispose();
        System.exit(0);
    }
    public void restartLevel() {
        ImageGUI.getInstance().clearImages();
        ImageGUI.getInstance().update();
    }

	@Override
	public void update(Observed source) {

		if (ImageGUI.getInstance().wasKeyPressed()) {

			int k = ImageGUI.getInstance().keyPressed();

            if(k == KeyEvent.VK_SPACE && !this.currentRoom.getOneFishPassed())
                changeCurrentFish();

            if (Direction.isDirection(k))
                canMoveToNextPosition(k);

            if (k == KeyEvent.VK_R)
                this.restartLevel();

		}

		int t = ImageGUI.getInstance().getTicks();

		while (this.lastTickProcessed < t) {
			processTick();
		}
		
		ImageGUI.getInstance().update();
	}

    private void changeCurrentFish() {
        if(this.currentFish.getName().equals(this.currentRoom.getBigFish().getName())) {
            this.currentFish = this.currentRoom.getSmallFish();
        } else {
            this.currentFish = this.currentRoom.getBigFish();
        }
    }
    private void canMoveToNextPosition(int k) {
        Vector2D vector2D = Direction.directionFor(k).asVector();
        Point2D nextPoint = this.currentFish.getNextPosition(vector2D);
        GameObject nextGameObject = this.currentRoom.getGameObject(nextPoint);

        if (!nextGameObject.blocksMovement(this.currentFish)) {
            this.currentFish.move(Direction.directionFor(k).asVector());
        } else {
            handleCollision(nextGameObject);
        }
    }
    private void handleCollision(GameObject nextGameObject) {
        if (nextGameObject instanceof End ) {
            if (this.currentRoom.getOneFishPassed() && this.currentRoom == this.rooms.getLast()) {
                this.endGame("SUCESSO!!!\nFim do Jogo");
            } else if (this.currentRoom.getOneFishPassed() && this.currentRoom != this.rooms.getLast()) {
                ImageGUI.getInstance().clearImages();
                System.out.println("olá");
                startLevel();
                return;
            }
            this.currentRoom.removeObject(this.currentFish);
            changeCurrentFish();
            this.currentRoom.setOneFishPassed(true);
        }

    }

	private void processTick() {		
		this.lastTickProcessed++;
	}	
}
