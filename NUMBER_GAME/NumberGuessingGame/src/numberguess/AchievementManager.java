package numberguess;

import java.util.ArrayList;
import java.util.List;

public class AchievementManager {
    private final List<String> unlockedBadges = new ArrayList<>();

    public String checkAndUnlock(int totalWins, int currentStreak) {
        if (totalWins == 1 && !unlockedBadges.contains("First Victory")) {
            unlockedBadges.add("First Victory");
            return "🏆 Trophy Unlocked: First Victory (You won your first match!)";
        }
        if (currentStreak == 3 && !unlockedBadges.contains("On Fire")) {
            unlockedBadges.add("On Fire");
            return "🏆 Trophy Unlocked: On Fire (3 Wins in a row!)";
        }
        return null;
    }
}