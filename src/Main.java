import java.util.Scanner;

public class Main {
	private static final String INTRO = "Welcome to Tic-Tac-Toe!\nDo you want to be X or O?";
	private static final String COMPUTER_FIRST = "The computer will go first.";
	private static final String PLAY_AGAIN_PROMPT = "Do you want to play again? (yes or no)";
	private static final String DIFFICULTY_GOING_UP = "Let's make it harder...";

	public static void main(String[] args) {
		showMessage(INTRO);
		boolean playAsX = playAsXChoice();
		showMessage(COMPUTER_FIRST);
		boolean playing = true;
		int difficulty = 0;
		while(playing) {
			var game = new Game(playAsX, difficulty);
			game.run();
			if(game.won())
				difficulty++;
			showMessage(PLAY_AGAIN_PROMPT);
			playing = playAgainChoice();
			if(playing && game.won())
				showMessage(DIFFICULTY_GOING_UP);
		}
	}

	/**
	 * Displays text for the player.
	 *
	 * @param message Message template to display
	 */
	private static void showMessage(String message) {
		System.out.println(message);
	}

	/**
	 * Checks which symbol the player wants to be.
	 *
	 * @return True if player is playing as X, false if O.
	 */
	private static boolean playAsXChoice() {
		var scanner = new Scanner(System.in);
		while(true) {
			String choice = scanner.nextLine().toLowerCase();
			if(choice.equals("x"))
				return true;
			if(choice.equals("o"))
				return false;
			System.out.println("Please enter 'X' or 'O'.\n");
		}
	}

	/**
	 * Checks if the player wants to play again.
	 *
	 * @return True if player is playing again, false otherwise.
	 */
	private static boolean playAgainChoice() {
		var scanner = new Scanner(System.in);
		while(true) {
			String choice = scanner.nextLine().toLowerCase();
			if(choice.equals("y") || choice.equals("yes"))
				return true;
			if(choice.equals("n") || choice.equals("no"))
				return false;
			System.out.println("Please enter 'yes' or 'no' if you want to play again.\n");
		}
	}
}
