package game.ninemensmorris.gui;

import game.ninemensmorris.model.Move;

public interface MoveExecutorCallback {
	public void makeMove(Move move);
	public void terminate();
}
