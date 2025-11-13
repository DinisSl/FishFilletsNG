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
	
	private final List<GameObject> objects;
	
	private SmallFish sf;
	private BigFish bf;
    private boolean oneFishPassed;
    private GameCharacter currentFish;

    private char[][] room;
    private final File file;
	
	public Room(File f) {
        this.file = f;
		this.objects = new ArrayList<>();
	}
// NAME
	public String getName() {
		return this.file.getName();
	}
// FISHES
	public void setSmallFish(SmallFish sf) {
		this.sf = sf;
	}
	public void setBigFish(BigFish bf) {
		this.bf = bf;
	}
	public SmallFish getSmallFish() {
		return sf;
	}
	public BigFish getBigFish() {
		return bf;
	}
// CURRENT FISH
    public GameCharacter getCurrentFish() {
        return currentFish;
    }
    public void setCurrentFish(GameCharacter currentFish) {
        this.currentFish = currentFish;
    }
    public void changeCurrentFish() {
        if(getCurrentFish() == getBigFish()) {
            setCurrentFish(getSmallFish());
        } else {
            setCurrentFish(getBigFish());
        }
    }
    public void changeCurrentFishIfAllowed() {
        if (!getOneFishPassed()) {
            changeCurrentFish();
        }
    }
// ONE FISH PASSED
    public boolean getOneFishPassed() {
        return oneFishPassed;
    }
    public void setOneFishPassed(boolean oneFishPassed) {
        this.oneFishPassed = oneFishPassed;
    }
// GAME OBJECT
	public GameObject getGameObject(Point2D point2D) {
        GameObject gameObjectFinal = null;
        for(GameObject gameObject : this.objects) {
            if (gameObject.getPosition().equals(point2D)) {

                if (gameObjectFinal == null || gameObject.getLayer() > gameObjectFinal.getLayer())
                    gameObjectFinal = gameObject;
            }
        }
        if (gameObjectFinal == null) {
            return new End(point2D);
        }
        return gameObjectFinal;
    }
    public void addObject(GameObject obj) {
		objects.add(obj);
		ImageGUI.getInstance().addImage(obj);
	}
	public void removeObject(GameObject obj) {
		objects.remove(obj);
		ImageGUI.getInstance().removeImage(obj);
	}
// INITIALIZE ROOM
	public void initializeRoom() {
        for (int i = 0; i < this.room.length; i++) {
            for (int j = 0; j < this.room[i].length; j++) {
                char letra = this.room[i][j];
                Point2D currentPoint = new Point2D(j, i);

                // Criar água em todos os quadrados
                this.addObject(new Water(currentPoint));

                // Dar skip quando é um espaço só com água
                if (letra == ' ')
                    continue;

                GameObject gameObject = GameObject.createGameObject(letra, currentPoint);

                if (gameObject instanceof BigFish) {
                    this.setBigFish((BigFish) gameObject);
                    this.addObject(gameObject);
                } else if (gameObject instanceof SmallFish) {
                    this.setSmallFish((SmallFish) gameObject);
                    this.addObject(gameObject);
                } else {
                    this.addObject(gameObject);
                }
            }
        }
        setCurrentFish(getBigFish());
	}
    public void loadRoom() {
        List<char[]> lines = new ArrayList<>();

        try (Scanner s = new Scanner(this.file)){
            while (s.hasNextLine()) {
                StringBuilder line = new StringBuilder(s.nextLine());
//              Preencher a linha que não tem muro "W" com espaços
                while (line.length() < 10)
                    line.append(" ");

                lines.add(line.toString().toCharArray());
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        }

        this.room = lines.toArray(new char[10][10]);
    }

    public boolean handleMovement(int k) {
        Vector2D vector2D = Direction.directionFor(k).asVector();
        Point2D nextPoint = getCurrentFish().getNextPosition(vector2D);
        GameObject nextGameObject = getGameObject(nextPoint);

        if (!nextGameObject.blocksMovement(getCurrentFish())) {
            getCurrentFish().move(Direction.directionFor(k).asVector());
            return false;
//            Não houve nehuma colisão que afetasse a progressão do nível
        } else {
            return handleCollision(nextGameObject);
//            Devolve o resultado da colisão
        }
    }

    private boolean handleCollision(GameObject nextGameObject) {
        if (nextGameObject instanceof End ) {
            if (this.oneFishPassed) {
//                Se o primeiro peixe já passou já completou o nível e devolve true
                return true;
            } else {
//                Se ainda não passou nenhum peixe logo este é o primeiro, remove-o
//                e troca-o
                removeObject(getCurrentFish());
                changeCurrentFish();
                setOneFishPassed(true);

                return false;
            }
        }
//        Se não for uma instância de End então é porque é uma parede
        return false;
    }
	
}