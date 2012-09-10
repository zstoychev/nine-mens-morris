package game.ninemensmorris.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import game.ninemensmorris.algorithms.AlphaBetaPruning;
import game.ninemensmorris.model.BoardState;
import game.ninemensmorris.model.Move;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NineMensMorrisGUI extends JFrame {
	private static final long serialVersionUID = -514606427157467570L;
	private BoardState currentGame;
	private NineMensMorrisBoard boardPanel;
	private JPanel controls;
	private JButton newGameButton;
	private JTextField maxTimeTextField;
	private JTextField maxDepthTextField;
	private JLabel statusLabel;
	private AlphaBetaPruning solver;
	private volatile MoveExecutorCallback moveExecutor;

	private class MoveExecutor implements MoveExecutorCallback {
		private boolean terminate = false;
		
		public synchronized void terminate() {
			this.terminate = true;
			solver.terminateSearch();
		}
		
		@Override
		public synchronized void makeMove(Move move) {
			if (terminate) {
				return;
			}

			currentGame.makeMove(move);
			boardPanel.repaint();
			
			if (currentGame.hasCurrentPlayerLost()) {
				if (currentGame.getCurrentPlayer() == 1) {
					statusLabel.setText("You won!");
				} else {
					statusLabel.setText("You lost!");
				}
			} else if (currentGame.getCurrentPlayer() == 1) {
				statusLabel.setText("Making move...");
				
				int maxDepth = Integer.parseInt(maxDepthTextField.getText());
				int maxTime = Integer.parseInt(maxTimeTextField.getText()) * 1000;
				
				solver.setMaxDepth(maxDepth);
				solver.setMaxTime(maxTime);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						Move move = solver.searchForBestMove();
						MoveExecutor.this.makeMove(move);
					}
				}).start();
			} else {
				statusLabel.setText("Your move");
				boardPanel.makeMove();
			}
		}
	}
	
	private void startNewGame() {
		if (moveExecutor != null) {
			moveExecutor.terminate();
		}
		currentGame = new BoardState();
		moveExecutor = new MoveExecutor();
		boardPanel.setBoard(currentGame, moveExecutor);
		statusLabel.setText("Your move");
		
		int maxDepth = Integer.parseInt(maxDepthTextField.getText());
		int maxTime = Integer.parseInt(maxTimeTextField.getText()) * 1000;
		solver = new AlphaBetaPruning(currentGame, maxDepth, maxTime);
		boardPanel.makeMove();
	}
	
	public NineMensMorrisGUI() {
		super("Nine Men's Morris");
		
		boardPanel = new NineMensMorrisBoard();
		
		add(boardPanel, BorderLayout.CENTER);
		
		controls = new JPanel();
		controls.setLayout(new FlowLayout());
		newGameButton = new JButton("New game");
		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startNewGame();
			}
		});
		controls.add(newGameButton);
		controls.add(new JLabel("Max move time:"));
		maxTimeTextField = new JTextField(3);
		maxTimeTextField.setText("30");
		controls.add(maxTimeTextField);
		controls.add(new JLabel("Max searching depth:"));
		maxDepthTextField = new JTextField(3);
		maxDepthTextField.setText("15");
		controls.add(maxDepthTextField);
		controls.add(new JLabel("Status:"));
		statusLabel = new JLabel("Your move");
		controls.add(statusLabel);
		
		add(controls, BorderLayout.SOUTH);
		
		startNewGame();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame game = new NineMensMorrisGUI();
		
		game.setSize(600, 700);
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setVisible(true);
	}
}
