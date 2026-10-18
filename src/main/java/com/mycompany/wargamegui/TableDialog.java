package com.mycompany.wargamegui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * A small read-only window that shows rows in a {@link JTable}. Used for both the leaderboard
 * and the round history, so the table code is written once.
 */
public class TableDialog extends JDialog {

    private static final int DIALOG_WIDTH = 640;
    private static final int DIALOG_HEIGHT = 360;
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TableDialog(Frame owner, String title, String[] columns, Object[][] rows) {
        super(owner, title, true);

        // a model that refuses edits, so the user cannot type over the data
        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);     // click a header to sort

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonRow = new JPanel();
        buttonRow.add(closeButton);

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonRow, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Builds a dialog listing every player's wins and losses.
     *
     * @param owner the window the dialog belongs to
     * @param stats the leaderboard rows, best first
     * @return the dialog, not yet visible
     */
    public static TableDialog leaderboard(Frame owner, List<PlayerStats> stats) {
        String[] columns = {"Rank", "Player", "Wins", "Losses"};
        Object[][] rows = new Object[stats.size()][];
        for (int i = 0; i < stats.size(); i++) {
            PlayerStats s = stats.get(i);
            rows[i] = new Object[]{i + 1, s.getName(), s.getWins(), s.getLosses()};
        }
        return new TableDialog(owner, "Leaderboard", columns, rows);
    }

    /**
     * Builds a dialog listing rounds from the history.
     *
     * @param owner  the window the dialog belongs to
     * @param title  the dialog title
     * @param rounds the rounds to show, newest first
     * @return the dialog, not yet visible
     */
    public static TableDialog rounds(Frame owner, String title, List<RoundRecord> rounds) {
        String[] columns = {"Played at", "Player", "Computer", "Player card",
            "Computer card", "Result", "Score after"};
        Object[][] rows = new Object[rounds.size()][];
        for (int i = 0; i < rounds.size(); i++) {
            RoundRecord r = rounds.get(i);
            rows[i] = new Object[]{
                r.getPlayedAt().format(TIME_FORMAT),
                r.getPlayerName(),
                r.getCpuName(),
                r.getPlayerCard(),
                r.getCpuCard(),
                r.getResult().replace('_', ' '),
                r.getPlayerScoreAfter() + " - " + r.getCpuScoreAfter()};
        }
        return new TableDialog(owner, title, columns, rows);
    }
}
