package game.ninemensmorris.model;

public final class BoardStateValue {
	private final int value;
	private final int remainingDepth;
	private final Move foundBestMove;
	private final boolean hasBeenCut;
	private final boolean couldHaveBeenCutDeeper;

	public BoardStateValue(int value, int remainingDepth, Move foundBestMove, boolean hasBeenCut, boolean couldHaveBeenCutDeeper) {
		this.value = value;
		this.remainingDepth = remainingDepth;
		this.foundBestMove = foundBestMove;
		this.hasBeenCut = hasBeenCut;
		this.couldHaveBeenCutDeeper = couldHaveBeenCutDeeper;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (couldHaveBeenCutDeeper ? 1231 : 1237);
		result = prime * result
				+ ((foundBestMove == null) ? 0 : foundBestMove.hashCode());
		result = prime * result + (hasBeenCut ? 1231 : 1237);
		result = prime * result + remainingDepth;
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
		BoardStateValue other = (BoardStateValue) obj;
		if (couldHaveBeenCutDeeper != other.couldHaveBeenCutDeeper) {
			return false;
		}
		if (foundBestMove == null) {
			if (other.foundBestMove != null) {
				return false;
			}
		} else if (!foundBestMove.equals(other.foundBestMove)) {
			return false;
		}
		if (hasBeenCut != other.hasBeenCut) {
			return false;
		}
		if (remainingDepth != other.remainingDepth) {
			return false;
		}
		if (value != other.value) {
			return false;
		}
		return true;
	}

	public int getValue() {
		return value;
	}

	public int getRemainingDepth() {
		return remainingDepth;
	}

	public Move getFoundBestMove() {
		return foundBestMove;
	}
	
	public boolean hasBeenCut() {
		return hasBeenCut;
	}
	
	public boolean couldHaveBeenCutDeeper() {
		return couldHaveBeenCutDeeper;
	}
}
