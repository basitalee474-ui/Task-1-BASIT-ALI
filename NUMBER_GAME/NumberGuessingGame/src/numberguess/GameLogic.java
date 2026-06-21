package numberguess;

import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

public class GameLogic {
    private final Random random = new Random();
    private final Map<String, Player> registeredPlayers = new HashMap<>();

    private int targetNumber;
    private int maxAttempts;
    private int attemptsRemaining;

    // Registers a brand new account choice
    public boolean registerNewUser(String username, String password) {
        if (registeredPlayers.containsKey(username)) {
            return false; // User already exists
        }
        registeredPlayers.put(username, new Player(username, password));
        return true;
    }

    // Authenticates credentials for existing accounts
    public int loginUser(String username, String password) {
        if (!registeredPlayers.containsKey(username)) {
            return -2; // Username not found
        }
        Player p = registeredPlayers.get(username);
        if (p.getPassword().equals(password)) {
            return 1; // Success
        }
        return -1; // Wrong password
    }

    public Player getPlayerInstance(String username) {
        return registeredPlayers.get(username);
    }

    public void initNumberGame(int min, int max, int attempts) {
        this.targetNumber = random.nextInt((max - min) + 1) + min;
        this.maxAttempts = attempts;
        this.attemptsRemaining = attempts;
    }

    public int evaluateGuess(int guess) {
        this.attemptsRemaining--;
        if (guess == targetNumber) return 0;
        if (guess > targetNumber) return 1;
        return -1;
    }

    public int getAttemptsRemaining() { return attemptsRemaining; }
    public int getTargetNumber() { return targetNumber; }

    public String generateRpsComputerMove() {
        String[] moves = {"ROCK", "PAPER", "SCISSORS"};
        return moves[random.nextInt(3)];
    }

    public int evaluateRps(String playerMove, String computerMove) {
        if (playerMove.equals(computerMove)) return 0;
        if ((playerMove.equals("ROCK") && computerMove.equals("SCISSORS")) ||
                (playerMove.equals("PAPER") && computerMove.equals("ROCK")) ||
                (playerMove.equals("SCISSORS") && computerMove.equals("PAPER"))) {
            return 1;
        }
        return -1;
    }

    public void playSoundEffect(boolean winTone) {
        new Thread(() -> {
            try {
                float sampleRate = 8000f;
                byte[] buffer = new byte[6000];
                for (int i = 0; i < buffer.length; i++) {
                    double angle;
                    if (winTone) {
                        double frequency = 440.0 + (i / 12.0);
                        angle = i / (sampleRate / frequency) * 2.0 * Math.PI;
                    } else {
                        double frequency = Math.max(100.0, 280.0 - (i / 10.0));
                        angle = i / (sampleRate / frequency) * 2.0 * Math.PI;
                    }
                    buffer[i] = (byte) (Math.sin(angle) * 127);
                }
                AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);
                SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
                sdl.open(af);
                sdl.start();
                sdl.write(buffer, 0, buffer.length);
                sdl.drain();
                sdl.close();
            } catch (LineUnavailableException ex) {
                System.err.println("Audio Error: " + ex.getMessage());
            }
        }).start();
    }
}