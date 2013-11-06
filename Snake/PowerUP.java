import java.util.Random;

/**
*	PowerUp items are energy items which add additional functionality to the game. 
*	@author: Lance Baker.
**/

public class PowerUP extends Energy {
			public static final long serialVersionUID = 1L;
			
			// The powerup enumerated type. It contains the functionality which the powerup provides.
			public enum PowerType {	ENERGY_BOOST, WALLS_DEACTIVATED, LEVELED_UP, CHANGED_MAP, BONUS_POINTS_GIVEN, SHRINKED_SNAKE_SIZE };
			
			private PowerType type; // The powerup type, which gets randomly selected.
			
			public PowerUP(int x, int y) {
					super(x, y); // Passes the coordinates to the super class.
					this.setType(); // Sets a random powerup type.
					super.setTime(5, 20); // Sets the minimum delay and maximum duration for the energy item.
					super.setImage(POWER_UP_IMAGE); // Sets the image to a default power up image.
			}
			
			private void setType() {
					Random random = new Random();
					Object[] objs = PowerType.values();
					this.type = (PowerType)objs[random.nextInt(objs.length)]; // Randomly selects a type.
			}
			
			public PowerType getPowerType() {
					return this.type; // Gets the Power up.
			}
			
			public int getPoints() {
					return POWER_UP_POINTS; // Gets the powerup points given for collecting the item.
			}
}
