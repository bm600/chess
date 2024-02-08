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
        TeamColor oldColor = this.turn;

        ChessPiece currPiece = board.getPiece(startPosition);
        if(currPiece == null){
            return new HashSet<>();
        }
        this.turn = currPiece.getTeamColor();
        Collection<ChessMove> movesList = currPiece.pieceMoves(board, startPosition);
        HashSet<ChessMove> finalMoves = new HashSet<>();

        for (ChessMove move : movesList) {
            ChessBoard clonedBoard = new ChessBoard(board);
            clonedBoard.removePiece(move.endPosition);
            clonedBoard.removePiece(move.startPosition);
            clonedBoard.addPiece(move.endPosition, currPiece);

            if (!isOtherBoardInCheck(currPiece.getTeamColor(), clonedBoard)) {
                finalMoves.add(move);
            }
        }
        this.turn = oldColor;
        return finalMoves;
    }

    private void changeTurn(){
        if(this.turn == TeamColor.WHITE) turn = TeamColor.BLACK;
        else if (this.turn == TeamColor.BLACK) turn = TeamColor.WHITE;
    }
    public boolean isOtherBoardInCheck(TeamColor teamColor, ChessBoard otherBoard) {
        ChessPosition king = findKing(teamColor, otherBoard);
        TeamColor otherTeam = null;
        if(teamColor == TeamColor.BLACK){
            otherTeam = TeamColor.WHITE;
        }
        else if(teamColor == TeamColor.WHITE){
            otherTeam = TeamColor.BLACK;
        }

        Collection<ChessMove> otherTeamMoves = allMoves(otherTeam, otherBoard);

        for(ChessMove move : otherTeamMoves){
            if(move.endPosition.equals(king)){
                return true;
            }
        }
        return false;
    }

    public Collection<ChessMove> allMoves(TeamColor teamColor, ChessBoard otherBoard) {
        Collection<ChessMove> allMoves = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition myPos = new ChessPosition(i, j);
                ChessPiece myPiece = otherBoard.getPiece(myPos);
                if (myPiece != null && myPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = myPiece.pieceMoves(otherBoard, myPos);
                    allMoves.addAll(moves);
                }
            }
        }

        return allMoves;
    }

    public Collection<ChessMove> allValidMoves(TeamColor teamColor, ChessBoard otherBoard) {
        Collection<ChessMove> allMoves = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition myPos = new ChessPosition(i, j);
                ChessPiece myPiece = otherBoard.getPiece(myPos);
                if (myPiece != null && myPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(myPos);
                    allMoves.addAll(moves);
                }
            }
        }

        return allMoves;
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
        }

        this.board.removePiece(end);
        this.board.removePiece(start);
        this.board.addPiece(end, piece);


        if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
            if ((piece.getTeamColor() == TeamColor.WHITE && end.getRow() == 8) || (piece.getTeamColor() == TeamColor.BLACK && end.getRow() == 1)) {
                if (move.getPromotionPiece() == null) {
                    throw new InvalidMoveException("No promotion piece was specified for Pawn.");
                }
                board.removePiece(end);
                ChessPiece promotion = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                this.board.addPiece(end, promotion);
            }
        }
        changeTurn();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    private ChessPosition findKing(TeamColor teamColor, ChessBoard myBoard){
        for(int r =1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                var currPosition = new ChessPosition(r, c);
                if(myBoard.getPiece(currPosition) != null) {
                    if (myBoard.getPiece(currPosition).getTeamColor() == teamColor & myBoard.getPiece(currPosition).getPieceType() == ChessPiece.PieceType.KING) {
                        return currPosition;
                    }
                }
            }
        }
        return null;
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = findKing(teamColor, board);
        TeamColor otherTeam = null;
        if(teamColor == TeamColor.BLACK){
            otherTeam = TeamColor.WHITE;
        }
        else if(teamColor == TeamColor.WHITE){
            otherTeam = TeamColor.BLACK;
        }

        Collection<ChessMove> otherTeamMoves = allMoves(otherTeam, board);

        for(ChessMove move : otherTeamMoves){
            if(move.endPosition.equals(king)){
                return true;
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
        if(isInCheck(teamColor)){
            return allValidMoves(teamColor, board).isEmpty();
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor)) {
            return allValidMoves(teamColor, board).isEmpty();
        }
        else{
            return false;
        }
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
