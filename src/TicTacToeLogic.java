import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Manages logic for a Tic Tac Toe game. This includes tracking the game board, the current player, the winner,
 * and a leaderboard tallying wins for X and O, as well as ties. Methods facilitate making moves, checking for a winner,
 * and resetting the board for a new game.
 *
 * @author Ethan Ashworth
 * @version April-6th-2024
 */
public class TicTacToeLogic
{
    // Class instance variables
    private TicTacToeButton[][] buttons;
    private int numFreeSquares = 9;
    private String player = "X";
    private String winner = " ";
    private Dictionary<String, Integer> leaderboard;

    /**
     * Constructor initializes the game with an empty board and leaderboard.
     *
     * @param buttons The 3x3 array of TicTacToeButton representing the game board.
     */
    public TicTacToeLogic(TicTacToeButton[][] buttons) {
        this.buttons = buttons;
        leaderboard = new Hashtable<>();
        leaderboard.put("X", 0); // adds X to Leaderboard
        leaderboard.put("O", 0); // adds O to Leaderboard
        leaderboard.put("T", 0); // adds T to Leaderboard
    }

    /**
     * Clears the game board for a new game, resetting all squares to blank, the number of free squares,
     * the current player to X, and the winner to a blank state.
     */
    public void clearBoard() {
        for (int i = 0; i < 3; i++) {
            buttons[i][0].setValue(" "); buttons[i][1].setValue(" "); buttons[i][2].setValue(" ");
        }

        numFreeSquares = 9;
        player = "X";
        winner = " ";
    }

    /**
     * Checks if the current move by a player results in a win by checking rows, columns, and diagonals.
     *
     * @param row The row of the last move.
     * @param col The column of the last move.
     * @return true if the move results in a win; false otherwise.
     */
    public boolean haveWinner(int row, int col) {
        String currentMark = buttons[row][col].getValue();
        if (currentMark.equals(" ")) return false; // Ignore empty buttons

        // Check row
        if (currentMark.equals(buttons[row][0].getValue()) &&
                currentMark.equals(buttons[row][1].getValue()) &&
                currentMark.equals(buttons[row][2].getValue())) {
            return true;
        }

        // Check Column
        if (currentMark.equals(buttons[0][col].getValue()) &&
                currentMark.equals(buttons[1][col].getValue()) &&
                currentMark.equals(buttons[2][col].getValue())) {
            return true;
        }

        // Check first diagonal
        if (row == col) {
            if (currentMark.equals(buttons[0][0].getValue()) &&
                    currentMark.equals(buttons[1][1].getValue()) &&
                    currentMark.equals(buttons[2][2].getValue())) {
                return true;
            }
        }

        // Check other diagonal
        if (row == 2 - col) {
            if (currentMark.equals(buttons[0][2].getValue()) &&
                    currentMark.equals(buttons[1][1].getValue()) &&
                    currentMark.equals(buttons[2][0].getValue())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Makes a move on the game board at the specified row and column, updates the game state,
     * checks for a win or tie, and updates the leaderboard accordingly.
     *
     * @param row The row where the move is made.
     * @param col The column where the move is made.
     */
    public void makeMove(int row, int col) {
        if (!buttons[row][col].getValue().equals(" ") || !winner.equals(" ")) return; // Ignore if already marked or game over

        buttons[row][col].setValue(player); // sets button value to the player who is moving
        numFreeSquares--; // subtracts from free square count

        // after move is made, checks for a winner
        if (haveWinner(row, col)) {
            // if winner is found, ends game with winner
            winner = player;
            updateLeaderboard(winner);
            TicTacToeUI.playSound("WIN");
            TicTacToeUI.showWinAnimation(winner);

        } else if (numFreeSquares == 0) {
            // if no empty squares remain, ends game with tie
            winner = "T";
            updateLeaderboard(winner);
            TicTacToeUI.playSound("TIE");
            TicTacToeUI.showWinAnimation(winner);

        } else {
            // if game is not over, switches to next player
            player = player.equals("X") ? "O" : "X";
        }
    }

    /**
     * Returns the symbol ("X" or "O") of the current player.
     *
     * @return The current player's symbol.
     */
    public String getCurrentPlayer() {
        return player;
    }

    /**
     * Gets the winner of the game if one exists.
     *
     * @return The winner's symbol ("X" or "O") or " " if there's no winner yet.
     */
    public String getWinner() {
        return winner;
    }

    /**
     * Updates the leaderboard with the winner of the current game.
     *
     * @param Winner The symbol of the winner ("X", "O", or "T" for a tie).
     */
    public void updateLeaderboard(String Winner) {
        leaderboard.put(Winner, (int)(leaderboard.get(Winner)+1));
    }

    /**
     * Returns the current scores from the leaderboard for X wins, O wins, and ties.
     *
     * @return An array of int containing the scores for X wins, O wins, and ties, in that order.
     */
    public int[] getLeaderboard(){
        return new int[]{leaderboard.get("X"), leaderboard.get("O"), leaderboard.get("T")}; // returns Leaderboard values as array of ints.
    }

}
