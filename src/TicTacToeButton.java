import javax.swing.JButton;
import java.awt.Color;
import javax.swing.border.LineBorder;

/**
 * A custom button for Tic Tac Toe game, extending JButton. It holds a value ("X", "O", or " ")
 * indicating the player's move and updates its icon based on the value.
 *
 * @author Ethan Ashworth
 * @version April-6th-2024
 */
public class TicTacToeButton extends JButton
{
    // Class instance variables
    private String value = " "; // The current value of the button

    /**
     * Constructor for TicTacToeButton
     * Initializes the button with default styling and a blank state.
     */
    public TicTacToeButton()
    {
        // Setup each button
        this.setIcon(TicTacToeUI.getIcon(" ")); // Start with blank icon
        this.setOpaque(true); // makes button opaque
        this.setContentAreaFilled(true);
        this.setBorderPainted(true); // sets border around icon
        this.setBorder(new LineBorder(Color.BLACK)); // sets border color
        this.setBackground(Color.DARK_GRAY); // sets button's background color to dark gray instead of default blue.
    }

    /**
     * Sets the button's value ("X", "O", or " ") and updates its icon accordingly.
     *
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
        switch (value) {
            case "X":
                this.setIcon(TicTacToeUI.getIcon("X"));
                break;
            case "O":
                this.setIcon(TicTacToeUI.getIcon("O"));
                break;
            default:
                this.setIcon(TicTacToeUI.getIcon(" "));
                break;
        }
    }

    /**
     * Retrieves the current value of the button.
     *
     * @return The button's value.
     */
    public String getValue() {
        return value;
    }
}
