package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", pieceColor=" + pieceColor +
                '}';
    }

    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor pieceColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        HashSet<ChessMove> movesList = new HashSet<>();
        switch(this.getPieceType()){
            case KING -> {
                int[] directions = {-1, 0, 1};
                for(int r : directions){
                    for(int c : directions) {
                        if(r == 0 & c == 0){ // Same as starting position
                            continue;
                        }
                        else{
                        int newRow = row + r;
                        int newCol = col + c;
                        //Check to see if new position is out of bounds
                        if(newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                            continue;
                        }
                        //Check to see if position is occupied
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        if(board.getPiece(newPosition) != null){
                            //Check to see if position is occupied by enemy
                            if(board.getPiece(newPosition).pieceColor != this.pieceColor){
                                ChessMove move = new ChessMove(myPosition, newPosition, null);
                                movesList.add(move);
                                //Adds the move to the available move list
                            }
                        }
                        //If position is not occupied, it is added.
                        else{
                        ChessMove move = new ChessMove(myPosition, newPosition, null);
                        movesList.add(move);
                        }}}}}
            case QUEEN -> {
            }
            case BISHOP -> {
                boolean addMove = false;
                int[] directions = {-7, -6, -5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6, 7};
                for(int r : directions){
                    int[] directions2 = {r, -r};
                    for(int c : directions2){
                        int newRow = row + r;
                        int newCol = col + c;
                        //Check to see if new position is out of bounds
                        if(newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                            continue;
                        }
                        //Check to see if position is occupied
                        var newPosition = new ChessPosition(newRow, newCol);
                        if(board.getPiece(newPosition) != null) {
                            //Sees if space is occupied by enemy
                            if (board.getPiece(newPosition).getTeamColor() != this.pieceColor) {
                                //Piece is taken and move is added to list
                                addMove = true;
                            }
                            else{
                                addMove = false;
                            }
                        }
                        //New position is not occupied
                        else {
                            addMove = true;
                        }
                        if(addMove){
                            //Check to see if path is blocked by any piece
                            int xDirection = Integer.compare(newRow, row);
                            int yDirection = Integer.compare(newCol, col);
                            int blockCount = 0;
                            //if x/yDirection is positive, the movement of that axis is also positive
                            for(int i = 1; i < Math.abs(newRow - row); i++){
                                int xValue = row + i * xDirection;
                                int yValue = col + i * yDirection;
                                var intermediatePosition = new ChessPosition(xValue, yValue);

                                //Checks if the intermediate position is occupied or not
                                if(board.getPiece(intermediatePosition) != null){
                                    blockCount += 1;
                                }
                            }
                            //Intermediate position(s) are not occupied and the move is added
                            if(blockCount == 0){
                                var move = new ChessMove(myPosition, newPosition, null);
                                movesList.add(move);
                            }
                        }
                    }
                }
            }
            case KNIGHT -> {
            }
            case ROOK -> {
            }
            case PAWN -> {
            }
        }
        return movesList;
    }}

