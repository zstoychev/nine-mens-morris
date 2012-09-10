package game.ninemensmorris.gui;

import game.ninemensmorris.model.BoardState;
import game.ninemensmorris.model.Move;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class NineMensMorrisBoard extends JPanel {
	private static final long serialVersionUID = 2961261317989680041L;

	private BoardState board;
	private int positionSelected;
	private boolean millFormed;
	private Move move;
	private MoveExecutorCallback moveExecutor;
	private boolean doMakeMove;
	
	public NineMensMorrisBoard() {
		addMouseListener(new Controller());
	}
	
	public void setBoard(BoardState board, MoveExecutorCallback moveExecutor) {
		this.board = board;
		this.positionSelected = -1;
		this.millFormed = false;
		this.move = null;
		this.moveExecutor = moveExecutor;
		this.doMakeMove = false;;

		repaint();
	}
	
	public void makeMove() {
		this.doMakeMove = true;
	}
	
	Point getPositionCoords(int position) {
		Point result = new Point();

		int margin = 30;
		int width = getSize().width - 2 * margin;
		int height = getSize().height - 2 * margin;
		int metric = Math.min(width, height);
		int positionSpace = metric / 6;
		
		int row = position / 3;
		if (row < 3) {
			result.x = row * positionSpace + (position % 3) * (metric - 2 * row * positionSpace) / 2;
			result.y = row * positionSpace;
		} else if (row == 3) {
			result.x = (position % 3) * positionSpace;
			result.y = row * positionSpace;
		} else {
			Point point = getPositionCoords(23 - position);
			point.x -= margin;
			point.y -= margin;
			result.x = metric - point.x;
			result.y = metric - point.y;
		}
		
		result.x += margin;
		result.y += margin;
		
		return result;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for (int i = 0; i < 24; i++) {
			for (int j : BoardState.POSITION_TO_NEIGHBOURS.get(i)) {
				Point start = getPositionCoords(i);
				Point end = getPositionCoords(j);
				g.drawLine(start.x, start.y, end.x, end.y);
			}
		}
		
		for (int i = 0; i < 24; i++) {
			Point coords = getPositionCoords(i);
			g.fillOval(coords.x - 5, coords.y - 5, 10, 10);
		}

		for (int i = 0; i < 24; i++) {
			if (move != null && i == move.getFromPosition()) {
				continue;
			}

			if (board.getPositionState(i) != 0 || (move != null && move.getToPosition() == i)) {
				if (positionSelected == i) {
					g.setColor(Color.RED);
				} else if (board.getPositionState(i) == 1
						|| (move != null && move.getToPosition() == i && board.getCurrentPlayer() == 0)) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.BLACK);
				}
				
				Point coords = getPositionCoords(i);
				g.fillOval(coords.x - 20, coords.y - 20, 40, 40);
				
				g.setColor(Color.BLACK);
				g.drawOval(coords.x - 20, coords.y - 20, 40, 40);
			}
		}
	}
	
	private class Controller extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (!doMakeMove || board.getCurrentPlayer() != 0 || board.hasCurrentPlayerLost()) {
				return;
			}
			
			int x = e.getX();
			int y = e.getY();
			
			for (int i = 0; i < 24; i++) {
				Point coords = getPositionCoords(i);
				
				if (coords.x - 20 <= x && x <= coords.x + 20
						&& coords.y - 20 <= y && y <= coords.y + 20) {
					if (millFormed) {
						if (board.getPositionState(i) == board.getOtherPlayer() + 1) {
							boolean areAllOtherPlayerPiecesFromMill = board.areAllPiecesFromMill(board.getOtherPlayer());

							if (areAllOtherPlayerPiecesFromMill || !board.doesPieceCompleteMill(-1, i, board.getOtherPlayer())) {
								move = new Move(move.getFromPosition(), move.getToPosition(), i);
								if (board.isMoveValid(move)) {
									moveExecutor.makeMove(move);
									move = null;
									millFormed = false;
									doMakeMove = false;
								}									
							}
						}
					} else {
						if (board.getPositionState(i) == 0) {
							if (positionSelected == -1) {
								move = new Move(i);
							} else {
								move = new Move(positionSelected, i);
							}
						} else if (board.getPositionState(i) == board.getCurrentPlayer() + 1) {
							if (positionSelected == -1) {
								positionSelected = i;
							} else if (positionSelected == i) {
								positionSelected = -1;
							} else {
								positionSelected = i;
							}
						}
						
						if (move != null) {
							if (board.isMoveValid(move)) {
								positionSelected = -1;
								if (board.doesPieceCompleteMill(move.getFromPosition(), move.getToPosition(), board.getCurrentPlayer())) {
									millFormed = true;
								} else {
									moveExecutor.makeMove(move);
									move = null;
									doMakeMove = false;
								}
							} else {
								move = null;
							}
						}
					}

					repaint();
					
					break;
				}
			}
		}
	}
}
