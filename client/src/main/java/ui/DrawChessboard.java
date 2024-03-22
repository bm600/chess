package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawChessboard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final String EMPTY = "   ";

    private static final String[][] chessPieces = new String[8][8];

    public static void draw() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        populateArray();
        drawHeaders(out);
        drawBoard(out);
        drawHeaders(out);
        out.println();
        out.println();

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

    }

    public static void drawReverse() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        populateArray();
        drawHeadersReverse(out);
        drawReverseBoard(out);
        drawHeadersReverse(out);
        out.println();
        out.println();

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeadersReverse(PrintStream out) {
        setBlack(out);

        String[] headers = {" H ", " G ", " F ", " E ", " D ", " C ", " B ", " A "}; // Reversed headers
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(EMPTY.repeat(1));
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]); // Reversed headers
        }
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(EMPTY.repeat(1));
        setBlack(out);

        out.println();
    }


    private static void drawReverseBoard(PrintStream out) {
        for (int j = BOARD_SIZE_IN_SQUARES - 1; j >= 0; j--) { // Iterate backwards
            for (int i = 0; i < 3; i++) {
                if (j % 2 != 0) { // Number is even
                    if (i == 1) {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(STR." \{j+1} ");
                    } else {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(EMPTY.repeat(1));
                    }
                    for (int k = 0; k < BOARD_SIZE_IN_SQUARES / 2; ++k) {
                        if (i == 1) {
                            drawBlackSquare(out, chessPieces[j][k * 2]);
                            drawWhiteSquare(out, chessPieces[j][(k * 2) + 1]);
                        } else {
                            drawBlackSquare(out, null);
                            drawWhiteSquare(out, null);
                        }
                    }
                    if (i == 1) {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(STR." \{j+1} ");
                    } else {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(EMPTY.repeat(1));
                    }
                    setBlack(out);
                    out.println();
                } else { // Number is odd
                    if (i == 1) {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(STR." \{j+1} ");
                    } else {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(EMPTY.repeat(1));
                    }
                    for (int k = 0; k < BOARD_SIZE_IN_SQUARES / 2; ++k) {
                        if (i == 1) {
                            drawWhiteSquare(out, chessPieces[j][k * 2]);
                            drawBlackSquare(out, chessPieces[j][(k * 2) + 1]);
                        } else {
                            drawWhiteSquare(out, null);
                            drawBlackSquare(out, null);
                        }
                    }
                    if (i == 1) {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(STR." \{j+1} ");
                    } else {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(EMPTY.repeat(1));
                    }
                    setBlack(out);
                    out.println();
                }
            }
        }
    }


    private static void drawHeaders(PrintStream out) {

        setBlack(out);

        String[] headers = {" A ", " B ", " C ", " D ", " E ", " F ", " G ", " H "};
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(EMPTY.repeat(1));
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(EMPTY.repeat(1));
        setBlack(out);

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

    private static void populateArray() {
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
        for (int j = 0; j < BOARD_SIZE_IN_SQUARES; j++) {
            for (int i = 0; i < 3; i++) {
                if (j % 2 != 0) { //Number is even
                    if(i == 1){
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(STR." \{j+1} ");
                    }
                    else {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(EMPTY.repeat(1));
                    }
                    for (int k = 0; k < BOARD_SIZE_IN_SQUARES / 2; ++k) {
                        if (i == 1) {
                            drawBlackSquare(out, chessPieces[j][k * 2]);
                            drawWhiteSquare(out, chessPieces[j][(k * 2) + 1]);
                        } else {
                            drawBlackSquare(out, null);
                            drawWhiteSquare(out, null);
                        }
                    }
                    if(i == 1){
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(STR." \{j+1} ");
                    }
                    else {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(EMPTY.repeat(1));
                    }
                    setBlack(out);
                    out.println();
                } else { //Number is odd
                    if(i == 1){
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(STR." \{j+1} ");
                    }
                    else {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(EMPTY.repeat(1));
                    }
                    for (int k = 0; k < BOARD_SIZE_IN_SQUARES / 2; ++k) {
                        if (i == 1) {
                            drawWhiteSquare(out, chessPieces[j][k * 2]);
                            drawBlackSquare(out, chessPieces[j][(k * 2) + 1]);
                        } else {
                            drawWhiteSquare(out, null);
                            drawBlackSquare(out, null);
                        }
                    }
                    if(i == 1){
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(SET_TEXT_COLOR_GREEN);
                        out.print(STR." \{j+1} ");
                    }
                    else {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        out.print(EMPTY.repeat(1));
                    }
                    setBlack(out);
                    out.println();
                }
            }
        }
    }

    private static void drawWhiteSquare(PrintStream out, String player) {
        setWhite(out);
        out.print(EMPTY.repeat(1));
        if (player == null) {
            out.print(EMPTY.repeat(1));
        } else {
            printPlayerWhite(out, player);
        }
        out.print(EMPTY.repeat(1));
    }

    private static void drawBlackSquare(PrintStream out, String player) {
        setBlack(out);
        out.print(EMPTY.repeat(1));
        if (player == null) {
            out.print(EMPTY.repeat(1));
        } else {
            printPlayerBlack(out, player);
        }
        out.print(EMPTY.repeat(1));
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayerWhite(PrintStream out, String player) {
        if(Character.isUpperCase(player.charAt(0))) {
            out.print(SET_BG_COLOR_WHITE);
            out.print(SET_TEXT_COLOR_RED);
        }
        else if(Character.isLowerCase(player.charAt(0))) {
            out.print(SET_BG_COLOR_WHITE);
            out.print(SET_TEXT_COLOR_BLUE);
        }

        out.print(STR." \{player} ");

        setWhite(out);
    }

    private static void printPlayerBlack(PrintStream out, String player) {
        if(Character.isUpperCase(player.charAt(0))){
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_RED);
        }
        else if(Character.isLowerCase(player.charAt(0))){
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_BLUE);
        }

        out.print(STR." \{player} ");

        setBlack(out);
    }
}


