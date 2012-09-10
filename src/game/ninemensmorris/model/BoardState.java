package game.ninemensmorris.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class BoardState {
	public static final int NUMBER_OF_POSITIONS = 24;
	public static final int NUMBER_OF_STARTING_PIECES = 9;
	public static final boolean IS_FLYING_ALLOWED = false;
	
	public static final List<List<Integer>> POSITION_TO_NEIGHBOURS;
	private static final Integer[][] positionToNeighboursArray = {
		{1, 9},
		{0, 2, 4},
		{1, 14},
		{4, 10},
		{1, 3, 5, 7},
		{4, 13},
		{7, 11},
		{4, 6, 8},
		{7, 12},
		{0, 10, 21},
		{3, 9, 11, 18},
		{6, 10, 15},
		{8, 13, 17},
		{5, 12, 14, 20},
		{2, 13, 23},
		{11, 16},
		{15, 17, 19},
		{12, 16},
		{10, 19},
		{16, 18, 20, 22},
		{13, 19},
		{9, 22},
		{19, 21, 23},
		{14, 22},
	};
	public static final List<List<Integer>> POSSIBLE_MILLS;
	private static final Integer[][] possibleMillsArrsy = {
		{0, 1, 2},
		{3, 4, 5},
		{6, 7, 8},
		{9, 10, 11},
		{12, 13, 14},
		{15, 16, 17},
		{18, 19, 20},
		{21, 22, 23},
		{0, 9, 21},
		{3, 10, 18},
		{6, 11, 15},
		{1, 4, 7 },
		{16, 19, 22},
		{8, 12, 17},
		{5, 13, 20},
		{2, 14, 23},
	};
	
	static {
		List<List<Integer>> posToNeigh = new ArrayList<List<Integer>>();
		
		for (int i = 0; i < positionToNeighboursArray.length; i++) {
			posToNeigh.add(Collections.unmodifiableList(Arrays.asList(positionToNeighboursArray[i])));
		}
		
		POSITION_TO_NEIGHBOURS = Collections.unmodifiableList(posToNeigh);
		
		List<List<Integer>> possMills = new ArrayList<List<Integer>>();
		
		for (int i = 0; i < possibleMillsArrsy.length; i++) {
			possMills.add(Collections.unmodifiableList(Arrays.asList(possibleMillsArrsy[i])));
		}
		
		POSSIBLE_MILLS = Collections.unmodifiableList(possMills);
	}
	
	private static long[] powersOf3;
	
	static {
		powersOf3 = new long[64];
		powersOf3[0] = 1;
		
		for (int i = 1; i < powersOf3.length; i++) {
			powersOf3[i] = powersOf3[i - 1]  * 3;
		}
	}
	
	private int[] positionsState;
	private int currentPlayer;
	private int[] playerToUnputPieces;
	private int[] playerToRemainingPieces;
	private long boardID;
	
	public BoardState() {
		positionsState = new int[24];
		for (int i = 0; i < positionsState.length; i++) {
			positionsState[i] = 0;
		}
		
		currentPlayer = 0;
		playerToUnputPieces = new int[2];
		playerToUnputPieces[0] = playerToUnputPieces[1] = NUMBER_OF_STARTING_PIECES;
		playerToRemainingPieces = new int[2];
		playerToRemainingPieces[0] = playerToRemainingPieces[1] = NUMBER_OF_STARTING_PIECES;
		boardID = 0;
	}
	
	public BoardState(BoardState state) {
		positionsState = state.positionsState.clone();
		currentPlayer = state.currentPlayer;
		playerToUnputPieces = state.playerToUnputPieces.clone();
		playerToRemainingPieces = state.playerToRemainingPieces.clone();
		boardID = state.boardID;
	}
	
	public int getPositionState(int position) {
		if (position < 0 || position >= NUMBER_OF_POSITIONS) {
			throw new IllegalArgumentException();
		}

		return positionsState[position];
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}
	
	public int getOtherPlayer() {
		return (currentPlayer + 1) % 2;
	}
	
	public int getUnputPiecesOfPlayer(int player) {
		return playerToUnputPieces[player];
	}

	public int getUnputPiecesOfCurrentPlayer() {
		return playerToUnputPieces[currentPlayer];
	}

	public int getUnputPiecesOfOtherPlayer() {
		return playerToUnputPieces[getOtherPlayer()];
	}
	
	public int getRemainingPiecesOfPlayer(int player) {
		return playerToRemainingPieces[player];
	}
	
	public int getRemainingPiecesOfCurrentPlayer() {
		return playerToRemainingPieces[currentPlayer];
	}

	public int getRemainingPiecesOfOtherPlayer() {
		return playerToRemainingPieces[getOtherPlayer()];
	}

	public long getBoardID() {
		return boardID;
	}
	
	public boolean hasCurrentPlayerLost() {
		return getRemainingPiecesOfCurrentPlayer() < 3
				|| getValidMoves(null).isEmpty();
	}
	
	private void decreaseUnputPiecesOfCurrentPlayer() {
		boardID -= (NUMBER_OF_STARTING_PIECES * 2
				- playerToUnputPieces[0] - playerToUnputPieces[1])
				* powersOf3[NUMBER_OF_POSITIONS + 1];
		
		playerToUnputPieces[currentPlayer]--;
		
		boardID += (NUMBER_OF_STARTING_PIECES * 2
				- playerToUnputPieces[0] - playerToUnputPieces[1])
				* powersOf3[NUMBER_OF_POSITIONS + 1];
		
	}
	
	private void increaseUnputPiecesOfCurrentPlayer() {
		boardID -= (NUMBER_OF_STARTING_PIECES * 2
				- playerToUnputPieces[0] - playerToUnputPieces[1])
				* powersOf3[NUMBER_OF_POSITIONS + 1];
		
		playerToUnputPieces[currentPlayer]++;
		
		boardID += (NUMBER_OF_STARTING_PIECES * 2
				- playerToUnputPieces[0] - playerToUnputPieces[1])
				* powersOf3[NUMBER_OF_POSITIONS + 1];
		
	}
	
	private void removeFromBoard(int position) {
		boardID -= positionsState[position] * powersOf3[position];
		
		positionsState[position] = 0;
	}
	
	private void putOnBoard(int position, int player) {
		positionsState[position] = player + 1;
		
		boardID += positionsState[position] * powersOf3[position];
	}
	
	private void tooglePlayer() {
		boardID -= currentPlayer * powersOf3[NUMBER_OF_STARTING_PIECES];
		
		currentPlayer = getOtherPlayer();
		
		boardID += currentPlayer * powersOf3[NUMBER_OF_STARTING_PIECES];
	}
	
	public void makeMove(Move move) {
		if (move.getFromPosition() == -1) {
			decreaseUnputPiecesOfCurrentPlayer();
		} else {
			removeFromBoard(move.getFromPosition());
		}
		
		putOnBoard(move.getToPosition(), currentPlayer);
		
		if (move.getPositionOfTakenPiece() != -1) {
			removeFromBoard(move.getPositionOfTakenPiece());
			playerToRemainingPieces[getOtherPlayer()]--;
		}
		
		tooglePlayer();
	}
	
	public void undoMove(Move move) {
		tooglePlayer();

		if (move.getFromPosition() == -1) {
			increaseUnputPiecesOfCurrentPlayer();
		} else {
			putOnBoard(move.getFromPosition(), currentPlayer);
		}
		
		removeFromBoard(move.getToPosition());
		
		if (move.getPositionOfTakenPiece() != -1) {
			putOnBoard(move.getPositionOfTakenPiece(), getOtherPlayer());
			playerToRemainingPieces[getOtherPlayer()]++;
		}
	}
	
	public boolean areAllPiecesFromMill(int player) {
		for (int i = 0; i < positionsState.length; i++) {
			if (positionsState[i] == player + 1 && !doesPieceCompleteMill(-1, i, player)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean doesPieceCompleteMill(int removeFromPosition, int position, int player) {
		int positionState =  player + 1;

		for (List<Integer> millCoords : POSSIBLE_MILLS) {
			if (millCoords.contains(position)) {
				boolean result = true;
				for (int i : millCoords) {
					if (i == removeFromPosition) {
						result = false;
					} else if (i != position && positionsState[i] != positionState) {
						result = false;
					}
				}
				if (result) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isPieceFromMill(int position) {
		if (positionsState[position] != 0) {
			return doesPieceCompleteMill(-1, position, positionsState[position] - 1);
		}
		
		return false;
	}
	
	public boolean isMoveValid(Move move) {
		if (positionsState[move.getToPosition()] != 0) {
			return false;
		}
			
		if (move.getFromPosition() != -1) {
			if (positionsState[move.getFromPosition()] - 1 != currentPlayer) {
				return false;
			}
			if ((getRemainingPiecesOfCurrentPlayer() > 3 || !IS_FLYING_ALLOWED)
					&& !POSITION_TO_NEIGHBOURS.get(move.getFromPosition()).contains(move.getToPosition())) {
				return false;
			}
			if (getUnputPiecesOfCurrentPlayer() > 0) {
				return false;
			}
		} else {
			if (getUnputPiecesOfCurrentPlayer() == 0) {
				return false;
			}
		}
		
		if (move.getPositionOfTakenPiece() != -1) {
			if (positionsState[move.getPositionOfTakenPiece()] - 1 != getOtherPlayer()) {
				return false;
			}
			
			if (isPieceFromMill(move.getPositionOfTakenPiece()) && !areAllPiecesFromMill(getOtherPlayer())) {
				return false;
			}
		}
		
		return true;
	}
	
	private static final class ValuedMove implements Comparable<ValuedMove> {
		private Move move;
		private int value;
		
		public ValuedMove(Move move, int value) {
			this.move = move;
			this.value = value;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((move == null) ? 0 : move.hashCode());
			result = prime * result + value;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ValuedMove other = (ValuedMove) obj;
			if (move == null) {
				if (other.move != null) {
					return false;
				}
			} else if (!move.equals(other.move)) {
				return false;
			}
			if (value != other.value) {
				return false;
			}
			return true;
		}

		public Move getMove() {
			return move;
		}
		
		@SuppressWarnings("unused")
		public int getValue() {
			return value;
		}

		@Override
		public int compareTo(ValuedMove o) {
			if (value > o.value) {
				return -1;
			} else if (value < o.value) {
				return 1;
			} else if (move.hashCode() < o.move.hashCode()) {
				return -1;
			} else if (move.hashCode() > o.move.hashCode()) {
				return 1;
			} else if (move.getFromPosition() - o.move.getFromPosition() != 0) {
				return move.getFromPosition() - o.move.getFromPosition();
			} else if (move.getToPosition() - o.move.getToPosition() != 0) {
				return move.getToPosition() - o.move.getToPosition();
			} else if (move.getPositionOfTakenPiece() - o.move.getPositionOfTakenPiece() != 0) {
				return move.getPositionOfTakenPiece() - o.move.getPositionOfTakenPiece();
			} else {
				return 0;
			}
		}
	}
	
	private void addPossibleMillTakes(SortedSet<ValuedMove> sortedMoves,
			Move move, MoveEvaluationFunction evaluationFunction) {
		boolean areAllOtherPlayerPiecesFromMill = areAllPiecesFromMill(getOtherPlayer());
		
		for (int i = 0; i < positionsState.length; i++) {
			if (positionsState[i] == getOtherPlayer() + 1) {
				if (areAllOtherPlayerPiecesFromMill || !doesPieceCompleteMill(-1, i, getOtherPlayer())) {
					move = new Move(move.getFromPosition(), move.getToPosition(), i);
					sortedMoves.add(new ValuedMove(move, evaluationFunction.evaluate(this, move)));
				}
			}
		}
	}
	
	public List<Move> getValidMoves(MoveEvaluationFunction evaluationFunction) {
		if (evaluationFunction == null) {
			evaluationFunction = new MoveEvaluationFunction() {
				@Override
				public int evaluate(BoardState boardState, Move move) {
					return 0;
				}
			};
		}

		SortedSet<ValuedMove> sortedMoves = new TreeSet<ValuedMove>();
		
		if (getUnputPiecesOfCurrentPlayer() > 0) {
			for (int i = 0; i < positionsState.length; i++) {
				if (positionsState[i] == 0) {
					Move move = new Move(i);
					if (doesPieceCompleteMill(-1, i, currentPlayer)) {
						addPossibleMillTakes(sortedMoves, move, evaluationFunction);
					} else {
						sortedMoves.add(new ValuedMove(move, evaluationFunction.evaluate(this, move)));
					}
				}
			}
		} else {
			if (getRemainingPiecesOfCurrentPlayer() > 3 || !IS_FLYING_ALLOWED) {
				for (int i = 0; i < positionsState.length; i++) {
					if (positionsState[i] == currentPlayer + 1) {
						for (int neighbour : POSITION_TO_NEIGHBOURS.get(i)) {
							if (positionsState[neighbour] == 0) {
								Move move = new Move(i, neighbour);
								if (doesPieceCompleteMill(i, neighbour, currentPlayer)) {
									addPossibleMillTakes(sortedMoves, move, evaluationFunction);
								} else {
									sortedMoves.add(new ValuedMove(move, evaluationFunction.evaluate(this, move)));
								}
							}
						}
					}
				}
			} else {
				for (int i = 0; i < positionsState.length; i++) {
					if (positionsState[i] == currentPlayer + 1) {
						for (int j = 0; j < positionsState.length; j++) {
							if (positionsState[j] == 0) {
								Move move = new Move(i, j);
								if (doesPieceCompleteMill(i, j, currentPlayer)) {
									addPossibleMillTakes(sortedMoves, move, evaluationFunction);
								} else {
									sortedMoves.add(new ValuedMove(move, evaluationFunction.evaluate(this, move)));
								}
							}
						}
					}
				}
			}
		}
		
		List<Move> result = new ArrayList<Move>();
		
		for (ValuedMove valuedMove : sortedMoves) {
			result.add(valuedMove.getMove());
		}
		
		return result;
	}
}
