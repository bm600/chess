package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class DrawChessboard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 3;
    private static final int LINE_WIDTH_IN_CHARS = 1;
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";

    private static final Random rand = new Random();

    private static final String[][] chessPieces = new String[8][8];


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        populateArray();

        drawHeaders(out);

        drawBoard(out);

        drawHeaders(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {

        setBlack(out);

        String[] headers = {" A ", " B ", " C ", " D ", " E ", " F ", " G ", " H "};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(EMPTY.repeat(1));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(1));

        setBlack(out);
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

    }

    private static void drawRowOfSquares1(PrintStream out) {
        for(int i = 0; i < 3; i++) {
            for (int squareRow = 0; squareRow < BOARD_SIZE_IN_SQUARES / 2; ++squareRow) {
                drawWhiteSquare(out, null);
                drawBlackSquare(out, null);
            }
            setBlack(out);
            out.println();
        }
    }

    private static void populateArray(){
        // White pieces
        DrawChessboard.chessPieces[0][0] = "R"; // Rook
        DrawChessboard.chessPieces[0][1] = "N"; // Knight
        DrawChessboard.chessPieces[0][2] = "B"; // Bishop
        DrawChessboard.chessPieces[0][3] = "Q"; // Queen
        DrawChessboard.chessPieces[0][4] = "K"; // King
        DrawChessboard.chessPieces[0][5] = "B"; // Bishop
        DrawChessboard.chessPieces[0][6] = "N"; // Knight
        DrawChessboard.chessPieces[0][7] = "R"; // Rook
        for (int i = 0; i < 8; i++) {
            DrawChessboard.chessPieces[1][i] = "P"; // Pawn
        }

        // Black pieces
        DrawChessboard.chessPieces[7][0] = "r"; // Rook
        DrawChessboard.chessPieces[7][1] = "n"; // Knight
        DrawChessboard.chessPieces[7][2] = "b"; // Bishop
        DrawChessboard.chessPieces[7][3] = "q"; // Queen
        DrawChessboard.chessPieces[7][4] = "k"; // King
        DrawChessboard.chessPieces[7][5] = "b"; // Bishop
        DrawChessboard.chessPieces[7][6] = "n"; // Knight
        DrawChessboard.chessPieces[7][7] = "r"; // Rook
        for (int i = 0; i < 8; i++) {
            DrawChessboard.chessPieces[6][i] = "p"; // Pawn
        }
    }

    private static void drawBoard(PrintStream out) {
        for(int j = 0; j < BOARD_SIZE_IN_SQUARES; j++) {
            for (int i = 0; i < 3; i++) {
                if (j % 2 != 0) { //Number is even
                    for (int k = 0; k < BOARD_SIZE_IN_SQUARES / 2; ++k) {
                        if(i == 1 & chessPieces[j][k*2] != null){
                            printPlayer(out, chessPieces[j][k*2]);
                        }if (i == 1 & chessPieces[j][(k*2)+1] != null) {
                            printPlayer(out, chessPieces[j][(k * 2) + 1]);
                        }
                        drawWhiteSquare(out, null);
                        drawBlackSquare(out, null);
                    }
                    setBlack(out);
                    out.println();
                }
                else{ //Number is odd
                    for (int k = 0; k < BOARD_SIZE_IN_SQUARES / 2; ++k) {
                        if(i == 1 & chessPieces[j][k*2] != null){
                            printPlayer(out, chessPieces[j][k * 2]);
                        }
                        if (i == 1 & chessPieces[j][(k * 2) + 1] != null) {
                            printPlayer(out, chessPieces[j][(k * 2) + 1]);
                        }
                        drawBlackSquare(out, null);
                        drawWhiteSquare(out, null);

                    }
                    setBlack(out);
                    out.println();
                }
            }
        }
    }

    private static void drawWhiteSquare(PrintStream out, String... player) {
        setWhite(out);
        out.print(EMPTY.repeat(1));
        if (player == null) {
            out.print(EMPTY.repeat(1));
        } else {
            printPlayer(out, player[0]);
        }
        out.print(EMPTY.repeat(1));
    }

    private static void drawBlackSquare(PrintStream out, String... player){
        setBlack(out);
        out.print(EMPTY.repeat(1));
        if(player == null){
            out.print(EMPTY.repeat(1));
        }
        else{
            printPlayer(out, player[0]);
        }
        out.print(EMPTY.repeat(1));
    }

    private static void drawSideNumber(PrintStream out, int num){
        for(int i = 0; i < 3; i++){
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(SET_TEXT_COLOR_GREEN);

            out.print(EMPTY.repeat(1));
            if(i == 1) {
                var value = STR." \{num} ";
                out.print(value);
            }
        }
    }

    private static void drawVerticalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_CHARS; ++lineRow) {
            setRed(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setBlack(out);
            out.println();
        }
    }


    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
    }
}


