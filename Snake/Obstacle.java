import java.awt.*;
import java.util.List;
import java.util.Iterator;

/**
* The Obstacle class is the super type of all Obstacles within the Snake application. It is the class in which directly extends the Point Object.
* @author: Lance Baker
**/
public abstract class Obstacle extends Point implements Constants {
		
		private static final long serialVersionUID = 1L;

		public Obstacle(int x, int y) {
				super(x, y); // Passing the coordinates to the super Point class.
		}
				
		@SuppressWarnings("unchecked") // It just has a few disputes because the List doesn't specify the generics.
		// This is a static method which receives the Obstacle list, it checks whether a obstacle exists at the coordinates received.
		// This therefore allows the game to check the location of the coordinates for a already existing item, before adding the new item.
		public static boolean checkExists(int x, int y, List list) {
				synchronized (list) { // Synchronizes the list, to lock the list from being accessed from other methods.
						for (Iterator it = list.iterator(); it.hasNext();) {
							Obstacle obstacle = (Obstacle)it.next();
							return ((x == obstacle.x) && (y == obstacle.y)); // Returns a boolean value depicting whther a item was found at those coordinates.
						}
				}
				return false;
		}
}