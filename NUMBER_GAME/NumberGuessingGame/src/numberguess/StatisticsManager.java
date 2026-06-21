package numberguess;

import java.io.*;

public class StatisticsManager {
    private static final String STATS_PATH = "data/statistics.txt";
    private int totalGuessesMade = 0;
    private int totalGamesPlayed = 0;

    public void recordSessionStats(int guesses, int games) {
        this.totalGuessesMade += guesses;
        this.totalGamesPlayed += games;
        saveToDisk();
    }

    private void saveToDisk() {
        try {
            File file = new File(STATS_PATH);
            file.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("TotalGuessesMade=" + totalGuessesMade + "\n");
                bw.write("TotalGamesPlayed=" + totalGamesPlayed + "\n");
            }
        } catch (IOException e) {
            System.err.println("Could not save statistics: " + e.getMessage());
        }
    }

    public int getTotalGuessesMade() { return totalGuessesMade; }
    public int getTotalGamesPlayed() { return totalGamesPlayed; }
}