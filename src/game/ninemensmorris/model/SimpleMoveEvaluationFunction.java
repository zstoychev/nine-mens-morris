package game.ninemensmorris.model;

public class SimpleMoveEvaluationFunction implements MoveEvaluationFunction {
	@Override
	public int evaluate(BoardState boardState, Move move) {
		if (boardState.doesPieceCompleteMill(move.getFromPosition(), move.getToPosition(), boardState.getCurrentPlayer())) {
			return 9;
		}
		
		if (boardState.doesPieceCompleteMill(move.getFromPosition(), move.getToPosition(), boardState.getOtherPlayer())) {
			for (int neighbour : BoardState.POSITION_TO_NEIGHBOURS.get(move.getToPosition())) {
				if (boardState.getPositionState(neighbour) == boardState.getOtherPlayer() + 1) {
					return 8;
				}
			}
			
			return 4;
		}
		
		if (boardState.doesPieceCompleteMill(-1, move.getFromPosition(), boardState.getOtherPlayer())) {
			for (int neighbour : BoardState.POSITION_TO_NEIGHBOURS.get(move.getFromPosition())) {
				if (boardState.getPositionState(neighbour) == boardState.getOtherPlayer() + 1) {
					return -2;
				}
			}
			
			return -1;
		}

		return 0;
	}
}
