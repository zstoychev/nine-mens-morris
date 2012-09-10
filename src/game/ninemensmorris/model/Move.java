package game.ninemensmorris.model;

public final class Move {
	private final int fromPosition;
	private final int toPosition;
	private final int positionOfTakenPiece;
	private volatile int hash = 0;	
	
	@Override
	public int hashCode() {
		if (hash == 0) {
			final int prime = 31;
			int result = 1;
			result = prime * result + fromPosition;
			result = prime * result + positionOfTakenPiece;
			result = prime * result + toPosition;
			hash = result;
		}
		
		return hash;
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
		Move other = (Move) obj;
		if (fromPosition != other.fromPosition) {
			return false;
		}
		if (positionOfTakenPiece != other.positionOfTakenPiece) {
			return false;
		}
		if (toPosition != other.toPosition) {
			return false;
		}
		return true;
	}

	public Move(int toPosition) {
		if (toPosition < 0 || toPosition >= BoardState.NUMBER_OF_POSITIONS) {
			throw new IllegalArgumentException();
		}

		this.fromPosition = -1;
		this.toPosition = toPosition;
		this.positionOfTakenPiece = -1;
	}
	
	public Move(int fromPosition, int toPosition) {
		if (toPosition < 0 || toPosition >= BoardState.NUMBER_OF_POSITIONS
				|| fromPosition < -1 || fromPosition >= BoardState.NUMBER_OF_POSITIONS) {
			throw new IllegalArgumentException();
		}

		this.fromPosition = fromPosition;
		this.toPosition = toPosition;
		this.positionOfTakenPiece = -1;
	}
	
	public Move(int fromPosition, int toPosition, int positionOfTakenPiece) {
		if (toPosition < 0 || toPosition >= BoardState.NUMBER_OF_POSITIONS
				|| fromPosition < -1 || fromPosition >= BoardState.NUMBER_OF_POSITIONS
				|| positionOfTakenPiece < 0 || positionOfTakenPiece >= BoardState.NUMBER_OF_POSITIONS) {
			throw new IllegalArgumentException();
		}

		this.fromPosition = fromPosition;
		this.toPosition = toPosition;
		this.positionOfTakenPiece = positionOfTakenPiece;
	}

	public int getFromPosition() {
		return fromPosition;
	}

	public int getToPosition() {
		return toPosition;
	}

	public int getPositionOfTakenPiece() {
		return positionOfTakenPiece;
	}
}
