package game.ninemensmorris.model;

public interface MoveEvaluationFunction {
	public int evaluate(BoardState boardState, Move move);
}
