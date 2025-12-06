package pt.iscte.poo.game;

import pt.iscte.poo.gui.ImageGUI;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ScoreKeeper {
    private static ScoreKeeper instance;
    private final File FILE = new File("scoreboard.csv");

    private int totalMoves;
    private final long startTime;

    /* -------------------------------------------------------------------------
     CONSTRUTOR E METODO SINGLETON
     -------------------------------------------------------------------------*/

    private ScoreKeeper() {
        this.totalMoves = 0;
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Devolve a instância se já tiver sido criada, se não cria-a e devolve-a
     *
     * @return A instância de ScoreKeeper.
     */
    public static ScoreKeeper getInstance() {
        if (instance == null) {
            // Cria a instância apenas se for a primeira vez
            instance = new ScoreKeeper();
        }
        return instance;
    }

    /* -------------------------------------------------------------------------
     GETTERS E SETTERS
     -------------------------------------------------------------------------*/

    public int getTotalMoves() {
        return totalMoves;
    }

    public void incrementTotalMoves() {
        this.totalMoves++;
    }

    /* -------------------------------------------------------------------------
     GESTOR DO SCOREBOARD
     -------------------------------------------------------------------------*/

    /**
     * Termina o jogo, grava a pontuação, ordena e mostra o scoreboard.
     *
     * @param finishTime O tempo do sistema quando termina o jogo.
     */
    public void finishGame(Long finishTime) {
        long timePassed = finishTime - this.startTime;
        saveScore(this.totalMoves, timePassed);
        updateAndDisplayScoreboard();
    }

    /**
     * Lê, ordena, reescreve o ficheiro csv e no fim mostra o Scoreboard.
     */
    public void updateAndDisplayScoreboard() {
        List<String> lines = readScoreboardLines();

        if (lines.isEmpty())
            return;

        lines.sort(timeComparator());

        writeTopTenScoreboard(lines);

        showScoreboard(lines);
    }

    /**
     * Constroi o scoreboard final do ficheiro csv e
     * mostra-o na GUI.
     * @param lines As linhas ordenadas do placar.
     */
    public void showScoreboard(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        int rank = 1;

        // Itera sobre as linhas do placar (já ordenadas e possivelmente limitadas)
        for (String line : lines) {
            // Divide a string onde encontrar a vírgula
            String[] parts = line.split(",");

            // Verifica se a linha tem pelo menos 2 partes para evitar erros
            if (parts.length >= 2) {
                try {
                    long tempo = Long.parseLong(parts[0].trim());
                    String movimentos = parts[1].trim();

                    sb.append(rank).append("º ")
                            .append(formatFinalTime(tempo)).append(" - ")
                            .append(movimentos).append(" movimentos totais")
                            .append("\n");
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao analisar números no placar: " + line);
                }
            }
            rank++;
        }

        ImageGUI.getInstance().showMessage("Sucesso, fim do jogo!!!", sb.toString());
    }

    /* -------------------------------------------------------------------------
     METODOS AUXILIARES FILE IO E FORMATTING
     -------------------------------------------------------------------------*/

    /**
     * Guarda o tempo e os movimentos totais de do jogo
     *
     * @param moves
     * @param timePassed
     */
    private void saveScore(int moves, long timePassed) {
        try (FileWriter writer = new FileWriter(FILE, true);
             PrintWriter pw = new PrintWriter(writer)) {

            pw.printf("%d,%d%n", timePassed, moves);

        } catch (IOException e) {
            System.err.println("Erro ao salvar a pontuação: " + e.getMessage());
        }
    }

    /**
     * Lê as linhas do ficheiro FILE para uma lista do tipo String
     *
     * @return lista com todas as linhas do ficheiro FILE
     */
    private List<String> readScoreboardLines() {
        List<String> lines = new ArrayList<>();
        try (Scanner s = new Scanner(FILE)) {
            while (s.hasNextLine()) {
                lines.add(s.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao abrir o ficheiro: " + FILE.getName());
        }
        return lines;
    }

    /**
     * Reescreve apenas os melhores 10 tempos no FILE
     *
     * @param sortedLines recebe já as 10 melhores linhas por ordem
     */
    private void writeTopTenScoreboard(List<String> sortedLines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            // Escreve apenas os 10 melhores resultados
            for (int i = 0; i < 10 && i < sortedLines.size(); i++) {
                pw.println(sortedLines.get(i));
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no ficheiro: " + e.getMessage());
        }
    }

    /**
     * Cria um comparador para ordenar strings.
     *
     * Ordena as strings pelo primeiro número [0] encontrado nelas (o tempo).
     * O tempo mais baixo (melhor) fica em primeiro.
     *
     * @return O comparador de strings.
     */
    private Comparator<String> timeComparator() {
        return (line1, line2) -> {
            try {
                // A ordenação é feita pelo tempo (índice 1)
                long time1 = Long.parseLong(line1.split(",")[0]);
                long time2 = Long.parseLong(line2.split(",")[0]);

                // Menor tempo é melhor
                return Long.compare(time1, time2);

            } catch (Exception e) {
                // Em caso de erro de parsing, trata as linhas como iguais (não altera a ordem)
                return 0;
            }
        };
    }

    /**
     * Formata um tempo em milissegundos para o formato MM:SS.mmm.
     * @param timePassed O tempo em milissegundos.
     * @return String formatada.
     */
    private String formatFinalTime(long timePassed) {
        Duration duration = Duration.ofMillis(timePassed);
        long minutes = duration.toMinutes();
        long seconds = duration.toSecondsPart();
        long milliseconds = duration.toMillisPart();

        return String.format("%02d:%02d.%03d%c", minutes, seconds, milliseconds, 's');
    }
}