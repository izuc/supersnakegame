import java.util.Random;

/**
*	EnergyDrinks are used throughout the game to add additional energy amounts to the energy level, the type of energy drink is randomly selected when its created.
*	The imageicon used for the energy item is set based on the DrinkType.toString() value.
*	Energy Drinks contain caffeine, which should be directly added to the Snake energy level without modification.
*	@author: Lance Baker
**/

public class EnergyDrink extends Energy {
			public static final long serialVersionUID = 1L;
			private static final int CAFFEINE[] = { 109, 120, 160 }; // Given directly to the caffeination level percentage.
			private static final int POINTS[] = { 50, 75, 100 }; // Amount of points to be added when collected.
			
			public enum DrinkType { V_DRINK, RED_BULL, MOTHER };
			
			private DrinkType type; // Type of the energy drink. Randomly selected when created.
			
			public EnergyDrink(int x, int y) {
					super(x, y); // Instantiates the Super Class, and passes the coordinates for the Point.
					this.setType(); // Sets the type.
					super.setTime(5, 15); // Sets the Minimum Delay and Maximum amount of time to appear for.
					super.setImage(type.toString()); // Sets the current ImageIcon based on the DrinkType.toString() value.
			}
			
			// Sets a random type of Drink based on the Enumerated DrinkType
			private void setType() {
					Random random = new Random();
					Object[] objs = DrinkType.values(); // Gets the values of the DrinkType to a Object Array.
					// A random number is generated based on the length of the Object Array, which therefore gets the value based on that random number.
					// It converts the Object to a DrinkType, and sets the variable.
					this.type = (DrinkType)objs[random.nextInt(objs.length)]; 
			}
			
			// Returns a Enumerated DrinkType value.
			public DrinkType getType() {
					return this.type;
			}
			
			// Gets the caffeine from the array based on the DrinkType ordinal position.
			public int getCaffeine() {
					return CAFFEINE[this.type.ordinal()];
			}
			
			// Gets the points from the array based on the DrinkType ordinal position.
			public int getPoints() {
					return POINTS[this.type.ordinal()];
			}
}
