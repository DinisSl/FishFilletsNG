package interfaces;

import objects.attributes.Weight;
import objects.base.GameObject;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

import java.util.List;

public interface LoadBearer {
    boolean isOverloaded(int heavyCount, int lightCount);
    void onOverload(Room room);

    /**
     * Processa a lógica de suporte de Game Objects do tipo Movable
     * para objetos que implementem Load Bearer.
     *
     * Esta função verifica se o objeto está sobrecarregado com base nos
     * objetos que se encontram diretamente acima dele na Grid da Room.
     * Apenas são considerados objetos que são Movable; qualquer objeto
     * que não seja Movable interrompe imediatamente a contagem.
     *
     * O metodo:
     * 1 -> Garante que o próprio objeto é um GameObject para obter a posição.
     * 2 -> Obtém todos os objetos posicionados acima do objeto atual.
     * 3 -> Conta quantos desses objetos são Weight.HEAVY e Weight.LIGHT.
     * 4 -> Se a combinação de pesos exceder o limite definido pelo metodo
     *    isOverloaded (definido em cada instancia de LoadBearer),
     *    executa a ação de sobrecarga através do metodo onOverload().
     *
     * @param room A sala onde a verificação é efetuada.
     * @return true se o objeto ficou sobrecarregado e foi tratada a
     *         respetiva ação; false caso contrário.
     */
    default boolean processLoadBearing(Room room) {
        if (!(this instanceof GameObject me))
            return false;

        int heavyCount = 0;
        int lightCount = 0;

        List<GameObject> objectsAbove = room.getGrid()
                .allObjectsAboveToSide(me.getPosition(), Direction.UP);

        for (GameObject obj : objectsAbove) {
            if (obj instanceof Movable movable) {
                if (movable.getWeight() == Weight.HEAVY) {
                    heavyCount++;
                } else if (movable.getWeight() == Weight.LIGHT) {
                    lightCount++;
                }
            } else {
                break;
            }
        }

        if (isOverloaded(heavyCount, lightCount)) {
            onOverload(room);
            return true;
        }

        return false;
    }

}
