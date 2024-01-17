package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
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
        return pieceColor;
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
        ArrayList<int[]> movesList = new ArrayList<int[]>();
        if(this.getPieceType() == PieceType.KING){
            int[] directions = {-1, 0, 1};
            for(int r : directions){
                for(int c : directions) {
                    if(r == 0 & c == 0){ // Same as starting position
                        continue;
                    }
                    else{
                    int newRow = row + r;
                    int newCol = col + c;
                    int[] move = {newRow, newCol};
                    // TODO Add in case to check if own team's piece is there
                    movesList.add(move);
                }
            }
        }
    }
        return null; //TODO must return collection of ChessMove object
    }