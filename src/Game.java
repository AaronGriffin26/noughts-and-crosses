import java.util.InputMismatchException;
import java.util.Scanner;

public class Game {
	private BoardSlot[] slots = new BoardSlot[] {
			BoardSlot.Blank, BoardSlot.Blank, BoardSlot.Blank,
			BoardSlot.Blank, BoardSlot.Blank, BoardSlot.Blank,
			BoardSlot.Blank, BoardSlot.Blank, BoardSlot.Blank
	};
	private boolean playAsX;
	private boolean finished;
	private boolean xIsWinner;
	private boolean oIsWinner;
	private BoardSlot playerSlot;
	private BoardSlot aiSlot;

	public Game(boolean playAsX, int difficulty) {
		this.playAsX = playAsX;
		playerSlot = (playAsX ? BoardSlot.Cross : BoardSlot.Nought);
		aiSlot = (playAsX ? BoardSlot.Nought : BoardSlot.Cross);
		AI.setSettings(!playAsX, difficulty);
	}

	/**
	 * Run the game. It will continue until the board is filled or a player wins.
	 */
	public void run() {
		boolean aiTurn = true;
		while(isPlaying()) {
			if(aiTurn)
				processAIMove();
			else {
				displayState();
				processMove(nextMove());
			}
			evaluateWin();
			aiTurn = !aiTurn;
		}
		displayState();
		if(won())
			displayVictory();
		else if(aiWon())
			displayGameOver();
		else
			displayDraw();
	}

	private boolean isPlaying() {
		return !finished;
	}

	private void displayState() {
		System.out.println(Board.getTextOutput(slots));
	}

	/**
	 * Asks the player which move they would like to make.
	 *
	 * @return One-based slot index
	 */
	private int nextMove() {
		System.out.println("What is your next move? (1-9)");
		var scanner = new Scanner(System.in);
		while(true) {
			try {
				int choice = scanner.nextInt();
				if(choice >= 1 && choice <= 9) {
					if(slots[choice - 1] == BoardSlot.Blank)
						return choice;
					System.out.println(choice + " is already occupied.");
				}
			}
			catch(InputMismatchException ignored) {
				scanner.next();
			}
			System.out.println("""
					7 8 9
					4 5 6
					1 2 3
					Please enter a number between 1 and 9.""");
		}
	}

	private void processMove(int choice) {
		slots[choice - 1] = playerSlot;
	}

	private void processAIMove() {
		int choice = AI.getNextMove(slots);
		if(slots[choice - 1] != BoardSlot.Blank) {
			AI.getNextMove(slots); // Breakpoint here to debug bad AI
			throw new IllegalStateException("Slot " + choice + " is already filled in");
		}
		slots[choice - 1] = aiSlot;
	}

	private void displayGameOver() {
		System.out.println("The computer has beaten you! You lose.");
	}

	private void displayVictory() {
		System.out.println("You beat the computer!");
	}

	private void displayDraw() {
		System.out.println("The board was filled in. It's a draw.");
	}

	/**
	 * Determines if a player has won, or declares a draw if the board is filled in.
	 */
	public void evaluateWin() {
		if(Board.isComplete(slots))
			finished = true;
		BoardSlot lineOwner = Board.getWinner(slots);
		if(lineOwner != BoardSlot.Blank)
			finished = true;
		xIsWinner = (lineOwner == BoardSlot.Cross);
		oIsWinner = (lineOwner == BoardSlot.Nought);
	}

	public boolean won() {
		if(playAsX)
			return (finished && xIsWinner);
		return (finished && oIsWinner);
	}

	public boolean aiWon() {
		if(playAsX)
			return (finished && oIsWinner);
		return (finished && xIsWinner);
	}
}
