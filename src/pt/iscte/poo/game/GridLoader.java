//package pt.iscte.poo.game;
//
//import objects.Water;
//import objects.management.GameCharacter;
//import objects.management.GameObject;
//import pt.iscte.poo.gui.ImageGUI;
//import pt.iscte.poo.utils.Point2D;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.Scanner;
//
//public class GridLoader {
//    public GridLoader(File file) {
//        loadRoom(file);
//    }
//
//    /**
//     * Carrega o mapa da sala a partir de um arquivo.
//     *
//     * Este metodo inicializa um HashMap de listas de Game Objects e lê o file "roomN.txt".
//     * Se uma linha tiver menos de 10 caracteres são adicionados espaços vazios
//     * até os ter. Isto pode acontecer numa linha que tenha um buraco na parede ("W    W    ")
//     * Por fim cada character da linha é passado a processPosition().
//     *
//     * @throws FileNotFoundException Se o arquivo especificado não for encontrado.
//     */
//    public void loadRoom(File file) {
//        this.grid.initializeHashMapOfLists();
//
//        try (Scanner s = new Scanner(file)) {
//            while (s.hasNextLine()) {
//                for (int y = 0; y < 10; y++) {
//                    String line = String.format("%-10s", s.nextLine());
//
//                    for (int x = 0; x < 10; x++) {
//                        processPosition(x, y, line.charAt(x));
//                    }
//                }
//            }
//            if (!this.activeGC.isEmpty()) setCurrentGameCharacter(this.activeGC.getFirst());
//
//        } catch (FileNotFoundException e) {
//            System.err.println("Ficheiro não encontrado");
//        }
//    }
//
//    /**
//     * Processa uma posição no mapa.
//     *
//     * Este metodo recebe as coordenadas (x, y) e um caracter que representa o respetivo
//     * Game Object.
//     * Adiciona água a todas as posições por defeito.
//     * Se o caracter for um espaço vazio, a função termina, pois já adicionou àgua.
//     * Caso contrário, passa o caracter a createGameObject() que cria o objeto correspondente.
//     * Se o objeto for um Game Character, adiciona-o à lista de personagens ativas.
//     *
//     * @param x Coordenada x do Game Object.
//     * @param y Coordenada y do Game Object.
//     * @param c Caracter que representa o respetivo Game Object.
//     */
//    private void processPosition(int x, int y, char c) {
//        Point2D p = new Point2D(x, y);
//        addObject(new Water(p));
//
//        if (c == ' ') return;
//
//        GameObject gameObject = GameObject.createGameObject(c, p);
//        this.addObject(gameObject);
//
//        if (gameObject instanceof GameCharacter) this.activeGC.add((GameCharacter) gameObject);
//    }
//
//    public void restartRoom() {
//        // Limpa o GUI do nível atual
//        ImageGUI.getInstance().clearImages();
//        // Limpa a lista de Game Characters ativos
//        this.activeGC.clear();
//        // Dá load à Room de volta para o estado passado pelo ficheiro
//        loadRoom();
//    }
//}
