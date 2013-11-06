import javax.swing.ImageIcon;

/**
*	The Wall are blocks which extend Obstacle. The walls itself should be avoided when its in a active state.
*	The wall status, and delay gametimer are static variables - this is due because all of the walls will either be active or not active at the same time.
*	@author: Lance Baker.
**/

public class Wall extends Obstacle {
		public static final long serialVersionUID = 1L;
		public enum Status { WALL_ACTIVE, WALL_NOTACTIVE }; // A enumerated type of the different wall states.
		
		private static GameTimer delay; // A static variable for the delay until the walls become active.
		private static Status status; // Sets the status of the Wall.
		
		public Wall() {
				super(0, 0);
		}
		
		public Wall(int x, int y) {
				super(x, y); // Passes the coordinates to the super class.
				status = Status.WALL_ACTIVE; // When the wall is first created, it sets the status to active.
		}
			
		public static ImageIcon getIcon() {
				// Sets the walls back to Active if the timer has expired.
				if (delay != null && delay.getTime() <= 0) {
					status = Status.WALL_ACTIVE;
				}
				// Returns a ImageIcon based upon the Wall status.
				return new ImageIcon(new Wall().getClass().getClassLoader().getResource(IMAGES_PATH + status.toString() + PNG));
		}
		
		public static void delayWalls(int min, int max) { // Delays the walls from becoming active
				status = Status.WALL_NOTACTIVE; // Sets the Walls to not active.
				delay = new GameTimer(GameTimer.Type.COUNTDOWN); // Instantiates a new GameTimer countdown
				delay.set((min + (int)(Math.random() * ((max - min) + 1)))); // randomly generates a number from the values received and sets the timer.
				delay.start(); // Starts the timer.
		}
		
		public static boolean isActive() { // Returns a boolean depicting whether the wall is in a active state.
				return (status != null && status == Status.WALL_ACTIVE);
		}
		
		
}