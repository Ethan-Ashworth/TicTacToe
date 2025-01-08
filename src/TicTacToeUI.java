import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

/**
 * UI Class for TicTacToe game, operates using TicTacToe Logic, but UI can be changed independently of logic.
 *
 * @author Ethan Ashworth
 * @version April-6th-2024
 * @version January-7th-2025
 */
public class TicTacToeUI
{
    // Class instance variables
    private JFrame frame;

    private JPanel boardPanel;
    private JPanel StatusPanel;

    private JLabel statusLabel;
    private JLabel leaderboardLabel;
    private static JLabel winAnimationLabel;

    private JLayeredPane layeredPane;
    private Dimension dimensions;

    private TicTacToeButton[][] buttons = new TicTacToeButton[3][3];

    private TicTacToeLogic gameLogic;

    private static Clip moveSound;
    private static Clip winSound;
    private static Clip tieSound;

    private static ImageIcon Xwin;
    private static ImageIcon Owin;
    private static ImageIcon Tie;

    private static ImageIcon xIcon, oIcon, blankIcon;


    /**
     * Constructor for objects of class TicTacToeUI
     */
    public TicTacToeUI()
    {
        initializeUI(); // sets up UI
        gameLogic = new TicTacToeLogic(buttons); // pass the buttons array to logic
    }

    /**
     * Helper function for loading resources.
     * Loads resources as URLs
     * @param path filepath of resource to be loaded
     * @return resource as a URL
     */
    private URL resourceLoader(String path) {
        URL resource = TicTacToeUI.class.getClassLoader().getResource(path);
        if (resource == null) {
            System.out.println("Resource not found: " + path);
            return null;
        }
        return resource;
    }

    /**
     * Helper function to load audio clips
     * Uses resourceLoader
     * @param path filepath of resource to be loaded
     * @return Audio Clip as a Clip object
     * @throws UnsupportedAudioFileException file format not supported
     * @throws LineUnavailableException line unavailable (commonly due to use elsewhere)
     * @throws IOException if file cannot be loaded/not found
     */
    private Clip createAudioClip(String path) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resourceLoader(path));
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        return clip;
    }

    /**
     * Initializes the UI for Tic Tac Toe game. This includes setting up the game window, scaling and loading icons,
     * initializing sound effects, configuring UI elements (like the game board and status labels), and applying a custom font.
     * Exception handling is in place for loading resources. The method creates and displays the game's JFrame, complete with
     * a layered pane for animations, a status panel for game info, and a configured game board.
     */
    private void initializeUI(){
        dimensions = new Dimension(708, 767); // Window dimensions

        // initialize gifs
        Xwin = new ImageIcon(resourceLoader("gif/Xwin.gif"));
        Owin = new ImageIcon(resourceLoader("gif/Owin.gif"));
        Tie = new ImageIcon(resourceLoader("gif/tie.gif"));

        // Initialize icons with scaled images
        int size = (int)(dimensions.getWidth()*0.34); // sets scaled size, found X0.34 was good size for each button scale

        // Initialize icons with scaled images
        blankIcon = new ImageIcon(new ImageIcon(resourceLoader("img/blank.png")).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        xIcon = new ImageIcon(new ImageIcon(resourceLoader("img/X.png")).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        oIcon = new ImageIcon(new ImageIcon(resourceLoader("img/O.png")).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));

        // Initialization for Sounds
        try {
            moveSound = createAudioClip("sound/MOVE.wav");
            tieSound = createAudioClip("sound/TIE.wav");
            winSound = createAudioClip("sound/WIN.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setup Frame and Display
        frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Quits when window is closed
        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(dimensions); // Uses dimensions variable, so window size can easily be changed

        // Initialize Move Label and Leaderboard Label
        statusLabel = new JLabel("  X's Turn");
        leaderboardLabel = new JLabel("Leaderboard - X: 0 | O: 0 | Ties: 0  ");

        // Import and use 'custom' font:
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, resourceLoader("font/junegull.regular.otf").openStream()).deriveFont(30f);
            statusLabel.setFont(font);
            leaderboardLabel.setFont(font);
        } catch (Exception e){
            e.printStackTrace();
        }

        // Status Panel for Leaderboard and Move Labels
        StatusPanel = new JPanel(new BorderLayout());
        StatusPanel.add(statusLabel, BorderLayout.WEST);
        StatusPanel.add(leaderboardLabel, BorderLayout.EAST);

        // Layered pane for Board and Animations, allowing animations to play overtop of game board
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension((int)(frame.getPreferredSize().getWidth()*0.98),
                (int)(frame.getPreferredSize().getHeight()*0.877)));

        // Initializes board panel
        boardPanel = new JPanel(new GridLayout(3, 3));
        initializeBoard();
        boardPanel.setSize(layeredPane.getPreferredSize());

        // initializes win animation label and sets hidden
        winAnimationLabel = new JLabel(Xwin);
        winAnimationLabel.setLocation(0, 0);
        winAnimationLabel.setVisible(false);                // Initially invisible
        winAnimationLabel.setSize(boardPanel.getSize());    // Sets to board panel size to cover entire board

        // Adds board & animation to Layered pane
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(winAnimationLabel, JLayeredPane.PALETTE_LAYER);

        // Adds everything to frame and finishes initialization
        frame.setJMenuBar(createMenu());
        frame.add(layeredPane, BorderLayout.CENTER);
        frame.add(StatusPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    /**
     * Sets up a 3x3 grid of Tic TacToeButtons for the game board. Each button is linked to the game logic
     * for moves and updates the game status upon being clicked.
     */
    private void initializeBoard(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                TicTacToeButton button = new TicTacToeButton(); // Initialize new buttons
                final int row = i;
                final int col = j;

                // adds ActionListener to each button, updating status when button is pressed (move is made)
                button.addActionListener(e -> {
                    gameLogic.makeMove(row, col);
                    updateStatus();
                });

                // adds button to appropriate spot in array and to board panel.
                buttons[i][j] = button;
                boardPanel.add(button);
            }
        }
    }

    /**
     * Constructs a menu bar with "Game" menu, offering "New Game" and "Quit" actions with shortcuts Ctrl+N and Ctrl+Q, respectively.
     * "New Game" resets the board and updates the turn status, while "Quit" exits the app.
     *
     * @return Configured JMenuBar.
     */
    private JMenuBar createMenu() {
        // initialize variables for method
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem quit = new JMenuItem("Quit:");

        // Add keyboard shortcuts
        final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK)); // ctrl+n
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK)); // ctrl+q

        newGame.addActionListener(e -> {
            // Logic for when newGame button in menu is pressed
            gameLogic.clearBoard();
            statusLabel.setText("  " + gameLogic.getCurrentPlayer() + "'s Turn");
        });

        quit.addActionListener(e -> System.exit(0)); // Logic to quit program when quit button in menu is pressed

        // add all items to menu
        gameMenu.add(newGame);
        gameMenu.add(quit);
        menuBar.add(gameMenu);

        return menuBar;
    }

    /**
     * Updates the leaderboard display with the latest game results. It reflects the counts of wins for X and O,
     * as well as the number of ties, fetched from the game logic.
     *
     * @param Winner The player who won the latest game or indicates a tie.
     */
    private void leaderboard(String Winner){
        int[] results = gameLogic.getLeaderboard();
        // Updates Leaderboard Label Text
        leaderboardLabel.setText("Leaderboard - X: " + results[0] + " | O: " + results[1] + " | Ties: " + results[2] + "  ");
    }

    /**
     * Updates the game status, including checking for a winner, updating the leaderboard and status label,
     * and playing relevant sound effects. If a winner is determined, it updates the leaderboard and status label accordingly;
     * otherwise, it indicates whose turn it is and plays a move sound.
     */
    private void updateStatus() {
        String winner = gameLogic.getWinner(); // Checks for a winner (to end current match) (" " if no winner), otherwise 'X', 'O' or 'T'

        if (!winner.equals(" ")) {
            // case if there is a winner/match done:
            leaderboard(winner);

            if (winner.equals("T")) {
                statusLabel.setText("  Tie Game!");
            } else {
                statusLabel.setText("  " + winner + " Wins!");
            }

        } else {
            // match not complete, game ongoing
            statusLabel.setText("  " + gameLogic.getCurrentPlayer() + "'s Turn"); // updates text to the current player
            playSound("MOVE"); // plays move sound
        }
    }

    /**
     * Plays a specified sound effect based on game events: "MOVE", "WIN", or "TIE". Ensures the sound starts from the beginning each time.
     *
     * @param Sound The type of sound effect to play, corresponding to a game move, win, or tie.
     */
    public static void playSound(String Sound) {
        if (Sound.equals("MOVE")) {
            // case for move sounds
            if (moveSound != null) {
                moveSound.setFramePosition(0); // sets sound to beginning
                moveSound.start(); // plays sound

            }

        } else if (Sound.equals("WIN")) {
            // case for win (both X and O)
            if (winSound != null) {
                winSound.setFramePosition(0); // sets sound to beginning
                winSound.start(); // plays sound

            }

        } else if (Sound.equals("TIE")) {
            // case for tie game sounds
            if (tieSound != null) {
                tieSound.setFramePosition(0); // sets sound to beginning
                tieSound.start(); // plays sound

            }
        }
    }

    /**
     * Retrieves the ImageIcon associated with a specific game symbol: "X", "O", or a blank state for any other input.
     *
     * @param icon The string identifier of the icon to retrieve.
     * @return ImageIcon corresponding to the input identifier.
     */
    public static ImageIcon getIcon(String icon) {
        switch (icon) {
            case "X":
                return xIcon;
            case "O":
                return oIcon;
            default:
                return blankIcon;
        }
    }

    /**
     * Displays a win animation for the specified winner ("X" or "O") or a tie animation for any other input.
     * The animation is shown for approximately 1.85 seconds before being hidden.
     *
     * @param Winner The winning symbol ("X" or "O") or any other value to indicate a tie.
     */
    public static void showWinAnimation(String Winner) {
        // selects appropriate animation depending on winner
        switch (Winner) {
            case "X":
                winAnimationLabel.setIcon(Xwin);
                break;
            case "O":
                winAnimationLabel.setIcon(Owin);
                break;
            default:
                winAnimationLabel.setIcon(Tie);
                break;
        }

        winAnimationLabel.setVisible(true); // plays animation

        Timer timer = new Timer(1850, e -> winAnimationLabel.setVisible(false)); // shows animation for length of animation (~1.85 seconds)
        timer.setRepeats(false); // runs only once
        timer.start();
    }

    /**
     * The entry point for the Tic Tac Toe application.
     *
     * @param args The command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TicTacToeUI();
            }
        });
    }
}
