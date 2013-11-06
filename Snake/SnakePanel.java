import javax.swing.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.net.URL;
import javax.sound.sampled.*;

/**
*	The Snake Panel class handles the rendering and processing of game elements. 
*	It contains a reference to the SnakeGame, and does not contain any direct data relating to the game play; therefore allowing the game to create a different game without having to recreate the Snake Panel itself.
*	The Snake Panel class processes the game items, (it doesn’t detect collisions directly) and determines whether the item has been picked up and performs an associated action associated with the item type.
*	@author: Lance Baker
**/

public class SnakePanel extends JPanel implements Runnable, Constants {
		private static final long serialVersionUID = 1L;
		private static Image gameImage;
		private static GameTimer textDisplay;
		private static String gameText;
		
		// This lastSecond static variable is used to work out when a new second has occurred. 
		// This is due because the main thread sleep time can vary, and the game timer operates under a different thread; therefore it allows time based actions to occur under the main thread.
		private static int lastSecond;
		
		private SnakeGame game; // Stores the current game, which gets loaded from the load game method.
		
		public SnakePanel() {
				this.setBackground(Color.white); // Sets the initial background to white.
				this.setPreferredSize( new Dimension(PANEL_WIDTH, PANEL_HEIGHT + HUD_HEIGHT)); // Changes the size to the Games width and Height (plus the score display area's height)
				this.setFocusable(true); // Makes it so the game's panel can be focused on.
				this.setControls(); // This calls a method which registers the keyboard actions to the panel.
				this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Sets the initial layout.
				// Loads the default background image, which gets shown when the game application is first loaded.
				this.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource(IMAGES_PATH + DEFAULT_IMAGE))));
		}
				
		private void startGame() {
				this.game.setGameMode(SnakeGame.GameMode.STARTED); // Changes the game mode to started.
				lastSecond = 0; // Sets the static lastSecond variable back to its initial value.
				this.addEnergy(); // Adds the initial amount of energy to the game.
		}
		
		private void setControls() {
				// This is a inner Action class for the registering of the current movement direction for the snake.
				final Action movement = new AbstractAction() {
						public static final long serialVersionUID = 1L;
						public void actionPerformed(ActionEvent e) { // The actionPerformed method gets triggered when the action has occured.
								SnakeGame.Compass currentDirection = SnakeGame.Compass.valueOf(e.getActionCommand()); // Matches the action command to a compass enum.
								if (currentDirection != game.getDirection()) { // Checks whether its a different direction before proceeding, to avoid holding down the KeyBoard button.
									game.setDirection(currentDirection); // Sets the new direction based off the converted ActionCommand
									updateGame(); // Updates the Game
								}
						}
				};
				
				final Action options = new AbstractAction() {
						public static final long serialVersionUID = 1L;
						public void actionPerformed(ActionEvent e) {
								game.setGameMode(SnakeGame.GameMode.valueOf(e.getActionCommand())); // Sets the current game mode based off the action command received.
						}
				};
				
				// Binds the KeyBoard Directional Keys to a Specified Compass Direction. I Just did it this way to try something different.
				this.registerKeyboardAction(movement, SnakeGame.Compass.NORTH.toString(), KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED);
				this.registerKeyboardAction(movement, SnakeGame.Compass.SOUTH.toString(), KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);
				this.registerKeyboardAction(movement, SnakeGame.Compass.WEST.toString(), KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), JComponent.WHEN_FOCUSED);
				this.registerKeyboardAction(movement, SnakeGame.Compass.EAST.toString(), KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), JComponent.WHEN_FOCUSED);
				
				// These are the hot keys for the Pausing, Resuming, and Stopping of the SnakeGame Application.
				this.registerKeyboardAction(options, SnakeGame.GameMode.PAUSED.toString(), KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), JComponent.WHEN_FOCUSED);
				this.registerKeyboardAction(options, SnakeGame.GameMode.PLAYING.toString(), KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), JComponent.WHEN_FOCUSED);
				this.registerKeyboardAction(options, SnakeGame.GameMode.STOPPED.toString(), KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), JComponent.WHEN_FOCUSED);
		}
		
		
		private boolean isNewSecond() {
				// This method is used to determine whether the GameTimer object (which the SnakeGame uses for the incrementing of the game time) has gone to a new second.
				// This is because the snake game could be running in a thread which could only sleeps for 200ms, therefore inorder to do time based actions - the game must check whether a new second has ticked over to avoid doing the same action numerous times.
				if (this.game.getTime() > lastSecond) { // Checks whether the game time is greater than the last second recorded.
						lastSecond = this.game.getTime(); // If it is a new second it sets the last known time to the current time.
						return true; // returns true if its a new second.
				}
				return false;
		}
		
		// This method gets called when the user presses a keyboard directional button, and when the thread updates the game.
		// Therefore it will need to be synchronized to only allow one execution of the method to occur at the same time.
		private synchronized void updateGame() {
				try {
					// Creates the game image to the height and width of the required amounts. 
					if (gameImage == null) gameImage = this.createImage(PANEL_WIDTH, (PANEL_HEIGHT + HUD_HEIGHT));
					if (gameImage != null) { // If the game image is not null it will render the screen and perform actions to the snake game.
						if (this.game.getGameMode() != SnakeGame.GameMode.GAMEOVER) { // Only does the following if the game is in a state other than gameover
							Graphics g = this.gameRender(); // Renders the game, and adds a background image, returns the Graphics from the gameImage.
							this.performTimeBasedActions(); // This method does actions which are based on the game time. Such as, adding the energy or incrementing to a new level.
							this.game.moveSnake(); // Moves the snake by one point which is based on the current direction.
							this.processItems(g); // Processes and renders the Energy items. It detects whether there was a collision with the item, and does corresponding actions.
							this.drawSnake(g); // Draws the snake to the screen.
							this.drawHud(g); // Draws the game statistics HUD to the game.
							this.processWalls(g); // Processes and renders the Walls. It detects whether there was a collision, and throws a Collision Exception if the snake dies.
							this.game.detectSnakeCollision(); // Detects whether there was a snake collision with itself, if there is - it throws a CollisionException.
						}
						this.paintScreen(); // Paints the screen
					}
				} catch (CollisionException ex) {
					SnakePanel.setGameText(10, ex.getMessage()); // Sets the exception message to the GameText message.
					this.game.setGameMode(SnakeGame.GameMode.GAMEOVER); // Changes the game state to GAME OVER.
					SoundFX("attention.wav"); // Plays the WAV file.
				}
		}
		
		private void performTimeBasedActions() {
					// Since the game is running in a main thread clocking in on a average 200ms,
					// and the game timer is in its own incrementing every second - the game would loop through the update game code serveral times.
					if (this.isNewSecond()) { // Therefore the isNewSecond() method ensures the following is only executed when a new second occurs.
						if (gameReady(this.game.getLevelTime())) {
							this.game.nextLevel(); // Increments the game level, and displays a random map.
							this.addEnergy(); // Adds energy to the game.
						} else if (gameReady(5)) { // Every 5 seconds it adds more energy to the game.
							this.addEnergy();
						}
					}
		}
		
		private boolean gameReady(int seconds) { // Returns true if the current game time MOD the seconds received is equal to zero.
				return ((this.game.getTime() % seconds) == 0);
		}
		
		private void addEnergy() {
				Random random = new Random();
				// It iterates multiple times depending on the gameLevel.
				for (int i = ZERO, x = ZERO, y = ZERO; i < (ONE + this.game.getGameLevel()); ) { 
					x = random.nextInt((PANEL_WIDTH / GRID_SIZE)) * GRID_SIZE; // Gets a random Coordinate based on the Grid Size.
					y = random.nextInt((PANEL_HEIGHT / GRID_SIZE)) * GRID_SIZE;
					if (!this.obstacleAt(x, y)) { // Checks whether there is a obstacle already at this position.
						// Adds the item to the items list in the SnakeGame object.
						this.game.getItems().add(((random.nextInt(5) == random.nextInt(5)) ? // There is a 1 in 5 chance the item is a power up, otherwise it adds a energy drink.
												new PowerUP(x, y) : new EnergyDrink(x, y))); 
					i++; // If a item added successfully it increments the counter.
					}
				}
		}
		
		private boolean obstacleAt(int x, int y) { // Checks the Grid Location for a Item or Wall.
				return (Obstacle.checkExists(x, y, this.game.getItems()) ? true : Obstacle.checkExists(x, y, this.game.getWall()));
		}
		
		// Processes and Renders the Items in the game. It checks whether the snake has collected a item, and does the corresponding actions associated with that item.
		private void processItems(Graphics g) {
				// Only completes the following if there are items stored in the list, and that the current game mode is playing.
				if (this.game.getItems().size() > 0 && this.game.getGameMode() == SnakeGame.GameMode.PLAYING) {
					synchronized (this.game.getItems()) { // Synchronizes the list to allow the list to be modified concurrently.
						// Iterates through each Energy Item.
						for (Iterator<Energy> it = this.game.getItems().iterator(); it.hasNext();) {
							Energy item = it.next();
							if (item.isAvailable()) { // If the item is available - meaning that the delay period is up and the item isn't expired.
								if (this.game.collision(item)) { // If the snake collides with the item it does the following.
									this.game.setPoints(item.getPoints()); // Sets the items Points.
									if (item instanceof EnergyDrink) { // If its a energy drink, it adds the caffeine amount to the snakes energy level.
										this.game.addEnergy(((EnergyDrink)item).getCaffeine());
										this.game.growSnake(); // Grows the snake by one increment.
									}
									if (item instanceof PowerUP) { // Power ups don't increase the size of the snake.
										this.grabPowerUp((PowerUP)item); // Grabs the PowerUP.
									}
									SoundFX("burp.wav"); // Plays a sound.
									it.remove(); // Removes the item.
								} else {
									item.getIcon().paintIcon(this, g, item.x, item.y); // If there was no collision, it paints the item to the screen.
								}
							} else {
								if (item.hasExpired()) it.remove(); // If the item display time duration has expired, it will get removed.
							}
						}
					}
				}
		}
		
		// This method is used to perform a action based upon the power up received.
		private void grabPowerUp(PowerUP item) {
				Random random = new Random();
				switch(item.getPowerType()) {
						case ENERGY_BOOST:
							this.game.setEnergy(FULLY_MAXED_ENERGY); // Sets the Energy Level to the Highest Amount.
							break;
						case WALLS_DEACTIVATED:
							Wall.delayWalls(10, 30); // Delays the Walls for a random amount of time - between 10 to 30 seconds.
							break;
						case LEVELED_UP:
							this.game.incrementLevel(); // Increments the Game Level without changing the map.
							break;
						case CHANGED_MAP:
							this.game.setRandomMap(); // Changes the game map.
							break;
						case BONUS_POINTS_GIVEN:
							this.game.setPoints(POWER_UP_POINTS * (1 + random.nextInt(5))); // Gives Bonus points, which are up to 5 times the standard power up amount.
							break;
						case SHRINKED_SNAKE_SIZE:
							this.game.shrinkSnakeSize(); // Shrinks the snakes size to a random size.
							break;
				}
				this.game.addEnergy(random.nextInt((FULLY_MAXED_ENERGY / 2))); // Adds a random amount of Energy - up to half of the Fully Maxed Amount.
				// Displays the information to the screen if the game is still running.
				if (!this.game.isGameOver()) SnakePanel.setGameText(5, item.getPowerType().toString().replace('_', ' '));
		}
		
		// This method processes and renders the Wall Blocks in the game. It throws a CollisionException if the snake dies by running into a wall.
		private void processWalls(Graphics g) throws CollisionException {
				boolean collision = false; // A boolean Flag for the collision.
				if (this.game.getWall().size() > 0) { // Only does it if there are walls.
					synchronized (this.game.getWall()) { // Synchronizes the Wall List.
						ImageIcon icon = Wall.getIcon(); // There is only one Wall Icon for all the walls, so it gets the icon before entering the iteration.
						for (Iterator<Wall> it = this.game.getWall().iterator(); it.hasNext();) {
							Wall wall = it.next(); // Gets the next wall block.
							icon.paintIcon(this, g, wall.x, wall.y); // Paints the wall block to the screen.
							// If the energy level is fully max'd out, the walls are not active, and the snake collides with a Wall - it then adds bonus points and removes the wall from the list.
							if (this.game.getEnergy() > MAX_ENERGY && (!Wall.isActive()) && this.game.collision(wall)) {
								this.game.setPoints(POWER_UP_POINTS);
								it.remove();
							} else {
								// If the Wall isActive and the snake collides with a wall block.
								if (Wall.isActive() && this.game.collision(wall)) {
									collision = true; // It flags the collision to true.
								}
							}
						}
					}
				}
				// If the collision gets flagged to true, it then throws a CollisionException.
				if (collision) throw new CollisionException(CollisionException.CollideType.WALL);
		}
		
		// This method is used to get the game graphics from the gameImage. It also adds the game background image to the graphics returned.
		private Graphics gameRender() {
				Graphics g = gameImage.getGraphics(); // Gets the Graphics from the gameImage.				
				try {
					// Draws the background image to the graphics.
					g.drawImage(ImageIO.read(new URL(getClass().getResource(IMAGES_PATH + BACKGROUND_IMG), BACKGROUND_IMG)), 
											ZERO, ZERO, PANEL_WIDTH, (PANEL_HEIGHT + HUD_HEIGHT), this);
				} catch (Exception e) { }
		
				return g;
		}
		
		// This method draws the Heads Up Display unit, which contains the statistics of the current game.
		private void drawHud(Graphics g) {				
				g.setFont(new Font(HUD_FONT, Font.PLAIN, 10)); // Sets the HUD_FONT with a size of 10point
				g.setColor(new Color(150, 150, 150)); // A light grey colour for the titles.
				// Draws the HUD statistic titles
				String energyPercent = LEFT_BRACE + ((this.game.getEnergy() > MAX_ENERGY) ? MAXED_ENERGY : String.valueOf((this.game.getEnergy() / 10)) + PERCENT ) + RIGHT_BRACE;
				g.drawString(ENERGY + energyPercent, HUD_GAP, PANEL_HEIGHT + HUD_GAP);
				g.drawString(POINTS, (HUD_SPACE * 2), (PANEL_HEIGHT + HUD_GAP));
				g.drawString(GAME_TIME, (HUD_SPACE * 3), (PANEL_HEIGHT + HUD_GAP));
				g.drawString(LEVEL, (HUD_SPACE * 4), (PANEL_HEIGHT + HUD_GAP));
				
				g.setFont(new Font(HUD_FONT, Font.BOLD, 18)); // Changes the Font Size to 18point
				// This adds a line to the energy bar for each 10 percent of energy.
				String percent_bar = EMPTY_STRING;
				for (int i = 0; i < ((this.game.getEnergy() > MAX_ENERGY) ? 10 : (this.game.getEnergy() / 100)); i++) {
					percent_bar += PERCENT_BAR;
				}
				// Sets the color of the energy bar ( Green if its above 50%, Orange if its above 40% and Below 51%, and Red if its below 40%)
				g.setColor((this.game.getEnergy() > 500) ? new Color(50, 200, 70) : 
							(this.game.getEnergy() > 400) ? new Color(250, 100, 0) : new Color(255, 0, 50));
				// Draws the energy bar to the HUD.
				g.drawString(percent_bar , HUD_GAP, (PANEL_HEIGHT + (HUD_GAP * 3)));
				
				// Sets the colour back to white.
				g.setColor(new Color(255, 255, 255));
				// Draws the statistics underneath the titles.
				g.drawString(String.valueOf(this.game.getTotalPoints()), (HUD_SPACE * 2), (PANEL_HEIGHT + (HUD_GAP * 3)));
				g.drawString(String.valueOf(this.game.getTime()), (HUD_SPACE * 3), (PANEL_HEIGHT + (HUD_GAP * 3)));
				g.drawString(String.valueOf(this.game.getGameLevel()), (HUD_SPACE * 4), (PANEL_HEIGHT + (HUD_GAP * 3)));
		}
		
		public void drawSnake(Graphics g) {
				ArrayList<Point> points = this.game.getSnakeBody(); // Gets the Points ArrayList from the SnakeGame.
				if (points.size() > ZERO) { // If the size is above zero.
					ImageIcon body = new ImageIcon(getClass().getClassLoader().getResource(IMAGES_PATH + BODY_CELL + PNG)); // Sets the imageicon for the body cells.
					ImageIcon head = new ImageIcon(getClass().getClassLoader().getResource(IMAGES_PATH + SNAKE_HEAD + PNG)); // Sets the imageicon used for the snake head.
					for (int i = 1; i < points.size(); i++) { // Iterates for each point in the Snake.
						body.paintIcon(this, g, points.get(i).x, points.get(i).y); // Paints the snake body cell.
					}
					head.paintIcon(this, g, points.get(ZERO).x, points.get(ZERO).y); // Adds the snake head.
				}
		}
				
		private void displayGameText(Graphics g) {
				if ((textDisplay != null && textDisplay.getTime() > 0) || (this.game.isGameOver())) { // Displays the game message if there is time left in the counter, or if the game is over.
					g.setColor(new Color(255, 255, 255)); // Sets the colour to white.
					g.setFont(new Font(HUD_FONT, Font.BOLD, 20)); // Sets the font to 20point in size.
					// Draws the score onto the screen if the game is over.
					g.drawString(((this.game.isGameOver())? (SCORE + this.game.getTotalScore()) : ""), (PANEL_WIDTH / 4), (PANEL_HEIGHT / 4));
					// Draws the game message onto the screen.
					g.drawString((gameText), (PANEL_WIDTH / 3), (PANEL_HEIGHT / 3));
				} else {
					gameText = EMPTY_STRING; // Sets the game message to a empty string.
				}
		}
		
		// This method receives a string for the location of the sound file and creates a new thread.
		private synchronized void SoundFX(final String soundFile) {
			// Creates a new thread with a anonymous inner runnable class.
			new Thread(new Runnable() {
				public void run() {
					try {
						Clip clip = AudioSystem.getClip(); // Gets the Clip object from the AudioSystem 
						// Creates a new AudioInputStream based on the resource location of the Sound File.
						AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getClassLoader().getResourceAsStream(SOUND_PATH + soundFile));
						clip.open(inputStream); // Opens the input steam into the clip.
						clip.start(); // Plays the file.
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
			}).start(); // Starts the thread.
		}
		
		public void newGame(SnakeGame game) {
				this.game = game; // Sets the current game to the one received.
				this.startGame(); // Starts the new game.
				new Thread(this).start(); // Launches a new thread.
		}
		
		public void restartGame() {
				if (this.game != null) { // Only performs the following if the current game is loaded.
					if (!this.game.isGameOver()) { // If the game is loaded and it isnt gameover, the existing thread can be reused.
						this.startGame(); // Starts the new game.
					} else {
						// If its game over, it will need to create a new thread. 
						// Therefore this just feeds it back into the newGame method, which receives the same SnakeGame object and creates a new thread.
						this.newGame(this.game); 
					}
				}
		}
		
		public void run() { 
				while (this.game.isRunning()) { // Loops whilst the game is running.
					try {
						this.updateGame(); // Updates the game and renders the graphics.
						Thread.sleep(this.game.getSpeed());	// sleeps for the time period from the getSpeed method.
						if (this.game.isGameOver()) { // If the game is over, it updates the game once more in order to display the message.
							this.updateGame();
						}
					} catch(InterruptedException ex) {
						
					}
				}
		}
		
		// This method paints the graphics to the JPanel.
		public void paintScreen() { 
				Graphics g;
				g = this.getGraphics(); // Gets the graphics from the JPanel
				if ((g != null) && (gameImage != null)) {
					g.drawImage(gameImage, ZERO, ZERO, null); // Draws the image to the graphics.
					this.displayGameText(g); // Displays the game text.
				}
				Toolkit.getDefaultToolkit().sync();
				g.dispose();
		}
		
		// Returns the current game.
		public SnakeGame getCurrentGame() {
				return this.game;
		}
		
		// Sets the game text to be rendered to the screen.
		private static void setGameText(int seconds, String text) {
				textDisplay = new GameTimer(GameTimer.Type.COUNTDOWN);
				textDisplay.set(seconds);
				textDisplay.start();
				gameText = text;
		}
}
