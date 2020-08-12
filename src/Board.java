import java.util.ArrayList;

public class Board {
	private static final String BOARD = """
			6 | 7 | 8
			---------
			3 | 4 | 5
			---------
			0 | 1 | 2""";
	private static final int CENTER = 4;
	private static final int TOP_LEFT = 6;
	private static final int TOP_RIGHT = 8;
	private static final int BOTTOM_LEFT = 0;
	private static final int BOTTOM_RIGHT = 2;

	public static String getTextOutput(BoardSlot[] slots) {
		String board = BOARD;
		for(int i = 0; i < 9; i++) {
			String visual = switch(slots[i]) {
				case Blank -> " ";
				case Nought -> "O";
				case Cross -> "X";
			};
			board = board.replace(String.valueOf(i), visual);
		}
		return board;
	}

	/**
	 * Gives a list of move-indexes that are not occupied.
	 *
	 * @param slots Layout to evaluate
	 * @return List of one-based move indexes
	 */
	public static ArrayList<Integer> getAvailableSlots(BoardSlot[] slots) {
		var available = new ArrayList<Integer>();
		for(int i = 0; i < 9; i++) {
			if(slots[i] == BoardSlot.Blank)
				available.add(i + 1);
		}
		return available;
	}

	/**
	 * Indicates a winner in a provided board layout.
	 *
	 * @param slots Layout to evaluate
	 * @return X or O for a formed line, or Blank if neither made a line
	 */
	public static BoardSlot getWinner(BoardSlot[] slots) {
		for(int i = 0; i < 3; i++) {
			BoardSlot rowSlot = slots[i * 3];
			if(rowSlot != BoardSlot.Blank && rowSlot == slots[i * 3 + 1] && rowSlot == slots[i * 3 + 2])
				return rowSlot;
			BoardSlot columnSlot = slots[i];
			if(columnSlot != BoardSlot.Blank && columnSlot == slots[i + 3] && columnSlot == slots[i + 6])
				return columnSlot;
		}
		if(slots[CENTER] != BoardSlot.Blank) {
			if(slots[TOP_LEFT] == slots[CENTER] && slots[BOTTOM_RIGHT] == slots[CENTER])
				return slots[CENTER];
			if(slots[TOP_RIGHT] == slots[CENTER] && slots[BOTTOM_LEFT] == slots[CENTER])
				return slots[CENTER];
		}
		return BoardSlot.Blank;
	}

	public static boolean isComplete(BoardSlot[] slots) {
		for(int i = 0; i < 9; i++) {
			if(slots[i] == BoardSlot.Blank)
				return false;
		}
		return true;
	}
}
