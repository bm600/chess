package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;

    private ChessBoard board;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = board.getPiece(startPosition);
        if(currPiece == null){
            return null;
        }
        //TODO make it so moving into check doesn't add to valid moves
        else {
            return currPiece.pieceMoves(board, startPosition);
        }
    }


    private Collection<ChessMove> testMovesList(Collection<ChessMove> movesList){
        HashSet<ChessMove> newMovesList = new HashSet<>();
        for(ChessMove move : movesList){
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            ChessPiece piece = board.getPiece(start);
            var clone = new ChessBoard(board);

            clone.removePiece(end);
            clone.removePiece(start);
            clone.addPiece(end, piece);
            if(!isOtherBoardInCheck(turn, clone)){
                newMovesList.add(move);
            }
        }
        return newMovesList;
    }

    private void changeTurn(){
        if(this.turn == TeamColor.WHITE) turn = TeamColor.BLACK;
        else if (this.turn == TeamColor.BLACK) turn = TeamColor.WHITE;
    }
    public boolean isOtherBoardInCheck(TeamColor teamColor, ChessBoard otherBoard) {
        ChessPosition king = findKing(teamColor);

        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                var currPosition = new ChessPosition(r, c);
                if(otherBoard.getPiece(currPosition) != null){
                    if(otherBoard.getPiece(currPosition).getTeamColor() != teamColor) {
                        Collection<ChessMove> movesList = validMoves(currPosition);
                        for (ChessMove move : movesList) {
                            if (move.getEndPosition().equals(king)) {
                                    return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);


        if(piece == null){
            throw new InvalidMoveException("Invalid move");
        }

        Collection<ChessMove> movesList = validMoves(start);

        TeamColor currTurn = piece.getTeamColor();
        if(this.turn != currTurn){
            throw new InvalidMoveException("Invalid move");
        } else if (!movesList.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        } else if(isInCheck(turn)){
            var clone1 = new ChessBoard(board);
            clone1.removePiece(end);
            clone1.removePiece(start);
            clone1.addPiece(end, piece);
            if(isOtherBoardInCheck(turn, clone1)){
                throw new InvalidMoveException("Invalid move");
            }
        }


        ChessBoard clone = new ChessBoard(board);
        ChessPiece piece2 = board.getPiece(start);
        clone.removePiece(end);
        clone.removePiece(start);
        clone.addPiece(end, piece2);

        if(isOtherBoardInCheck(turn, clone)){
            throw new InvalidMoveException("Invalid move");
            //TODO finish checking the CHECK
        }

        var removedPiece = board.getPiece(end);
        this.board.removePiece(end);
        this.board.removePiece(start);
        this.board.addPiece(end, piece);


        //TODO May need to check for Pawn Promotion
        changeTurn();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    private ChessPosition findKing(TeamColor teamColor){
        for(int r =1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                var currPosition = new ChessPosition(r, c);
                if(board.getPiece(currPosition) != null) {
                    if (board.getPiece(currPosition).getTeamColor() == teamColor & board.getPiece(currPosition).getPieceType() == ChessPiece.PieceType.KING) {
                        return currPosition;
                    }
                }
            }
        }
        return null;
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = findKing(teamColor);

        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                var currPosition = new ChessPosition(r, c);
                if(board.getPiece(currPosition) != null){
                if(board.getPiece(currPosition).getTeamColor() != teamColor) {
                    Collection<ChessMove> movesList = validMoves(currPosition);
                    for (ChessMove move : movesList) {
                        if (move.getEndPosition().equals(king)) {
                            return true;
                        }
                    }
                }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
