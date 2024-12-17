package program;

import java.util.*;
import java.time.Duration;
import java.time.Instant;

class Player {
    private String name;
    private long timeTaken;
    private String result;

    public Player(String name, long timeTaken, String result) {
        this.name = name;
        this.timeTaken = timeTaken;
        this.result = result;
    }

    public String getName() {
    	return name;
    	}
    public long getTimeTaken() {
    	return timeTaken;
    	}
    public String getResult() {
        return result;
        }

    @Override
    public String toString() {
        return name + " - " + result + " - " + timeTaken + "ms";
    }
}

public class RockPaperScissors {
    private static final String[] OPTIONS = {"Rock", "Paper", "Scissors"};
    private static List<Player> leaderboard = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean keepPlaying = true;

        System.out.println("Welcome to Rock-Paper-Scissors Game!");

        while (keepPlaying) {
            System.out.println("\nPress 'N' to start a New Game or 'L' to view Leaderboard. Press 'Q' to Quit.");
            char choice = scanner.next().toUpperCase().charAt(0);

            switch (choice) {
                case 'N':
                    startNewGame();
                    break;
                case 'L':
                    displayLeaderboard();
                    break;
                case 'Q':
                    System.out.println("Thanks for playing! Goodbye!");
                    keepPlaying = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void startNewGame() {
        System.out.println("Enter your name: ");
        scanner.nextLine(); // Consume newline
        String playerName = scanner.nextLine();

        System.out.println("\nChoose one: Rock, Paper, or Scissors.");
        System.out.println("Enter 0 for Rock, 1 for Paper, 2 for Scissors.");

        Instant startTime = Instant.now(); // Start Timer

        int playerChoice = getPlayerChoice();
        int computerChoice = getComputerChoice();

        System.out.println("\nYou chose: " + OPTIONS[playerChoice]);
        System.out.println("Computer chose: " + OPTIONS[computerChoice]);

        String result = determineWinner(playerChoice, computerChoice);
        Instant endTime = Instant.now(); // End Timer

        long timeElapsed = Duration.between(startTime, endTime).toMillis();
        System.out.println("\nResult: " + result);
        System.out.println("Time taken: " + timeElapsed + " ms");

        // Add to leaderboard
        leaderboard.add(new Player(playerName, timeElapsed, result));
    }

    private static int getPlayerChoice() {
        int choice = -1;
        while (choice < 0 || choice > 2) {
            System.out.print("Enter your choice (0/1/2): ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice < 0 || choice > 2) {
                    System.out.println("Invalid choice. Please choose 0, 1, or 2.");
                }
            } else {
                System.out.println("Please enter a valid number (0, 1, or 2).");
                scanner.next(); // Clear invalid input
            }
        }
        return choice;
    }

    private static int getComputerChoice() {
        Random random = new Random();
        return random.nextInt(3);
    }

    private static String determineWinner(int playerChoice, int computerChoice) {
        if (playerChoice == computerChoice) {
            return "It's a Draw!";
        } else if ((playerChoice == 0 && computerChoice == 2) || // Rock beats Scissors
                   (playerChoice == 1 && computerChoice == 0) || // Paper beats Rock
                   (playerChoice == 2 && computerChoice == 1)) { // Scissors beat Paper
            return "You Win!";
        } else {
            return "Computer Wins!";
        }
    }

    private static void displayLeaderboard() {
        if (leaderboard.isEmpty()) {
            System.out.println("Leaderboard is empty. Play a game first!");
        } else {
            System.out.println("\n--- Leaderboard ---");
            leaderboard.stream()
                .sorted(Comparator.comparingLong(Player::getTimeTaken)) // Sort by fastest time
                .forEach(System.out::println);
        }
    }
}
