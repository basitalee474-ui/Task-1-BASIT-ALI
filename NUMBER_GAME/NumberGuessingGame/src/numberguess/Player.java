package numberguess;

public class Player {
    private String name;
    private String password;
    private int totalRoundsWon;
    private int currentStreak;

    public Player(String name, String password) {
        this.name = name;
        this.password = password;
        this.totalRoundsWon = 0;
        this.currentStreak = 0;
    }

    public String getName() { return name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getTotalRoundsWon() { return totalRoundsWon; }
    public int getCurrentStreak() { return currentStreak; }

    public void incrementWins() {
        this.totalRoundsWon++;
        this.currentStreak++;
    }
    public void resetStreak() { this.currentStreak = 0; }
}