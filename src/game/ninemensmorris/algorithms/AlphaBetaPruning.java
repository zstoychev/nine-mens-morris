package game.ninemensmorris.algorithms;

import game.ninemensmorris.model.BoardState;
import game.ninemensmorris.model.BoardStateValue;
import game.ninemensmorris.model.Move;
import game.ninemensmorris.model.MoveEvaluationFunction;
import game.ninemensmorris.model.SimpleMoveEvaluationFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlphaBetaPruning {
	private static final int INFINITY = 1001;
	private static final int WIN_BOARD_VALUE = 1000;
	private static final int END_SEARCH = 10000;

	private BoardState boardState;
	private int maxDepth;
	private int maxTime;
	private long startTime;
	private Map<Long, BoardStateValue> transpositionTable;
	private Move currentBestMove;
	private int currentBestMoveValue;
	private MoveEvaluationFunction moveEvaluationFunction;
	private BoardState currentBoard;
	private boolean doTerminateMove;

	public AlphaBetaPruning(BoardState boardState, int maxDepth, int maxTime) {
		this.boardState = boardState;
		this.maxDepth = maxDepth;
		this.maxTime = maxTime;
		this.moveEvaluationFunction = new SimpleMoveEvaluationFunction();
		this.transpositionTable = new HashMap<Long, BoardStateValue>();
		this.doTerminateMove = false;
		
		this.currentBoard = null;
		this.startTime = 0;
		this.currentBestMove = null;
		currentBestMoveValue = -INFINITY;
	}
	
	public void setBoardState(BoardState boardState) {
		this.currentBoard = boardState;
	}

	public BoardState getBoardState() {
		return currentBoard;
	}
	
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getMaxDepth() {
		return maxDepth;
	}
	
	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public int getMaxTime() {
		return maxTime;
	}
	
	private int getNumberOfAdjacentMoves(int player) {
		int result = 0;

		for (int i = 0; i < BoardState.NUMBER_OF_POSITIONS; i++) {
			if (currentBoard.getPositionState(i) == player + 1) {
				for (int j : BoardState.POSITION_TO_NEIGHBOURS.get(i)) {
					if (currentBoard.getPositionState(j) == 0) {
						result++;
					}
				}
			}
		}
		
		return result;
	}
	
	private int getNumberOfMills(int player) {
		int result = 0;
		
		millCordsLoop:
		for (List<Integer> millCords : BoardState.POSSIBLE_MILLS) {
			for (int i : millCords) {
				if (currentBoard.getPositionState(i) != player + 1) {
					continue millCordsLoop;
				}
			}
			
			result++;
		}
		
		return result;
	}
	
//	private int getNumberOfFormableMills(int player) {
//		int result = 0;
//
//		millCordsLoop:
//		for (List<Integer> millCords : BoardState.possibleMills) {
//			int emptyPosition = -1;
//
//			for (int i : millCords) {
//				if (currentBoard.getPositionState(i) != player + 1) {
//					if (currentBoard.getPositionState(i) == 0 && emptyPosition == -1) {
//						emptyPosition = i;
//					} else {
//						continue millCordsLoop;
//					}
//				}
//			}
//			
//			if (emptyPosition == -1) {
//				continue millCordsLoop;
//			}
//			
//			if (currentBoard.getUnputPiecesOfPlayer(player) > 0) {
//				result++;
//			}
//			
//			for (int neighbour : BoardState.positionToNeighbours.get(emptyPosition)) {
//				if (currentBoard.getPositionState(neighbour) == player + 1) {
//					result++;
//				}
//			}
//		}
//
//		return result;
//	}
	
	private int evaluateCurrentBoard() {
		int result = 0;
		
		result += 10 * (currentBoard.getRemainingPiecesOfCurrentPlayer() - currentBoard.getRemainingPiecesOfOtherPlayer());
		result += 2 * (getNumberOfAdjacentMoves(currentBoard.getCurrentPlayer()) - getNumberOfAdjacentMoves(currentBoard.getOtherPlayer()));
		result += 8 * (getNumberOfMills(currentBoard.getCurrentPlayer()) - getNumberOfMills(currentBoard.getOtherPlayer()));
		//result += 2 * (getNumberOfFormableMills(currentBoard.getCurrentPlayer()) - getNumberOfFormableMills(currentBoard.getOtherPlayer()));
		
		return result;
	}
	int hits = 0;
	private int alphaBetaPrunningSearch(int alpha, int beta, int currentDepth, int remainingDepth) {
		if ((System.currentTimeMillis() - startTime > maxTime && currentBestMove != null) || doTerminateMove) {
			doTerminateMove = false;
			return END_SEARCH;
		}

		BoardStateValue boardComputedValue = transpositionTable.get(currentBoard.getBoardID());
		if (boardComputedValue != null && boardComputedValue.getRemainingDepth() >= remainingDepth) { hits++;
			if (boardComputedValue.hasBeenCut()) {
				alpha = Math.max(alpha, boardComputedValue.getValue());
			}
			
			if (boardComputedValue.couldHaveBeenCutDeeper()) {
				beta = Math.min(beta, boardComputedValue.getValue());
			}
			
			if (!(boardComputedValue.couldHaveBeenCutDeeper() || boardComputedValue.hasBeenCut())
					|| alpha >= beta) {
				if (currentDepth == 0) {
					currentBestMove = boardComputedValue.getFoundBestMove();
					currentBestMoveValue = boardComputedValue.getValue();
				}
	
				return boardComputedValue.getValue();
			}
		}

		List<Move> validMoves = currentBoard.getValidMoves(moveEvaluationFunction);
		if (currentBoard.getRemainingPiecesOfCurrentPlayer() < 3 || validMoves.isEmpty()) {
			return -WIN_BOARD_VALUE;
		}
		
		if (remainingDepth == 0) {
			return evaluateCurrentBoard();
		} else {
			Move nodeBestMove = null;
			int nodeBestValue = -INFINITY;

			for (Move move : validMoves) {
				currentBoard.makeMove(move);
				
				int value = -alphaBetaPrunningSearch(-beta, -alpha, currentDepth + 1, remainingDepth -1);

				currentBoard.undoMove(move);
			
				if (Math.abs(value) == END_SEARCH) {
					return END_SEARCH;
				}

				if (value > nodeBestValue) {
					nodeBestValue = value;
					nodeBestMove = move;
				}

				if (value > alpha) {
					alpha = value;
					
					if (currentDepth == 0) {
						currentBestMove = move;
						currentBestMoveValue = alpha;
					}
				}
				
				if (alpha >= beta) {
					break;
				}
			}
			
			transpositionTable.put(currentBoard.getBoardID(), new BoardStateValue(nodeBestValue, remainingDepth, nodeBestMove, alpha >= beta, nodeBestValue < alpha));
			
			return nodeBestValue;
		}
	}
	
	public Move searchForBestMove() {
		currentBestMove = null;
		startTime = System.currentTimeMillis();
		currentBoard = new BoardState(boardState);
		currentBestMoveValue = -INFINITY;
		Move prevBestMove = currentBestMove;
		int prevBestMoveValue = currentBestMoveValue;
		

		for (int depth = Math.min(2, maxDepth); depth <= maxDepth; depth += 2) {
			int value = alphaBetaPrunningSearch(-INFINITY, INFINITY, 0, depth);
			
			if (Math.abs(value) == END_SEARCH) {
				if (currentBestMoveValue <= prevBestMoveValue) {
					currentBestMove = prevBestMove;
					currentBestMoveValue = prevBestMoveValue;
				}
				
				break;
			}
			
			prevBestMove = currentBestMove;
			prevBestMoveValue = value;
		}

		return currentBestMove;
		
//		List<Move> moves = boardState.getValidMoves(moveEvaluationFunction);
//		Random random = new Random();
//		
//		return moves.get(random.nextInt(moves.size()));
	}
	
	public synchronized void terminateSearch() {
		doTerminateMove = true;
	}
}
