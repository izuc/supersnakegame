import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Point;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.Serializable;
import java.net.URL;

/**
*	The Snake Game class acts as a controller to the Snake Game. It handles all the direct operations for the game, and stores all the current game information. The player information, with relating statistics to the game are calculated and stored within this class. 
*	The class handles the incrementing of the game levels, the selection of a random game map, and the storing of the game elements – such as energy items, power ups, and wall blocks.
*	The snake itself is stored within this class, and is the only class with knowledge of the snake. The game movement directions are handled within this class and are based on the Compass directions enumerated type. 
*	The game time uses the GameTimer class, which has a mode of stopwatch; which increments the game time in a separate thread to the main application.
*	The Snake Game class has a game mode enumerated type, which allows for the game to enter different states; such as Playing, Paused, Stopped, or Game Over. The other methods check if the game is in its correct state before proceeding to perform the action. 
*	@author: Lance Baker
**/

public class SnakeGame implements Constants, Serializable {	
		public static final long serialVersionUID = 1L;
		public enum Compass { NORTH, SOUTH, EAST, WEST }; // Compass Directions, used for the directions of the snake based on the keyboard directional arrows.
		public enum DifficultyLevel { SLUG, WORM, SNAKE }; // The game difficulty level will cause the snakes rate to be faster and the walls to appear disapear at a faster rate, and the items will appear for a less duration.
		
		public enum GameMode { STARTED, PLAYING, PAUSED, STOPPED, GAMEOVER }; // The game mode states
		
		private Compass direction; // Storing the current direction
		private DifficultyLevel difficultyLevel; // The current difficulty level
		private GameMode mode; // Storing the current mode
		private GameTimer timer; // The GameTimer used to count the playing seconds 
		
		private String playerName; // The Player's name.
		private int totalPoints; // The total points collected from the Energy items.
		private int gameLevel; // The current game level.
		private int energy; // Percent representing the amount of caffeine in the snakes body. 0 % will cause the snake to stop, and 100% will cause the speed of the snake to increase to a max. Caffeine can only be collected from energy drinks.
		
		private List<Energy> items; // List containing the energy items
		private List<Wall> walls; // List containing the Wall Blocks
		private File[] gameMaps;  // Loaded Dynamically and Filled with the Level Files. This array is used to setRandomMap.
		private Snake snake; // The snake which extends ArrayList<Point>
		
		// The SnakeGame receives the playerName (String) and the DifficultyLevel.
		public SnakeGame(String playerName, DifficultyLevel difficultyLevel) {
				this.playerName = playerName; // Sets the Player name.
				this.difficultyLevel = difficultyLevel; // Sets the DifficultyLevel
				this.timer = new GameTimer(GameTimer.Type.STOPWATCH); // Creats a new GameTimer with the STOPWATCH Type (in which increments seconds). The GameTimer has two states, it can either be a StopWatch or a CountDown.	
								
				this.items = new ArrayList<Energy>(); // Instantiates the Energy items ArrayList.
				this.walls = new ArrayList<Wall>(); // Instantiates the Wall Blocks ArrayList.
				this.snake = new Snake(); // Instantiates a new Snake (which extends ArrayList<Point>)
		}
		
		// This method is used to start the game timer.
		private void startTimer() {
				this.timer.set(0); // Since the initial game timer is incrementing time, the default seconds will be set to Zero.
				this.timer.start(); // This will start the game timer, in which uses a Thread type wrapping on the javax.swing.Timer class.
		}
		
		public void incrementLevel() {
				this.gameLevel++; // Increments the Game Level
		}
		
		public void nextLevel() {
				this.incrementLevel(); // Increments the next level and then displays a random map
				this.setRandomMap(); // A random map is displayed based on a randomly generated index value from the File[] gameMaps Array.
		}
		
		// This method is used to peak inside of the maps directory, and load all of the files inside of this directory dynamically into a File[] array.
		private void loadGameMaps() {
				try {
					URL dirURL = getClass().getClassLoader().getResource(MAP_PATH); // Gets the directory
					if (dirURL != null && dirURL.getProtocol().equals("file")) {
						// Gets all of the files within the resource folder and loads it into a URL. 
						//For which is then passed to a File Object, and then the files inside of this object is then accessed by the .listFiles() method.
						// The gameMaps instance variable is now set to the File[] object array returned.
						this.gameMaps = new File(dirURL.toURI()).listFiles(); // Sets the gameMaps to the file objects in the directory.
					}
				} catch (Exception ex) {
					System.out.println("Can't Load Game Map Files");
				}
		}
		
		// This method sets a random map based upon the file[] objects inside the gameMaps array.
		public void setRandomMap() {
				try {
					Random random = new Random();
					if (this.gameMaps != null && this.gameMaps.length > 0) {
						this.walls = new MapGetter(this.gameMaps[random.nextInt(this.gameMaps.length)]); // Selects a random map
						if (Wall.isActive()) { // If the walls are active it delays the walls.
							Wall.delayWalls(WALL_MIN_DELAY, WALL_MAX_DELAY); // Delays the walls from being active for a random duration
						}
					}
				} catch (FileNotFoundException ex) {
					System.out.println("Can't Find Map");
				} catch (IOException ex) {
					System.out.println("IO Exception");
				}
		}
		
		public void setDirection(Compass direction) {
				// The following statement restricts the snake to certain navigation rules.
				if (((direction == Compass.WEST) && (this.direction != Compass.EAST)) ||
					((direction == Compass.EAST) && (this.direction != Compass.WEST)) ||
					((direction == Compass.NORTH) && (this.direction != Compass.SOUTH)) ||
					((direction == Compass.SOUTH) && (this.direction != Compass.NORTH))) {
					this.direction = direction;
				}
		}
		
		// Checks whether the snake collides with the Obstacle received.
		public boolean collision(Obstacle obstacle) {
				return this.snake.collision(obstacle, GRID_SIZE, false);
		}
		
		// This method is used to move the snake. It decrements the energy level for each movement the snake makes.
		public void moveSnake() {
				if (this.mode == SnakeGame.GameMode.PLAYING) { // If the game is currently being played
					// If the current energy is greater than zero - it decrements the energy by 5.
					this.energy = (this.energy > 0) ? this.energy -= 5 : 0;
					this.snake.move(this.getDirection()); // Moves the snake in one incremented point following the direction passed.
				}
				if (this.energy <= ZERO) {
					this.setGameMode(GameMode.GAMEOVER); // If the energy is less than or equal to zero it sets the game mode to gameover.
				}
		}
		
		// This method is used to detect whether the snake collided with itself. It has to be checked seperatly due to the exception that will be thrown.
		public void detectSnakeCollision() throws CollisionException {
				if (this.snake.collision(this.snake.get(ZERO), SNAKE_SIZE, true)) { // If the snake collided with its own body, then it throws a Collision Exception.
					throw new CollisionException(CollisionException.CollideType.SNAKE_BODY);
				}
		}
		
		// Increments the snakes size by one point, and keeping the body length.
		public void growSnake() {
				this.snake.incrementSize(this.getDirection());
		}
		
		// Returns the snake as a ArrayList of Point.
		public ArrayList<Point> getSnakeBody() {
				return this.snake;
		}
		
		public void setGameMode(GameMode mode) {
				switch(mode) {
					case STARTED:
							this.snake.clear(); // Clears the snake.
							this.walls.clear(); // Removes all existing walls.
							this.items.clear(); // Removes any energy items laying around.
							
							this.setDirection(SnakeGame.Compass.NORTH); // Sets the default starting direction to north.
							this.snake.add(new Point((PANEL_WIDTH / 2), (PANEL_HEIGHT / 2))); // Adds a starting point which is the screen's center location.
							
							this.setEnergy(MAX_ENERGY); // Sets the energy level to the Max amount.
							this.totalPoints = 0; // Sets the total points to its initial value.
							this.gameLevel = 1; // Sets the game level to one.
							
							this.loadGameMaps(); // Loads the maps in the map directory into a Array of File Objects
							this.setRandomMap(); // Randomly loads a map.
							
							this.startTimer(); // Starts the game timer.
							this.mode = GameMode.PLAYING; // Sets the mode to Playing.
						break;
					case PLAYING:
						if (this.mode == GameMode.PAUSED) { // If the game is paused, the state can be changed back to playing.
							this.timer.setStatus(GameTimer.Status.TICKING); // Changes the Game Timer back to its Ticking state.
							this.mode = GameMode.PLAYING; // Sets the GameMode to playing.
						}
						break;
					case PAUSED:
						if (this.mode == GameMode.PLAYING) { // The game can only be paused if its currently playing.
							this.timer.setStatus(GameTimer.Status.PAUSED); // Changes the GameTimer state to Paused.
							this.mode = GameMode.PAUSED; // Sets the GameMode to paused.
						}
						break;
					case STOPPED:
						if (this.mode == GameMode.PLAYING || this.mode == GameMode.PAUSED) { // If the game is playing or paused, it can be stopped.
							this.mode = GameMode.STOPPED; // Sets the GameMode to Stopped.
						}
						break;
					case GAMEOVER:
						if (this.mode == GameMode.PLAYING) { // If the game is playing, the state can be set to GameOver.
							this.timer.setStatus(GameTimer.Status.STOPPED); // Sets the GameTimer to stopped.
							this.mode = GameMode.GAMEOVER; // Sets the GameMode to GameOver.
						}
						break;
				}
		}
		
		public GameMode getGameMode() {
				return this.mode; // Gets the current game mode state.
		}
		
		public boolean isGameOver() {
				// Returns a boolean value depicting whether its game over.
				return (this.mode == GameMode.GAMEOVER);
		}
		
		public Compass getDirection() {
				return this.direction; // Gets the current compass direction of the snake.
		}
		
		public void setPoints(int points) {
				// Adds the points received to the totalPoints.
				if (points > 0) this.totalPoints += points;
		}
		
		public void addEnergy(int energy) {
				// Adds the energy received to the energy level.
				int totalEnergy = (this.energy + energy);
				this.energy = (totalEnergy <= FULLY_MAXED_ENERGY && totalEnergy > ZERO) ? totalEnergy : 
									(totalEnergy > FULLY_MAXED_ENERGY) ? FULLY_MAXED_ENERGY : ZERO;
		}
		
		public void setEnergy(int energy) {
				this.energy = (energy > ZERO) ? energy : ZERO; // Sets a new energy amount to the energy level.
		}
		
		public void shrinkSnakeSize() {
				// Reduces the size of the snake based on a random number.
				Random random = new Random();
				this.snake.reduceSize(random.nextInt(this.snake.size()-1));
		}
		
		public int getEnergy() {
				return this.energy; // Gets the energy level.
		}
		
		public int getGameLevel() {
				return this.gameLevel; // Gets the current game level.
		}
		
		public int getLevelTime() {
				// Gets the current level time, which is the default time devided by the ordinal position of the difficultyLevel.
				return (DEFAULT_LEVEL_TIME / (this.difficultyLevel.ordinal() + 1));
		}
		
		public int getTotalPoints() {
				return this.totalPoints; // Gets the current total points.
		}
		
		public List<Energy> getItems() {
				return this.items; // Gets the Energy Items.
		}
		
		public List<Wall> getWall() {
				return this.walls; // Gets the Wall Blocks.
		}
		
		public int getTime() {
				return this.timer.getTime(); // Gets the Current Game Time.
		}
		
		public void setPlayer(String player) {
				this.playerName = player; // Sets the player name.
		}
		
		public String getPlayer() {
				return this.playerName; // Gets the player name.
		}
		
		public DifficultyLevel getDifficultyLevel() {
				return this.difficultyLevel; // Gets the game's difficulty level.
		}
		
		public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
				this.difficultyLevel = difficultyLevel; // Sets the difficulty level of the game.
		}
		
		public int getTotalScore() {
				// Gets the total calculated score for when the game is over. 
				// Score Formula: ((TotalPoints + (SnakeSize * GameTime)) * GameLevel) * DifficultyLevel 
				return (((this.totalPoints + (this.snake.size() * this.getTime())) * this.gameLevel) * (this.difficultyLevel.ordinal() + 1));
		}
		
		// The getSpeed() method returns a value for the sleep time in ms for the thread - the higher the value; the slower the speed. 
		// The Game Energy lost is added to the speed value, therefore the more energy which gets lost the slower the snake becomes.
		// The Formula is (((MaxEnergy - TotalEnergy) + DIFFICULTY_SPEED) / GameLevel)
		public int getSpeed() {	
				int devision = MAX_ENERGY / SNAKE_SPEED[this.difficultyLevel.ordinal()];
				int speed = ((((devision > 0) ? (SNAKE_SPEED[this.difficultyLevel.ordinal()] - (this.energy / devision)) : ZERO) 
								+ SNAKE_SPEED[this.difficultyLevel.ordinal()]) / this.gameLevel); 
				return ((this.energy > MAX_ENERGY) ? speed / 5 : speed); // If there is a energy boost, the speed gets devided by 5.
		}
				
		public boolean isRunning() { // Returns a boolean value depicting whether to keep the games thread running.
				return ((this.mode == GameMode.STARTED) || (this.mode == GameMode.PLAYING) || (this.mode == GameMode.PAUSED));
		}
		
		// This method is used to to get the game statistics, and return them as a array which will be used for the score board.
		public Object[] getStatistics() {
				Object[] object = { this.playerName, this.totalPoints, this.gameLevel, this.getTime(), this.getTotalScore() };
				return object;
		}
}