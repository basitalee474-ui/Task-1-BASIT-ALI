package numberguess;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreManager {
    private static final String SCORE_PATH = "data/scores.txt";

    public static void saveScore(String gameName, String playerName, int score) {
        try {
            File file = new File(SCORE_PATH);
            file.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(file, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println(gameName + "," + playerName + "," + score);
            }
        } catch (IOException e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }

    public static List<String> getHighScores() {
        List<String> scores = new ArrayList<>();
        File file = new File(SCORE_PATH);
        if (!file.exists()) return scores;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    scores.add(parts[0] + " - " + parts[1] + ": " + parts[2] + " pts");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading scores: " + e.getMessage());
        }
        return scores;
    }
}