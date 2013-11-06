import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.table.AbstractTableModel;

/**
*	The Snake game frame is the starting point for the application. 
*	The class contains a MenuAction enumerated type, which completes actions based on the Menu item selected. 
*	The class stores the games in a structure of LinkedList<SnakeGame> and can be serialized and reloaded back into the system. 
*	The snake game frame creates the initial layout, and firstly instantiates a Snake Panel with no game loaded. 
*	The SnakeGame is later created, when the user selects the “New Game” option; then the Snake Panel receives the SnakeGame created and begins the game play.
*	@author: Lance Baker.
**/

public class SnakeGameFrame extends JFrame implements Constants {
		private static final long serialVersionUID = 1L;
		
		// Actions for the Menu
		public enum MenuAction { 	NEW_GAME, RESTART_GAME, SAVE_RESULTS, RESTORE_GAME, EXIT_GAME,
									SET_LEVEL, SET_PLAYER, VIEW_RESULTS, ABOUT_BOX	};

		private SnakePanel snakePanel; // The snake panel
		private LinkedList<SnakeGame> games; // The snake games.
		private Serialize<LinkedList<SnakeGame>> serialize; // The serialize object for the SnakeGame LinkedList.
		
		private String defaultPlayer; // The default player name
		private SnakeGame.DifficultyLevel defaultDifficulty; // The default difficulty level
				
		public SnakeGameFrame() {
				this.createInterface(); // Creates the JMenu and the SnakePanel.
				this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				this.setResizable(false);
				this.setTitle(MAIN_HEADING);
				this.setVisible(true);
				// Instantiates the games LinkedList and serialize object
				this.games = new LinkedList<SnakeGame>();
				this.serialize = new Serialize<LinkedList<SnakeGame>>();
				
				this.defaultPlayer = DEFAULT_PLAYER; // Sets the player and difficulty level to the default values
				this.defaultDifficulty = SnakeGame.DifficultyLevel.SLUG;

				JDialog.setDefaultLookAndFeelDecorated(true); // Sets the JDialogs to have the traditional default java look.
		}
		
		private void createInterface() {				
				final Action menuAction = new AbstractAction() {
						public static final long serialVersionUID = 1L;
						public void actionPerformed(ActionEvent e) {
								// Gets the ActionCommand from the event, which was set in the Menu. 
								// Converts the ActionCommand to a MenuAction Enumerated Type and passes it to the LauchAction method.
								LaunchAction(MenuAction.valueOf(e.getActionCommand())); 
						}
				};
				
				// Creates the menuText ArayList<String[]> which holds the string arrays for each menu,
				ArrayList<String[]> menuText = new ArrayList<String[]>();
				menuText.add(0, OPTIONS_MENU);
				menuText.add(1, OPTIONS_FILE);
				menuText.add(2, OPTIONS_GAME);
				menuText.add(3, OPTIONS_HELP);
				
				// Sets the JMenuBar to the instantiated Menu which receives the menuText, the Action, and the Object Array of the MenuAction values
				this.setJMenuBar(new Menu(menuText, menuAction, MenuAction.values()));
				this.snakePanel = new SnakePanel(); // Instantiates the new SnakePanel
				this.snakePanel.requestFocus(); // Requests focus for the SnakePanel.
				
				// Adds a mouse listener, so therefore it enables the user to simply click on the panel to gain focus.
				this.snakePanel.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
							snakePanel.requestFocus(); // Requests focus.
					}
				});
				
				// Adds a FocusAdaper for the FocusGained and FocusLost on the snakePanel.
				this.snakePanel.addFocusListener(new FocusAdapter() {
					public void focusGained(FocusEvent e) {
							snakePanel.paintScreen(); // When the focus is gained, paint the screen of the snake panel. 
							if ((snakePanel.getCurrentGame() != null) && (!snakePanel.getCurrentGame().isGameOver())) {
								snakePanel.getCurrentGame().setGameMode(SnakeGame.GameMode.PLAYING); // If the game is paused, it changes the state back to playing.
							}
					}
					public void focusLost(FocusEvent e) {
							if ((snakePanel.getCurrentGame() != null) && (!snakePanel.getCurrentGame().isGameOver())) {
								snakePanel.getCurrentGame().setGameMode(SnakeGame.GameMode.PAUSED); // Pauses the game when the focus is lost.
							}
					}
				});
				
				this.getContentPane().add(snakePanel, BorderLayout.CENTER); // Adds the snake panel to the default frame container.
				this.toggleMenuItems(false); // Sets the game menu items to false.
				this.pack(); // Packs the content.
		}
		
		public void createGame(String playerName, SnakeGame.DifficultyLevel difficulty) {
				if (this.games == null) this.games = new LinkedList<SnakeGame>(); // If the games list is null - it instantiates the list.
				this.games.add(new SnakeGame(playerName, difficulty)); // Adds a new SnakeGame containing the player name and difficulty level to the list.
				this.snakePanel.newGame(this.games.getLast()); // Adds the SnakeGame to the SnakePanel.
				this.toggleMenuItems(true); // Enables the game menu items.
		}
		
		// This disables any menu items which only get used when a game is/ has been running.
		private void toggleMenuItems(boolean state) {
				JMenu fileMenu = ((Menu)this.getJMenuBar()).getMenuAt(0);
				((JMenuItem)fileMenu.getMenuComponent(1)).setEnabled(state);
				((JMenuItem)fileMenu.getMenuComponent(2)).setEnabled(state);
		}
		
		// This method iterates through each SnakeGame stored in the games list
		// increments the counter for the games which have a GameOver state.
		private int getCompletedCount() {
				int length = 0;
				for (SnakeGame game : this.games) length += (game.isGameOver()) ? 1 : 0;
				return length;
		}
		
		public void LaunchAction(MenuAction actions) {
				switch(actions) {
						case NEW_GAME:
							new NewGameFrame(this); // Displays the new game frame.
							break;
						case RESTART_GAME:
							this.snakePanel.restartGame(); // Restarts the snake game
							break;
						case SAVE_RESULTS:						
							if (this.games.size() > 0) { // If the games list is greater than zero, it serializes the games list.
								this.serialize.saveObject(this.games, SAVE_FILE_NAME);
							}
							break;
						case RESTORE_GAME:
							// Restores the serialized games list.
							this.games = this.serialize.loadObject(SAVE_FILE_NAME); 
							break;
						case EXIT_GAME:
							System.exit(0); // Exits the application
							break;
						case SET_LEVEL:
								Object[] levels = SnakeGame.DifficultyLevel.values(); // Gets the levels from the DifficultyLevel enum values
								// Prompts for the selection of the difficulty level, it selects the defaultDifficulty value.
								Object difficulty = JOptionPane.showInputDialog(this, "Select a difficulty level", "Difficulty Level", 
													JOptionPane.INFORMATION_MESSAGE, null, levels, levels[this.defaultDifficulty.ordinal()]);
								if (difficulty != null) {
									this.defaultDifficulty = (SnakeGame.DifficultyLevel)difficulty; // Sets the default difficulty level
									if ((this.snakePanel.getCurrentGame() != null) && (this.snakePanel.getCurrentGame().getGameMode() == SnakeGame.GameMode.PLAYING)) {
										this.snakePanel.getCurrentGame().setDifficultyLevel((SnakeGame.DifficultyLevel)difficulty); // Changes the difficulty level in the current game.
									}
								}
							break;
						case SET_PLAYER:
								// Prompts a JDialog for the input of the player name, the player name will then get stored in the default player field.
								Object object = JOptionPane.showInputDialog(this, "Enter a Player name", "Player Name", 
												JOptionPane.INFORMATION_MESSAGE, null, null, this.defaultPlayer);
								if (object != null && object.toString().length() > 0) {
									this.defaultPlayer = object.toString();
									if (this.snakePanel.getCurrentGame() != null) {
										this.snakePanel.getCurrentGame().setPlayer(object.toString());
									}
								}
							break;
						case VIEW_RESULTS:
							new ViewResults(this); // Displays the Results JDialog box.
							break;
						case ABOUT_BOX:
							new AboutBox(this); // Displays the AboutBoxSS
							break;
				}
		}
		
		// This method is used to get the completed game results (the games with a game over state) from the games list.
		// It converts it to a 2 dimensional object array, which will then be placed inside of a JTable.
		public Object[][] getGameResults() {
				Object[][] results = new Object[this.getCompletedCount()][4];
				for (int index = 0; index < this.games.size(); index++) {
					if (this.games.get(index).isGameOver()) {
						results[index] = this.games.get(index).getStatistics(); // Gets the statistics array from each game which is in a game over state.
					}
				}
				return results;
		}
		
		public String getDefaultPlayer() {
				return this.defaultPlayer; // Gets the defaultPlayer
		}
		
		public SnakeGame.DifficultyLevel getDefaultDifficulty() {
				return this.defaultDifficulty; // Gets the defaultDifficulty level
		}
				
		public static void main(String args[]) { // Main method where the application gets executed from.
				JFrame.setDefaultLookAndFeelDecorated(true); // Sets the default look and feel to the traditional java style
				JFrame snakeGame = new SnakeGameFrame(); // Instantiates the SnakeGameFrame
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Positions the location of the SnakeGameFrame towards the centre.
				snakeGame.setLocation(screenSize.width / 4, screenSize.height / 4);
				snakeGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
}

/**
*	The ButtonBox class allows easy generation of the JButtons, with the associated actions based on the String[] array of names received.
*	A button gets created for each string element in the array, and the action command gets set with the string value. The ActionListener gets received, and added to each button created.
*	@author: Lance Baker.
**/
class ButtonBox extends JPanel {
		private static final long serialVersionUID = 1L;
		public ButtonBox(String[] names, ActionListener action) { // Receives a String[] array of the button names and a corresponding ActionListener for the handling of those button actions.
				JPanel buttonBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // Creates a ButtonBox JPanel for the buttons to be added to.
				buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10))); // Creates a border for the button box.
				this.setLayout(new BorderLayout()); // Sets itself with a borderlayout.
				
				LinkedList<JButton> buttons = new LinkedList<JButton>(); // Creates a JButton LinkedList.
				
				for (String name : names) { // Iterates for each String element in the array received.
					buttons.add(new JButton(name)); // Creates a new JButton, and adds it to the buttons list.
					buttons.getLast().addActionListener(action); // Adds the ActionListener recieved to that button.
					buttons.getLast().setActionCommand(name); // Sets the action command to the String element.
					buttonBox.add(buttons.getLast()); // Adds the JButton to the buttonBox.
				}
				
				this.add(new JSeparator(), BorderLayout.NORTH); // Creates a Separator in the north location of the 
				this.add(buttonBox, BorderLayout.EAST); // Adds the buttonBox to the east side, so therefore the buttons appear towards the right.
		}
}
/**
*	This is the NewGameFrame class which prompts the user for the player name, and the difficulty level.
*	 The class extends the JDialog class, therefore allowing the new prompt to be associated with the main parent.
*	It calls the createGame method in the SnakeGameFrame once the action has been completed, passing in the data collected from the prompt.
*	@author: Lance Baker.
**/
class NewGameFrame extends JDialog {
		private static final long serialVersionUID = 1L;
		private static final String NEW_GAME = "New Game";
		private static final String PLAYER_NAME = "Player Name:";
		private static final String GAME_DIFFICULTY = "Game Difficulty:";
		private static final String[] BUTTON_NAMES = { "Begin", "Close" };
		
		private SnakeGameFrame parent;
		private JTextField textBox;
		private JComboBox difficultyLevel;
		
		public NewGameFrame(SnakeGameFrame parent) {
				super(parent);
				this.parent = parent; // Creates a reference to the SnakeGameFrame
				this.initialiseForm(); // Creates the form
				this.setVisible(true); // Sets the visibility to true
				this.setSize(new Dimension(225, 125)); // Sets the Size of the form to the specified amount.
				this.setResizable(false); // Ensures the user cannot resize the form.
				this.setLocationRelativeTo(parent); // Sets the location of the form to appear in the center position of the parent
				this.setTitle(NEW_GAME);
		}
		
		private void initialiseForm() {
				JPanel layout = new JPanel(new GridLayout(2, 2, 5, 5)); // Grid used for the layout of the form.
				layout.add(new Label(PLAYER_NAME)); // Label for the PlayerName.
				layout.add(this.textBox = new JTextField(20)); // Textbox used to collect the player name.
				layout.add(new Label(GAME_DIFFICULTY)); // Label for the DifficultyLevel.
				layout.add(this.difficultyLevel = new JComboBox(SnakeGame.DifficultyLevel.values())); // Combobox containing the values from the DifficultyLevel Enumerated Type.
				
				this.textBox.setText(this.parent.getDefaultPlayer()); // Sets the textbox with the default playername.
				this.difficultyLevel.setSelectedItem(this.parent.getDefaultDifficulty()); // Selects the default difficulty level from the combobox.
				
				// Creates a inner ActionListener classs, which a action that gets passed to the ButtonBox class.
				final ActionListener action = new ActionListener() {
						public void actionPerformed(ActionEvent e) {
								if (e.getActionCommand() == BUTTON_NAMES[0]) { // If the action command is equal to the first button name.
									if (textBox.getText().length() > 0) { // Validation to ensure the user cannot proceed without entering a player name.
										// Calls the method in the parent, and creates the game based on the infomation received.
										parent.createGame(textBox.getText(), (SnakeGame.DifficultyLevel)difficultyLevel.getSelectedItem());
										setVisible(false); // Closes the JDialog.
									}
								} else if (e.getActionCommand() == BUTTON_NAMES[1]) {
									setVisible(false); // Closes the JDialog if they press "Close".
								}
						}
				};
				
				this.getContentPane().add(layout, BorderLayout.CENTER); // Adds the form to the center of the default container in the JDialog.
				// The ButtonBox class just allows a more simpler approach to creating the buttons to be displayed, with the corresponding actions.
				this.getContentPane().add(new ButtonBox(BUTTON_NAMES, action), BorderLayout.SOUTH);		
		}
}

/**
*	This is the AboutBox which displays information about the game application.
*	@author: Lance Baker.
**/
class AboutBox extends JDialog implements Constants {

		private static final long serialVersionUID = 1L;
		private static final String[] BUTTON_NAMES = { "Close" }; // Close Button used for the ButtonBox
		private static final String[] INFO_HEADINGS = {"Developer:", "Application:", "Creation Date:", "Student Number:"};
		private static final String[] INFO_DATA = {"Lance Baker", "Super Snake Game", "1st May 2009", "t311478010"};
		
		public AboutBox(SnakeGameFrame parent) {
				super(parent);
				// This is the inner ActionListener for the buttons.
				final ActionListener action = new ActionListener() {
						public void actionPerformed(ActionEvent e) {
								if (e.getActionCommand() == BUTTON_NAMES[0]) {
									setVisible(false); // Closes the JDialog.
								}
						}
				};
						
				this.setBackground(new Color(255, 255, 255)); // Sets the background colour to white.
				BackGroundPanel panel = new BackGroundPanel(IMAGES_PATH + ABOUT_BOX_IMAGE); // Creates the background image.
				panel.setLayout(new BorderLayout()); // Sets the layout of the panel
				
				JPanel infoBox = new JPanel(new BorderLayout()); // Creates a info box, which contains the application information.
				infoBox.setOpaque(false); // Makes it so the JPanel is transparent
				// Instantiates a Info panel based on the INFO_HEADINGS and INFO_DATA String[] arrays. Adds the Info panel to the South position of the InfoBox.
				infoBox.add(new Info(INFO_HEADINGS, INFO_DATA), BorderLayout.SOUTH); 
				panel.add(infoBox, BorderLayout.EAST); // Adds the InfoBox to the East position of the BackGroundPanel.
				
				this.setTitle(ABOUT); // Sets the title of the about box.
				this.getContentPane().add(panel, BorderLayout.CENTER); // Adds the panel to the center position of the default container.
				this.getContentPane().add(new ButtonBox(BUTTON_NAMES, action), BorderLayout.SOUTH); // Creates and adds the buttonbox to the south position.
				this.pack(); // Packs the JDialog
				this.setResizable(false); // Ensures it cannot be resized.
				this.setLocationRelativeTo(parent); // Sets the location of the box to appear to the center position of the parent.
				this.setVisible(true); // Makes it visible.
		}
		
		/**
		*	This is the Info panel which is used in the AboutBox. It receives the two String[] arrays and adds the elements to a grid layedout panel.
		*	@author: Lance Baker.
		**/
		private class Info extends JPanel {
				private static final long serialVersionUID = 1L;
				public Info(String[] headings, String[] data) {
					this.setLayout(new GridLayout(data.length, 2, 5, 0));
					this.setOpaque(false);
					for (int i = 0; i < data.length; i++) {
						JLabel heading = new JLabel(headings[i]);
						JLabel content = new JLabel(data[i]);
						heading.setForeground(Color.white); // Sets the Colour of the JLabels to White.
						content.setForeground(Color.white);
						this.add(heading);
						this.add(content);
					}
				}
		}
		
		/**
		*	This is a BackGroundPanel, it is essentially a JPanel with a painted background image.
		*	@author: Lance Baker.
		**/
		private class BackGroundPanel extends JPanel {
				private static final long serialVersionUID = 1L;
				private Image image;
				public BackGroundPanel(String location) { // Receives the location of the image in its contructor.
						try {
							// Creates a ImageIcon from the Resource location, and Gets and Sets the Image.
							this.image = new ImageIcon(this.getClass().getClassLoader().getResource(location)).getImage();
						} catch (Exception ex) { }
						this.setPreferredSize(new Dimension(350, 300)); // Sets the preferred size of the background panel.
				}
				protected void paintComponent(Graphics g) {
						super.paintComponent(g); // Paints the parent JPanel
						if (this.image != null) { // If the image is set, it Draws the image to the graphics.
							g.drawImage(this.image, 0,0, this.getWidth(), this.getHeight(), this);
						}
				}
		}
}

/**
*	This is a JDialog which displays the scores collected from the Snake game.
*	It contains a scrollable JTable with a TableModel.
*	@author: Lance Baker.
**/
class ViewResults extends JDialog {
		private static final long serialVersionUID = 1L;
		private static final String[] RESULT_HEADINGS = { "Player Name", "Total Points", "Game Level", "Time ", "Total Score" }; // The titles for the data in the JTable.
		private static final String[] BUTTON_NAMES = { "Close" }; // For the ButtonBox.
		
		private static final String GAME_RESULTS = "Game Results"; // Title for the JDialog
		public ViewResults(SnakeGameFrame parent) { // Receives the SnakeGameFrame as a reference.
				super(parent);
				// Creates a ActionListener for the ButtonBox.
				final ActionListener action = new ActionListener() {
						public void actionPerformed(ActionEvent e) {
								if (e.getActionCommand() == BUTTON_NAMES[0]) {
									setVisible(false); // Sets the JDialog's visibility to false if the user closes the box.
								}
						}
				};
				// Creates and Adds the Scrollable JTable with the TableModel to the Center location of the default container.
				this.getContentPane().add(new JScrollPane(new JTable(new ResultsTableModel(parent.getGameResults(), RESULT_HEADINGS))), BorderLayout.CENTER);
				// Creates and Adds the ButtonBox to the South location of the default container.
				this.getContentPane().add(new ButtonBox(BUTTON_NAMES, action), BorderLayout.SOUTH);
				
				this.setSize(new Dimension(400, 200)); // Sets a fixed size to the JDialog
				this.setResizable(false); // Ensures it cannot be resized.
				this.setLocationRelativeTo(parent); // Sets the location to the center of the parent.
				this.setTitle(GAME_RESULTS); // Sets the title.
				this.setVisible(true); // Makes the JDialog visible.
		}
		
		// TableModel used to handle the Array of data, and Column names. It disallows the user from editing the cells.
		private class ResultsTableModel extends AbstractTableModel {
				private static final long serialVersionUID = 1L;
				private Object[][] data;
				private String[] columns;
						
				public ResultsTableModel(Object[][] data,  String[] columns) {
					this.data = data;
					this.columns = columns;
				}
				
				public int getColumnCount() {			
					return this.columns.length;
				}
				
				public int getRowCount() {
					return this.data.length;
				}
				
				public Object getValueAt(int row, int col){
					return this.data[row][col];
				}
				
				public String getColumnName(int col) {
					return this.columns[col];
				}
				
				public boolean isCellEditable(int row, int col) {
					return false; // Changes every cell to be uneditable.
				}
		}
}