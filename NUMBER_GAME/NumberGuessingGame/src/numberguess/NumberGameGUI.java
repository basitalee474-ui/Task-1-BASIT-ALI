package numberguess;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class NumberGameGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainCardContainer;
    private JLayeredPane glassLayeredPane;

    private final GameLogic logicEngine = new GameLogic();
    private Player sessionUser;
    private final StatisticsManager statsTracker = new StatisticsManager();
    private final AchievementManager achievementEngine = new AchievementManager();

    // Input fields
    private JTextField loginUserField, signUpUserField;
    private JPasswordField loginPassField, signUpPassField;
    private JLabel authFeedbackMessage;

    // Game variables
    private JTextField guessField;
    private JLabel numFeedback, numHUD;
    private JLabel dashboardGreeting;
    private JLabel rpsFeedback, rpsHUD;
    private int currentRoundGuesses = 0;

    public NumberGameGUI() {
        setTitle("ARCADE NEXUS");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainCardContainer = new JPanel(cardLayout);
        mainCardContainer.setBackground(ThemeManager.BG_DARK);

        // Screens registration
        mainCardContainer.add(buildWelcomeAuthScreen(), "AUTH_MAIN");
        mainCardContainer.add(buildMenuScreen(), "MENU");
        mainCardContainer.add(buildNumberGameScreen(), "NUMBER_GAME");
        mainCardContainer.add(buildRpsGameScreen(), "RPS_GAME");

        // Wrap into layered pane to support big screen alerts cleanly
        glassLayeredPane = new JLayeredPane();
        mainCardContainer.setBounds(0, 0, 600, 550);
        glassLayeredPane.add(mainCardContainer, JLayeredPane.DEFAULT_LAYER);

        add(glassLayeredPane);
        cardLayout.show(mainCardContainer, "AUTH_MAIN");
    }

    /**
     * Screen 1: Unified Login & Registration Screen
     */
    private JPanel buildWelcomeAuthScreen() {
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setBackground(ThemeManager.BG_DARK);
        container.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel mainTitle = new JLabel("NumberGuessingGame", SwingConstants.CENTER);
        mainTitle.setFont(new Font("Impact", Font.PLAIN, 42));
        mainTitle.setForeground(ThemeManager.NEON_CYAN);
        container.add(mainTitle, BorderLayout.NORTH);

        JTabbedPane tabSys = new JTabbedPane();
        tabSys.setBackground(ThemeManager.PANEL_SURFACE);
        tabSys.setForeground(ThemeManager.TEXT_LIGHT);
        tabSys.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Sub-Tab A: Sign In
        JPanel loginTab = new JPanel(new GridLayout(5, 1, 5, 5));
        loginTab.setBackground(ThemeManager.PANEL_SURFACE);
        loginTab.setBorder(new EmptyBorder(15, 20, 15, 20));
        loginUserField = new JTextField(); setupInputStyle(loginUserField);
        loginPassField = new JPasswordField(); setupInputStyle(loginPassField);
        JButton actLogin = createBtn("LOG IN NOW", ThemeManager.NEON_CYAN);
        loginTab.add(createLabel("Your Username:")); loginTab.add(loginUserField);
        loginTab.add(createLabel("Your Password:")); loginTab.add(loginPassField);
        loginTab.add(actLogin);

        // Sub-Tab B: Create Account
        JPanel registerTab = new JPanel(new GridLayout(5, 1, 5, 5));
        registerTab.setBackground(ThemeManager.PANEL_SURFACE);
        registerTab.setBorder(new EmptyBorder(15, 20, 15, 20));
        signUpUserField = new JTextField(); setupInputStyle(signUpUserField);
        signUpPassField = new JPasswordField(); setupInputStyle(signUpPassField);
        JButton actRegister = createBtn("CREATE ACCOUNT & REGISTER", ThemeManager.NEON_PINK);
        registerTab.add(createLabel("Choose Username:")); registerTab.add(signUpUserField);
        registerTab.add(createLabel("Choose Password:")); registerTab.add(signUpPassField);
        registerTab.add(actRegister);

        tabSys.addTab("SIGN IN TO PLAY", loginTab);
        tabSys.addTab("CREATE NEW ACCOUNT", registerTab);

        authFeedbackMessage = new JLabel("Welcome! Please log in or sign up to get started.", SwingConstants.CENTER);
        authFeedbackMessage.setFont(new Font("Segoe UI", Font.BOLD, 12));
        authFeedbackMessage.setForeground(ThemeManager.TEXT_MUTED);

        actLogin.addActionListener(e -> handleLoginRoute());
        actRegister.addActionListener(e -> handleRegistrationRoute());

        container.add(tabSys, BorderLayout.CENTER);
        container.add(authFeedbackMessage, BorderLayout.SOUTH);
        return container;
    }

    private void handleLoginRoute() {
        String u = loginUserField.getText().trim();
        String p = new String(loginPassField.getPassword()).trim();
        int res = logicEngine.loginUser(u, p);

        if (res == 1) {
            sessionUser = logicEngine.getPlayerInstance(u);
            enterDashboard();
        } else if (res == -1) {
            authFeedbackMessage.setText("❌ Wrong Password! Please try again.");
            authFeedbackMessage.setForeground(ThemeManager.NEON_RED);
            logicEngine.playSoundEffect(false);
        } else {
            authFeedbackMessage.setText("⚠️ Account doesn't exist! Create it in the next tab.");
            authFeedbackMessage.setForeground(ThemeManager.NEON_PINK);
        }
    }

    private void handleRegistrationRoute() {
        String u = signUpUserField.getText().trim();
        String p = new String(signUpPassField.getPassword()).trim();
        if (u.isEmpty() || p.isEmpty()) {
            authFeedbackMessage.setText("⚠️ Fields cannot be empty!");
            authFeedbackMessage.setForeground(ThemeManager.NEON_RED);
            return;
        }
        if (logicEngine.registerNewUser(u, p)) {
            authFeedbackMessage.setText("🎉 Account created! Now switch to Sign In tab to enter.");
            authFeedbackMessage.setForeground(ThemeManager.NEON_GREEN);
            signUpUserField.setText(""); signUpPassField.setText("");
        } else {
            authFeedbackMessage.setText("❌ Username already taken! Choose another one.");
            authFeedbackMessage.setForeground(ThemeManager.NEON_RED);
        }
    }

    private void enterDashboard() {
        dashboardGreeting.setText("PLAYER CONTROLLER: " + sessionUser.getName().toUpperCase());
        authFeedbackMessage.setText("Welcome back!");
        loginUserField.setText(""); loginPassField.setText("");
        cardLayout.show(mainCardContainer, "MENU");
    }

    /**
     * Screen 2: Central Game Selection Hub
     */
    private JPanel buildMenuScreen() {
        JPanel container = new JPanel(new BorderLayout(0, 20));
        container.setBackground(ThemeManager.BG_DARK);
        container.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setOpaque(false);
        JLabel title = new JLabel("PLAYER HUB", SwingConstants.CENTER);
        title.setFont(new Font("Impact", Font.PLAIN, 36));
        title.setForeground(ThemeManager.TEXT_LIGHT);
        dashboardGreeting = new JLabel("PLAYER CONTROLLER ACTIVE", SwingConstants.CENTER);
        dashboardGreeting.setForeground(ThemeManager.NEON_PINK);
        header.add(title); header.add(dashboardGreeting);
        container.add(header, BorderLayout.NORTH);

        JPanel actionCard = createPanelCard();
        actionCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton openNumGame = createBtn("PLAY: THE NUMBER GAME", ThemeManager.NEON_CYAN);
        JButton openRpsGame = createBtn("PLAY: ROCK, PAPER, SCISSORS", ThemeManager.BG_DARK);
        openRpsGame.setBorder(BorderFactory.createLineBorder(ThemeManager.NEON_PINK, 2));
        JButton changePass  = createBtn("⚙️ SECURITY CONFIG", ThemeManager.PANEL_SURFACE);
        JButton disconnect  = createBtn("LOG OUT", ThemeManager.NEON_RED);

        openNumGame.addActionListener(e -> { resetNumberGameSession(); cardLayout.show(mainCardContainer, "NUMBER_GAME"); });
        openRpsGame.addActionListener(e -> cardLayout.show(mainCardContainer, "RPS_GAME"));
        changePass.addActionListener(e -> {
            String newP = JOptionPane.showInputDialog(this, "Type your new password:", "Security Config", JOptionPane.PLAIN_MESSAGE);
            if (newP != null && !newP.trim().isEmpty()) {
                sessionUser.setPassword(newP.trim());
                JOptionPane.showMessageDialog(this, "🔑 Password successfully changed!", "Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        disconnect.addActionListener(e -> { sessionUser = null; cardLayout.show(mainCardContainer, "AUTH_MAIN"); });

        gbc.gridx = 0; gbc.gridy = 0; actionCard.add(openNumGame, gbc);
        gbc.gridx = 0; gbc.gridy = 1; actionCard.add(openRpsGame, gbc);
        gbc.gridx = 0; gbc.gridy = 2; actionCard.add(changePass, gbc);
        gbc.gridx = 0; gbc.gridy = 3; actionCard.add(disconnect, gbc);
        container.add(actionCard, BorderLayout.CENTER);

        return container;
    }

    /**
     * Screen 3: Number Game Layout
     */
    private JPanel buildNumberGameScreen() {
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setBackground(ThemeManager.BG_DARK);
        container.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JButton back = createBtn("← HUB", ThemeManager.PANEL_SURFACE);
        back.addActionListener(e -> cardLayout.show(mainCardContainer, "MENU"));
        JButton rules = createBtn("📋 RULES", ThemeManager.NEON_PINK);
        rules.addActionListener(e -> showRulesDialog("Number Game Rules", "1. A secret number between 1 and 100 has been chosen.\n2. You have exactly 7 total attempts to find it.\n3. Each wrong guess tells you if the answer is too high or too low."));
        topBar.add(back, BorderLayout.WEST); topBar.add(rules, BorderLayout.EAST);
        container.add(topBar, BorderLayout.NORTH);

        JPanel mainArea = createPanelCard();
        mainArea.setLayout(new BoxLayout(mainArea, BoxLayout.Y_AXIS));
        mainArea.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel tip = new JLabel("Guess the secret number (1 to 100):", SwingConstants.CENTER);
        tip.setFont(new Font("Segoe UI", Font.BOLD, 13)); tip.setForeground(ThemeManager.TEXT_LIGHT); tip.setAlignmentX(Component.CENTER_ALIGNMENT);

        guessField = new JTextField();
        guessField.setMaximumSize(new Dimension(140, 42)); guessField.setFont(new Font("Consolas", Font.BOLD, 24));
        guessField.setBackground(ThemeManager.BG_DARK); guessField.setForeground(ThemeManager.NEON_CYAN);
        guessField.setHorizontalAlignment(JTextField.CENTER); guessField.setBorder(BorderFactory.createLineBorder(ThemeManager.NEON_PINK, 2, true));
        guessField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton actionCheck = createBtn("CHECK GUESS", ThemeManager.NEON_CYAN); actionCheck.setAlignmentX(Component.CENTER_ALIGNMENT);
        numFeedback = new JLabel("Ready. Input a number!", SwingConstants.CENTER); numFeedback.setForeground(ThemeManager.TEXT_MUTED); numFeedback.setAlignmentX(Component.CENTER_ALIGNMENT);
        numHUD = new JLabel("Attempts Left: 7  |  Wins: 0", SwingConstants.CENTER); numHUD.setForeground(ThemeManager.NEON_GREEN); numHUD.setAlignmentX(Component.CENTER_ALIGNMENT);

        actionCheck.addActionListener(e -> {
            try {
                int input = Integer.parseInt(guessField.getText().trim());
                currentRoundGuesses++;
                int status = logicEngine.evaluateGuess(input);

                if (status == 0) {
                    triggerBigScreenOverlay(true, "🎉 WINNER!", "Awesome! The number was indeed " + logicEngine.getTargetNumber());
                    sessionUser.incrementWins();
                    statsTracker.recordSessionStats(currentRoundGuesses, 1);
                    ScoreManager.saveScore("NUMBER_GAME", sessionUser.getName(), 100);
                    resetNumberGameSession();
                } else if (logicEngine.getAttemptsRemaining() <= 0) {
                    triggerBigScreenOverlay(false, "😢 GAME OVER", "Out of attempts! The number was " + logicEngine.getTargetNumber());
                    statsTracker.recordSessionStats(currentRoundGuesses, 1);
                    sessionUser.resetStreak();
                    resetNumberGameSession();
                } else if (status == 1) {
                    numFeedback.setText("Too High! Try a smaller number.");
                    numFeedback.setForeground(ThemeManager.NEON_CYAN);
                } else {
                    numFeedback.setText("Too Low! Try a bigger number.");
                    numFeedback.setForeground(ThemeManager.NEON_PINK);
                }
                numHUD.setText("Attempts Left: " + logicEngine.getAttemptsRemaining() + "  |  Wins: " + sessionUser.getTotalRoundsWon());
            } catch (NumberFormatException ex) {
                numFeedback.setText("⚠️ Invalid entry. Numbers only!");
                numFeedback.setForeground(ThemeManager.NEON_RED);
            }
            guessField.setText("");
        });

        mainArea.add(tip); mainArea.add(Box.createRigidArea(new Dimension(0, 10)));
        mainArea.add(guessField); mainArea.add(Box.createRigidArea(new Dimension(0, 15)));
        mainArea.add(actionCheck); mainArea.add(Box.createRigidArea(new Dimension(0, 20)));
        mainArea.add(numFeedback); mainArea.add(Box.createRigidArea(new Dimension(0, 20)));
        mainArea.add(numHUD);
        container.add(mainArea, BorderLayout.CENTER);
        return container;
    }

    /**
     * Screen 4: Rock, Paper, Scissors Layout
     */
    private JPanel buildRpsGameScreen() {
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setBackground(ThemeManager.BG_DARK);
        container.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JButton back = createBtn("← HUB", ThemeManager.PANEL_SURFACE);
        back.addActionListener(e -> cardLayout.show(mainCardContainer, "MENU"));
        JButton rules = createBtn("📋 RULES", ThemeManager.NEON_PINK);
        rules.addActionListener(e -> showRulesDialog("RPS Battle Rules", "1. Click an action button below to select your move.\n2. Rock crushes Scissors.\n3. Paper wraps Rock.\n4. Scissors cuts Paper."));
        topBar.add(back, BorderLayout.WEST); topBar.add(rules, BorderLayout.EAST);
        container.add(topBar, BorderLayout.NORTH);

        JPanel mainArea = createPanelCard();
        mainArea.setLayout(new BoxLayout(mainArea, BoxLayout.Y_AXIS));
        mainArea.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel invite = new JLabel("Choose your battle item to play against the computer:", SwingConstants.CENTER);
        invite.setFont(new Font("Segoe UI", Font.BOLD, 13)); invite.setForeground(ThemeManager.TEXT_LIGHT); invite.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel toolRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        toolRow.setOpaque(false);
        JButton r = createBtn("ROCK", ThemeManager.PANEL_SURFACE); r.setBorder(BorderFactory.createLineBorder(ThemeManager.NEON_CYAN, 2));
        JButton p = createBtn("PAPER", ThemeManager.PANEL_SURFACE); p.setBorder(BorderFactory.createLineBorder(ThemeManager.NEON_PINK, 2));
        JButton s = createBtn("SCISSORS", ThemeManager.PANEL_SURFACE); s.setBorder(BorderFactory.createLineBorder(ThemeManager.TEXT_LIGHT, 2));
        toolRow.add(r); toolRow.add(p); toolRow.add(s); toolRow.add(Box.createRigidArea(new Dimension(0, 10)));

        rpsFeedback = new JLabel("Pick an option to trigger combat loop.", SwingConstants.CENTER); rpsFeedback.setForeground(ThemeManager.TEXT_MUTED); rpsFeedback.setAlignmentX(Component.CENTER_ALIGNMENT);
        rpsHUD = new JLabel("Your Wins: 0  ||  CPU Wins: 0", SwingConstants.CENTER); rpsHUD.setFont(new Font("Segoe UI", Font.BOLD, 16)); rpsHUD.setForeground(ThemeManager.NEON_CYAN); rpsHUD.setAlignmentX(Component.CENTER_ALIGNMENT);

        java.awt.event.ActionListener coreEngine = e -> {
            String choice = ((JButton) e.getSource()).getText();
            String cpuMove = logicEngine.generateRpsComputerMove();
            int result = logicEngine.evaluateRps(choice, cpuMove);
            statsTracker.recordSessionStats(1, 1);

            if (result == 0) {
                triggerBigScreenOverlay(true, "🤝 MATCH TIE", "Both picked " + choice + "! Great minds think alike.");
            } else if (result == 1) {
                triggerBigScreenOverlay(true, "🎉 WINNER!", "Nice move! Your " + choice + " destroyed Computer's " + cpuMove);
                sessionUser.incrementWins();
                ScoreManager.saveScore("RPS_GAME", sessionUser.getName(), 10);
            } else {
                triggerBigScreenOverlay(false, "😢 DEFEAT!", "Ouch! Computer's " + cpuMove + " outsmarted your " + choice);
                sessionUser.resetStreak();
            }
            rpsHUD.setText("Your Wins: " + sessionUser.getTotalRoundsWon() + "  ||  CPU Wins: " + (statsTracker.getTotalGamesPlayed() - sessionUser.getTotalRoundsWon()));
        };

        r.addActionListener(coreEngine); p.addActionListener(coreEngine); s.addActionListener(coreEngine);

        mainArea.add(invite); mainArea.add(Box.createRigidArea(new Dimension(0, 20)));
        mainArea.add(toolRow); mainArea.add(Box.createRigidArea(new Dimension(0, 25)));
        mainArea.add(rpsFeedback); mainArea.add(Box.createRigidArea(new Dimension(0, 20)));
        mainArea.add(rpsHUD);
        container.add(mainArea, BorderLayout.CENTER);

        return container;
    }

    /**
     * Big Screen Game Over / Win Full Screen Popup Component
     */
    private void triggerBigScreenOverlay(boolean isWin, String mainHeader, String textDetails) {
        logicEngine.playSoundEffect(isWin);

        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(ThemeManager.OVERLAY_BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, 600, 550);
        overlay.setLayout(new GridBagLayout());

        JPanel popupBox = new JPanel();
        popupBox.setBackground(ThemeManager.PANEL_SURFACE);
        popupBox.setBorder(BorderFactory.createLineBorder(isWin ? ThemeManager.NEON_GREEN : ThemeManager.NEON_RED, 3, true));
        popupBox.setLayout(new BoxLayout(popupBox, BoxLayout.Y_AXIS));
        popupBox.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLbl = new JLabel(mainHeader, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Impact", Font.PLAIN, 46));
        titleLbl.setForeground(isWin ? ThemeManager.NEON_GREEN : ThemeManager.NEON_RED);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msgLbl = new JLabel("<html><center>" + textDetails + "</center></html>", SwingConstants.CENTER);
        msgLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        msgLbl.setForeground(ThemeManager.TEXT_LIGHT);
        msgLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton dismissBtn = createBtn("TRY AGAIN", ThemeManager.NEON_CYAN);
        dismissBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dismissBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        dismissBtn.addActionListener(e -> {
            glassLayeredPane.remove(overlay);
            glassLayeredPane.repaint();
            glassLayeredPane.revalidate();

            String badgeAlert = achievementEngine.checkAndUnlock(sessionUser.getTotalRoundsWon(), sessionUser.getCurrentStreak());
            if (badgeAlert != null) {
                JOptionPane.showMessageDialog(this, badgeAlert, "Trophy Unlocked!", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        popupBox.add(titleLbl); popupBox.add(Box.createRigidArea(new Dimension(0, 15)));
        popupBox.add(msgLbl); popupBox.add(Box.createRigidArea(new Dimension(0, 25)));
        popupBox.add(dismissBtn);

        overlay.add(popupBox);
        glassLayeredPane.add(overlay, JLayeredPane.DRAG_LAYER);
        glassLayeredPane.repaint();
        glassLayeredPane.revalidate();
    }

    private void showRulesDialog(String title, String ruleContent) {
        UIManager.put("OptionPane.background", ThemeManager.PANEL_SURFACE);
        UIManager.put("Panel.background", ThemeManager.PANEL_SURFACE);
        UIManager.put("OptionPane.messageForeground", ThemeManager.TEXT_LIGHT);
        JOptionPane.showMessageDialog(this, ruleContent, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetNumberGameSession() {
        logicEngine.initNumberGame(1, 100, 7);
        currentRoundGuesses = 0;
        numFeedback.setText("Enter a guess to begin!");
        numFeedback.setForeground(ThemeManager.TEXT_MUTED);
    }

    private JButton createBtn(String label, Color colorHex) {
        JButton b = new JButton(label);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setBackground(colorHex);
        b.setForeground(ThemeManager.TEXT_LIGHT); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        return b;
    }

    private JLabel createLabel(String txt) {
        JLabel l = new JLabel(txt); l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(ThemeManager.TEXT_MUTED); return l;
    }

    private void setupInputStyle(JTextField f) {
        f.setBackground(ThemeManager.BG_DARK); f.setForeground(ThemeManager.TEXT_LIGHT);
        f.setCaretColor(ThemeManager.NEON_CYAN); f.setBorder(BorderFactory.createLineBorder(new Color(60, 50, 95), 1));
    }

    private JPanel createPanelCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.PANEL_SURFACE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        p.setOpaque(false); return p;
    }
}