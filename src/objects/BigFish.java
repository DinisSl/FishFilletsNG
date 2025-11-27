package objects;

import interfaces.Movable;
import objects.management.GameCharacter;
import objects.management.GameObject;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class BigFish extends GameCharacter {
	
	public BigFish(Point2D p) {
		super(p);
	}

    // Se tiver a apontar para a direita devolve o bigFishRight,
    // Se tiver a apontar para a esquerda devolve o bigFishLeft
    @Override
	public String getName() {
        if (super.getCurrentDirection() == Direction.RIGHT) {
            return "bigFishRight";
        } else if (super.getCurrentDirection() == Direction.LEFT) {
            return "bigFishLeft";
        }
        return "bigFishLeft";
	}

    // Se o SmallFish bater no BigFish bloqueia se n passa
    @Override
    public boolean blocksMovement(GameObject gameCharacter) {
        return true;
    }

    @Override
    public boolean processMovement(Direction direction, Room room) {
        Vector2D vector = direction.asVector();
        Point2D nextPos = getNextPosition(vector);
        GameObject nextObj = room.getGameObject(nextPos);

        // 1. Verificar Saída
        if (nextObj == null) {
            return room.handleExit();
        }

        // 2. Verificar se está bloqueado
        if (nextObj.blocksMovement(this)) {
            checkDeadlyCollision(nextObj, room);

            if (nextObj instanceof Movable movable) {
                if (movable.canBePushedBy(this)) {
                    attemptChainPush(vector, room);
                }
            }
            return false;
        }

        // 3. Caminho livre
        moveSelf(vector, room);
        return false;
    }

    // Lógica de empurrar em cadeia movida do MovementSystem para aqui
    private void attemptChainPush(Vector2D vector, Room room) {
        Direction dir = Direction.forVector(vector);
        // Obtém objetos na direção do movimento
        List<GameObject> lineOfObjects = room.getGrid().allObjectsAboveToSide(this.getPosition(), dir);

        List<Movable> pushChain = new ArrayList<>();
        boolean canMove = false;

        for (GameObject obj : lineOfObjects) {
            if (obj instanceof Water) {
                // Encontrou espaço vazio, pode empurrar tudo até aqui
                canMove = true;
                break;
            }

            if (obj instanceof Movable p && p.canBePushedBy(this)) {
                pushChain.add(p);
            } else {
                // Parede ou objeto imóvel
                break;
            }
        }

        // Executar o empurrão de trás para a frente
        if (canMove && !pushChain.isEmpty()) {
            for (int i = pushChain.size() - 1; i >= 0; i--) {
                Movable p = pushChain.get(i);
                GameObject obj = (GameObject) p;
                p.push(room, obj.getPosition(), obj.getPosition().plus(vector));
            }
            moveSelf(vector, room);
        }
    }
}
