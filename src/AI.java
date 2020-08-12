import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class AI {
	private static final int CENTER = 4;
	private static final int TOP_LEFT = 6;
	private static final int TOP_RIGHT = 8;
	private static final int BOTTOM_LEFT = 0;
	private static final int BOTTOM_RIGHT = 2;

	private static int level = 0;
	private static BoardSlot aiSlot = BoardSlot.Blank;
	private static BoardSlot playerSlot = BoardSlot.Blank;

	/**
	 * Initializes the AI with who they are playing as and what competence level they are on.
	 *
	 * @param playAsX  True for X, false for O
	 * @param newLevel Level of thinking
	 */
	public static void setSettings(boolean playAsX, int newLevel) {
		level = newLevel;
		aiSlot = (playAsX ? BoardSlot.Cross : BoardSlot.Nought);
		playerSlot = (aiSlot == BoardSlot.Cross ? BoardSlot.Nought : BoardSlot.Cross);
	}

	/**
	 * Gives the AI's choice for a provided board.
	 *
	 * @param slots Board layout
	 * @return One-based move index
	 */
	public static int getNextMove(BoardSlot[] slots) {
		return switch(level) {
			case 0 -> randomMove(slots);
			case 1 -> attackMove(slots);
			case 2 -> defensiveMove(slots);
			case 3 -> followUpMove(slots);
			default -> perfectMove(slots);
		};
	}

	/**
	 * Picks an available slot at pure random from 1 to 9.
	 *
	 * @param slots Current markings
	 * @return One-based move index
	 */
	private static int randomMove(BoardSlot[] slots) {
		var available = Board.getAvailableSlots(slots);
		var random = new Random();
		int index = random.nextInt(available.size());
		return available.get(index);
	}

	/**
	 * Picks the slot that will win the game, otherwise a random slot.
	 *
	 * @param slots Current markings
	 * @return One-based move index
	 */
	private static int attackMove(BoardSlot[] slots) {
		int result = matchMove(slots, aiSlot);
		if(result > 0)
			return result;
		return randomMove(slots);
	}

	/**
	 * Picks the slot that will win the game.
	 * If there is no such slot, pick the slot that prevents the player from winning.
	 * If there is no such slot, pick a random slot.
	 *
	 * @param slots Current markings
	 * @return One-based move index
	 */
	private static int defensiveMove(BoardSlot[] slots) {
		int result = matchMove(slots, aiSlot);
		if(result > 0)
			return result;
		result = matchMove(slots, playerSlot);
		if(result > 0)
			return result;
		return randomMove(slots);
	}

	/**
	 * If the game just started, pick a random slot that is not the best.
	 * Then it will try to pick the slot that will win the game.
	 * If there is no such slot, pick the slot that prevents the player from winning.
	 * If there is no such slot, pick the slot with the best odds of winning using minimax.
	 *
	 * @param slots Current markings
	 * @return One-based move index
	 */
	private static int followUpMove(BoardSlot[] slots) {
		int newMove = perfectMove(slots);
		boolean boardIsClear = true;
		for(BoardSlot s : slots) {
			if(s != BoardSlot.Blank) {
				boardIsClear = false;
				break;
			}
		}
		if(boardIsClear) {
			var random = new Random();
			while(true) {
				int move = random.nextInt(9) + 1;
				if(move != newMove)
					return move;
			}
		}
		return newMove;
	}

	/**
	 * Picks the slot that will win the game.
	 * If there is no such slot, pick the slot that prevents the player from winning.
	 * If there is no such slot, pick the slot with the best odds of winning using minimax.
	 *
	 * @param slots Current markings
	 * @return One-based move index
	 */
	private static int perfectMove(BoardSlot[] slots) {
		int result = matchMove(slots, aiSlot);
		if(result > 0)
			return result;
		result = matchMove(slots, playerSlot);
		if(result > 0)
			return result;
		var available = Board.getAvailableSlots(slots);
		var scores = new HashMap<Integer, Integer>();
		int maxScore = Integer.MIN_VALUE;
		for(int i : available) {
			var newSlots = Arrays.copyOf(slots, 9);
			newSlots[i - 1] = aiSlot;
			int value = minimax(newSlots, 0, false);
			maxScore = Math.max(maxScore, value);
			scores.put(i, value);
		}
		for(int i : scores.keySet()) {
			if(scores.get(i) == maxScore)
				return i;
		}
		return randomMove(slots);
	}

	/**
	 * A method that scores a board layout in favor of the AI.
	 * The depth level brings the score closer to 0, making the score less favorable than those of lower depths.
	 *
	 * @param newSlots Modified layout
	 * @param depth    Iteration level
	 * @param isMax    True for maximization, false for minimization
	 * @return Score
	 */
	private static int minimax(BoardSlot[] newSlots, int depth, boolean isMax) {
		if(Board.getWinner(newSlots) == aiSlot)
			return 10 - depth;
		if(Board.getWinner(newSlots) == playerSlot)
			return -10 + depth;
		if(Board.isComplete(newSlots))
			return 0;
		var available = Board.getAvailableSlots(newSlots);
		if(isMax) {
			int bestValue = Integer.MIN_VALUE;
			for(int i : available) {
				var beyond = Arrays.copyOf(newSlots, 9);
				beyond[i - 1] = aiSlot;
				int newValue = minimax(beyond, depth + 1, false);
				bestValue = Math.max(bestValue, newValue);
			}
			return bestValue;
		}
		else {
			int bestValue = Integer.MAX_VALUE;
			for(int i : available) {
				var beyond = Arrays.copyOf(newSlots, 9);
				beyond[i - 1] = playerSlot;
				int newValue = minimax(beyond, depth + 1, true);
				bestValue = Math.min(bestValue, newValue);
			}
			return bestValue;
		}
	}

	/**
	 * Picks the slot that will win the game for the specified player.
	 *
	 * @param slots  Current markings
	 * @param player Mark to fill in
	 * @return One-based move index, or 0 if no line can be formed.
	 */
	private static int matchMove(BoardSlot[] slots, BoardSlot player) {
		if(slots[CENTER] == BoardSlot.Blank) {
			if(slots[TOP_LEFT] == player && slots[BOTTOM_RIGHT] == player)
				return CENTER + 1;
			if(slots[TOP_RIGHT] == player && slots[BOTTOM_LEFT] == player)
				return CENTER + 1;
		}
		if(slots[CENTER] == player) {
			if(slots[TOP_LEFT] == player && slots[BOTTOM_RIGHT] == BoardSlot.Blank)
				return BOTTOM_RIGHT + 1;
			if(slots[TOP_RIGHT] == player && slots[BOTTOM_LEFT] == BoardSlot.Blank)
				return BOTTOM_LEFT + 1;
			if(slots[BOTTOM_LEFT] == player && slots[TOP_RIGHT] == BoardSlot.Blank)
				return TOP_RIGHT + 1;
			if(slots[BOTTOM_RIGHT] == player && slots[TOP_LEFT] == BoardSlot.Blank)
				return TOP_LEFT + 1;
		}
		for(int i = 0; i < 3; i++) {
			int rowCount = 0;
			int columnCount = 0;
			for(int j = 0; j < 3; j++) {
				rowCount += (slots[i * 3 + j] == player ? 1 : 0);
				columnCount += (slots[i + j * 3] == player ? 1 : 0);
			}
			if(rowCount == 2) {
				for(int j = 0; j < 3; j++) {
					if(slots[i * 3 + j] == BoardSlot.Blank)
						return i * 3 + j + 1;
				}
			}
			if(columnCount == 2) {
				for(int j = 0; j < 3; j++) {
					if(slots[i + j * 3] == BoardSlot.Blank)
						return i + j * 3 + 1;
				}
			}
		}
		return 0;
	}
}
