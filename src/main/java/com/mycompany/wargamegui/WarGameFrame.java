package com.mycompany.wargamegui;


import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 * The game window. It only shows things and passes clicks on: the rules live in
 * {@link WarGame}, and all SQL lives in {@link GameDAO} (reached through {@link GameRecorder}).
 *
 * @author neilf
 */
public class WarGameFrame extends javax.swing.JFrame {

    private static final Logger LOGGER = Logger.getLogger(WarGameFrame.class.getName());
    private static final int WINDOW_WIDTH = 620;
    private static final int WINDOW_HEIGHT = 520;
    private static final int HISTORY_LIMIT = 100;   // most rounds shown in a history window

    private final GameDAO dao = new GameDAO();
    private final GameRecorder recorder = new GameRecorder(dao);
    private WarGame game;
    private String playerName;
    private boolean databaseReady;

    /**
     * Creates the window for a player called "Player". The NetBeans form editor uses this
     * constructor; the game itself uses {@link #WarGameFrame(String)}.
     */
    public WarGameFrame() {
        this(PlayerNames.DEFAULT_NAME);
    }

    /**
     * Creates the window, opens the database (the game still works if that fails) and
     * starts a first game.
     *
     * @param playerName the already-validated name of the human player
     */
    public WarGameFrame(String playerName) {
        initComponents();
        this.playerName = playerName;
        setJMenuBar(buildMenuBar());
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);   // pack() alone gives a tiny window (labels start blank)
        setLocationRelativeTo(null);            // open in the middle of the screen

        // runs before EXIT_ON_CLOSE ends the program, so Derby is always shut down cleanly
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DBManager.shutdown();
            }
        });

        openDatabase();
        startNewGame();
    }

    // ------------------------------------------------------------ game flow

    /** Builds a fresh shuffled deck and game, and puts the window back to its starting look. */
    private void startNewGame() {
        // Generated with assistance from Claude (Anthropic)
        jLabel1.setText(playerName);
        jLabel2.setText(PlayerNames.CPU_NAME);
        Deck deck = new Deck();
        deck.shuffle();
        game = new WarGame(deck, new HumanPlayer(playerName), new CPU(PlayerNames.CPU_NAME));

        resultLabel.setText(" ");   // a single space keeps the label's height
        scoreLabel.setText(" ");
        playerCardLabel.setIcon(CardImages.backIcon());
        cpuCardLabel.setIcon(CardImages.backIcon());
        drawButton.setEnabled(true);   // a finished game leaves it disabled
    }

    private static String describe(RoundOutcome outcome) {
        return switch (outcome) {
            case PLAYER_WIN -> "You win this round!";
            case CPU_WIN -> "Computer wins this round!";
            case TIE -> "Tie!";
        };
    }

    // ------------------------------------------------------------- database

    // A failure here must never stop the game from starting.
    private void openDatabase() {
        try {
            DBManager.getInstance();
            databaseReady = true;
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Could not open the database", ex);
            databaseReady = false;
            JOptionPane.showMessageDialog(this,
                    "The game database could not be opened. Another copy of the game may\n"
                    + "already be running. You can still play, but stats and history\n"
                    + "will not be saved or shown.",
                    "Database unavailable", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveRound(RoundResult result) {
        if (!databaseReady) {
            return;
        }
        try {
            recorder.record(game, result);
        } catch (SQLException ex) {
            databaseReady = false;      // stop retrying (and stop nagging) after the first failure
            showDatabaseError(ex);
        }
    }

    private boolean requireDatabase() {
        if (!databaseReady) {
            JOptionPane.showMessageDialog(this,
                    "The database is not available, so this can't be shown.",
                    "Database unavailable", JOptionPane.INFORMATION_MESSAGE);
        }
        return databaseReady;
    }

    private void showDatabaseError(SQLException ex) {
        LOGGER.log(Level.SEVERE, "Database error", ex);
        JOptionPane.showMessageDialog(this,
                "A database problem occurred:\n" + ex.getMessage()
                + "\n\nThe game can carry on, but it may not be saved.",
                "Database error", JOptionPane.ERROR_MESSAGE);
    }

    // ----------------------------------------------------------------- menus

    private JMenuBar buildMenuBar() {
        int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic(KeyEvent.VK_G);
        JMenuItem newGameItem = menuItem("New Game", e -> startNewGame());
        newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcut));
        gameMenu.add(newGameItem);
        gameMenu.add(menuItem("Change Player Name...", e -> changePlayerName()));
        gameMenu.addSeparator();
        JMenuItem exitItem = menuItem("Exit", e -> dispatchEvent(
                new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcut));
        gameMenu.add(exitItem);

        JMenu statsMenu = new JMenu("Stats");
        statsMenu.setMnemonic(KeyEvent.VK_S);
        statsMenu.add(menuItem("Leaderboard", e -> showLeaderboard()));
        statsMenu.add(menuItem("My Stats", e -> showMyStats()));

        JMenu historyMenu = new JMenu("History");
        historyMenu.setMnemonic(KeyEvent.VK_H);
        historyMenu.add(menuItem("Recent Rounds", e -> showRecentRounds()));
        historyMenu.add(menuItem("My Rounds", e -> showMyRounds()));
        historyMenu.addSeparator();
        historyMenu.add(menuItem("Clear History...", e -> clearHistory()));

        JMenuBar bar = new JMenuBar();
        bar.add(gameMenu);
        bar.add(statsMenu);
        bar.add(historyMenu);
        return bar;
    }

    private static JMenuItem menuItem(String text, java.awt.event.ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(action);
        return item;
    }

    private void changePlayerName() {
        String name = askPlayerName(this, playerName);
        if (name != null) {
            playerName = name;
            startNewGame();     // a new name means a new game
        }
    }

    private void showLeaderboard() {
        if (!requireDatabase()) {
            return;
        }
        try {
            TableDialog.leaderboard(this, dao.getLeaderboard()).setVisible(true);
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void showMyStats() {
        if (!requireDatabase()) {
            return;
        }
        try {
            PlayerStats stats = dao.getStats(playerName);
            String message = (stats == null)
                    ? playerName + " has not finished a game yet."
                    : playerName + ": " + stats.getWins() + " wins, " + stats.getLosses() + " losses";
            JOptionPane.showMessageDialog(this, message, "My Stats", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void showRecentRounds() {
        if (!requireDatabase()) {
            return;
        }
        try {
            TableDialog.rounds(this, "Recent Rounds", dao.getRecentRounds(HISTORY_LIMIT)).setVisible(true);
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void showMyRounds() {
        if (!requireDatabase()) {
            return;
        }
        try {
            TableDialog.rounds(this, "My Rounds",
                    dao.getRoundsForPlayer(playerName, HISTORY_LIMIT)).setVisible(true);
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void clearHistory() {
        if (!requireDatabase()) {
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete the whole round history? This cannot be undone.\n"
                + "(Win/loss totals on the leaderboard are kept.)",
                "Clear History", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.clearHistory();
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    /**
     * Asks for a player name until a valid one is typed or the user cancels. The name is
     * trimmed, and blank, too-long or reserved names are rejected with a message.
     *
     * @param parent  the window to centre the dialogs on (may be null)
     * @param initial the name to pre-fill the box with
     * @return the valid, trimmed name, or null if the user cancelled
     */
    public static String askPlayerName(Component parent, String initial) {
        String suggestion = initial;
        while (true) {
            Object input = JOptionPane.showInputDialog(parent,
                    "Enter your name (up to " + PlayerNames.MAX_LENGTH + " characters):",
                    "Player name", JOptionPane.QUESTION_MESSAGE, null, null, suggestion);
            if (input == null) {
                return null;
            }
            String name = input.toString().trim();
            String problem = PlayerNames.validate(name);
            if (problem == null) {
                return name;
            }
            JOptionPane.showMessageDialog(parent, problem, "Invalid name", JOptionPane.WARNING_MESSAGE);
            suggestion = name;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        drawButton = new javax.swing.JButton();
        resultLabel = new javax.swing.JLabel();
        scoreLabel = new javax.swing.JLabel();
        playerCardLabel = new javax.swing.JLabel();
        cpuCardLabel = new javax.swing.JLabel();
        newGameButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        drawButton.setText("Draw Cards");
        drawButton.addActionListener(this::drawButtonActionPerformed);

        resultLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        resultLabel.setText("resultLabel");
        resultLabel.setToolTipText("");
        resultLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        scoreLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        scoreLabel.setText("scoreLabel");
        scoreLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        playerCardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        cpuCardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        newGameButton.setText("New Game");
        newGameButton.addActionListener(this::newGameButtonActionPerformed);

        jLabel1.setText("playerNameLabe");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("cpuNameLabel");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scoreLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(resultLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(playerCardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cpuCardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(drawButton, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(newGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(159, 159, 159))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 361, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(117, 117, 117))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(scoreLabel)
                .addGap(20, 20, 20)
                .addComponent(resultLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 331, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playerCardLabel)
                    .addComponent(cpuCardLabel))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(drawButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void drawButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawButtonActionPerformed
        RoundResult result = game.playRound();

        playerCardLabel.setIcon(CardImages.iconFor(result.getPlayerCard()));
        cpuCardLabel.setIcon(CardImages.iconFor(result.getCpuCard()));
        resultLabel.setText(describe(result.getOutcome()));
        scoreLabel.setText("Score: You " + result.getPlayerScore()
                + " — Computer " + result.getCpuScore());

        if (result.isGameOver()) {
            resultLabel.setText("Game Over! " + result.getWinner().getName() + " wins!");
            drawButton.setEnabled(false);
        }
        saveRound(result);
    }//GEN-LAST:event_drawButtonActionPerformed

    private void newGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameButtonActionPerformed
        startNewGame();
    }//GEN-LAST:event_newGameButtonActionPerformed

    /**
     * Starts the program: asks for a name, then opens the game window.
     *
     * @param args the command line arguments (not used)
     */
    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Ask for a name, then create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            String name = askPlayerName(null, PlayerNames.DEFAULT_NAME);
            if (name != null) {     // cancelled -> nothing is opened and the program simply ends
                new WarGameFrame(name).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cpuCardLabel;
    private javax.swing.JButton drawButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton newGameButton;
    private javax.swing.JLabel playerCardLabel;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JLabel scoreLabel;
    // End of variables declaration//GEN-END:variables
}
