package program.game;


import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Task {
    private JFrame frame;
    private JPanel panel;
    private JLabel promptLabel, timerLabel, resultLabel, leaderboardLabel;
    private JButton rockButton, paperButton, scissorsButton, newGameButton;
    private long startTime, endTime;
    private String playerName;
    private int moves;
    private static final String DB_URL = "jdbc:sqlite:leaderboard.db";
    private Random random = new Random();

    public Task() {
        frame = new JFrame("Rock-Paper-Scissors Game");
        panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        promptLabel = new JLabel("Press 'New Game' to Start", SwingConstants.CENTER);
        timerLabel = new JLabel("Time: 0.0 seconds", SwingConstants.CENTER);
        resultLabel = new JLabel("", SwingConstants.CENTER);
        leaderboardLabel = new JLabel("Leaderboard:", SwingConstants.CENTER);

        newGameButton = new JButton("New Game");
        rockButton = new JButton("Rock");
        paperButton = new JButton("Paper");
        scissorsButton = new JButton("Scissors");

        panel.add(promptLabel);
        panel.add(timerLabel);
        panel.add(newGameButton);
        panel.add(rockButton);
        panel.add(paperButton);
        panel.add(scissorsButton);
        panel.add(resultLabel);
        panel.add(leaderboardLabel);

        addActionListeners();
        disableGameButtons();

        frame.add(panel);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void addActionListeners() {
        newGameButton.addActionListener(e -> startNewGame());
        rockButton.addActionListener(e -> playGame("Rock"));
        paperButton.addActionListener(e -> playGame("Paper"));
        scissorsButton.addActionListener(e -> playGame("Scissors"));
    }

    private void startNewGame() {
        playerName = JOptionPane.showInputDialog(frame, "Enter your name:");
        if (playerName == null || playerName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Name cannot be empty!");
            return;
        }
        promptLabel.setText("Choose Rock, Paper, or Scissors");
        timerLabel.setText("Time: 0.0 seconds");
        resultLabel.setText("");
        enableGameButtons();
        startTime = System.currentTimeMillis();
        moves = 0;
    }

    private void playGame(String playerChoice) {
        moves++;
        String[] choices = {"Rock", "Paper", "Scissors"};
        String computerChoice = choices[random.nextInt(3)];
        String result = determineWinner(playerChoice, computerChoice);
        endTime = System.currentTimeMillis();

        double timeTaken = (endTime - startTime) / 1000.0;
        timerLabel.setText(String.format("Time: %.2f seconds", timeTaken));
        resultLabel.setText(String.format("You: %s | Computer: %s | %s", playerChoice, computerChoice, result));

        saveGameResult(playerName, result, timeTaken);
        updateLeaderboard();
        disableGameButtons();
    }

    private String determineWinner(String player, String computer) {
        if (player.equals(computer)) {
            return "It's a Draw!";
        }
        if ((player.equals("Rock") && computer.equals("Scissors")) ||
            (player.equals("Scissors") && computer.equals("Paper")) ||
            (player.equals("Paper") && computer.equals("Rock"))) {
            return "You Win!";
        }
        return "Computer Wins!";
    }

    private void saveGameResult(String name, String result, double timeTaken) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO leaderboard VALUES (?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, result.equals("You Win!") ? "Win" : result.equals("Computer Wins!") ? "Loss" : "Draw");
            stmt.setDouble(3, timeTaken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLeaderboard() {
        StringBuilder leaderboard = new StringBuilder("Leaderboard:\n");
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT player_name, COUNT(*) AS wins FROM leaderboard WHERE result='Win' GROUP BY player_name ORDER BY wins DESC, time_taken ASC LIMIT 5")) {
            while (rs.next()) {
                leaderboard.append(rs.getString("player_name"))
                        .append(" - Wins: ")
                        .append(rs.getInt("wins"))
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        leaderboardLabel.setText("<html>" + leaderboard.toString().replace("\n", "<br>") + "</html>");
    }

    private void enableGameButtons() {
        rockButton.setEnabled(true);
        paperButton.setEnabled(true);
        scissorsButton.setEnabled(true);
    }

    private void disableGameButtons() {
        rockButton.setEnabled(false);
        paperButton.setEnabled(false);
        scissorsButton.setEnabled(false);
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS leaderboard (player_name VARCHAR(50), result VARCHAR(10), time_taken DOUBLE)");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(Task::new);
    }
}

