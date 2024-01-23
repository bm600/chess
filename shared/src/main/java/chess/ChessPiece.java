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
                int[][] directions = {
                        {1, 0}, //up
                        {0, 1}, //right
                        {-1, 0}, //down
                        {0, -1}, //left
                        {1, 1}, //up/right
                        {1, -1}, //up/left
                        {-1, 1}, //down/right
                        {-1, -1} //down/left
                };
                for(int[] direction : directions){
                    int r = direction[0];
                    int c = direction[1];
                    searchSpace(myPosition, myPosition, board, movesList, r, c, this.pieceColor);
                }
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
                int[][] directions = {
                        {2, 1}, //up and right
                        {2, -1}, //up and left
                        {1, 2}, //right and up
                        {-1, 2}, //right and down
                        {-2, 1}, //down and right
                        {-2, -1}, //down and left
                        {-1, -2}, //left and down
                        {1, -2}, //left and up
                };

                for(int[] direction : directions){
                    int newRow = row + direction[0];
                    int newCol = col + direction[1];
                    var newPosition = new ChessPosition(newRow, newCol);

                    //Check for out of bounds
                    if(newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                        continue;
                    }
                    //New position is in bounds, check to see if occupied

                    if(board.getPiece(newPosition) != null){
                        //position is occupied, check if is occupied by enemy team
                        if(board.getPiece(newPosition).getTeamColor() != this.pieceColor){
                            //occupied by enemy, spot it taken and added to list
                            var newMove = new ChessMove(myPosition, newPosition, null);
                            movesList.add(newMove);
                        }
                    }
                    else{
                        //spot is not occupied, it is added to list
                        var newMove = new ChessMove(myPosition, newPosition, null);
                        movesList.add(newMove);
                    }
                }
            }
            case ROOK -> {
                    //Defines directions going in the x or y direction
                    int[][] directions = {{1, 0}, {-1, 0}, {0, -1},{0, 1}};
                    for (int[] direction : directions) {
                        int r = direction[0]; int c = direction[1];
                        searchSpace(myPosition, myPosition, board, movesList, r, c, this.pieceColor);
                    }
            }
            case PAWN -> {
                int direction;
                if(this.pieceColor == ChessGame.TeamColor.WHITE){
                    direction = 1;
                }
                else{
                    direction = -1;
                }

            }
        }
        return movesList;
    }

    private void searchSpace(ChessPosition startPosition, ChessPosition currentPosition, ChessBoard board, Collection<ChessMove> movesList,
                             int r, int c, ChessGame.TeamColor myColor) {

        int row = currentPosition.getRow(); int col = currentPosition.getColumn();
        int new_row = row + r; int new_col = col + c;
        //New position variable is assigned with new row/column values
        var newPos = new ChessPosition(new_row, new_col);

        // Checks to see if new position value is within bounds
        if (new_row < 1 || new_row > 8 || new_col < 1 || new_col > 8) {
            return;
        }
        // Checks for other pieces in new spot
        if (board.getPiece(newPos) != null) {
            //board is occupied, If occupied by enemy they are captured
            var piece = board.getPiece(newPos);
            if (piece.pieceColor != myColor) {
                //Move is added if piece is of the opposite team
                var newMove = new ChessMove(startPosition, newPos, null);
                movesList.add(newMove);
            }
            //Recursion stops and the algorithm doesn't repeat
            return;
        }
        // Updates variables and move is added to list
        var newMove = new ChessMove(startPosition, newPos, null);
        movesList.add(newMove);
        // Repeats recursion
        searchSpace(startPosition, newPos, board, movesList, r, c, myColor);
    }
}

