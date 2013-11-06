import java.awt.*;
import java.util.*;

/**
*	The Snake Class extents a ArrayList<Point> and contains methods for the incrementing of the Point location based on the Compass direction received.
*	It contains a collision detection method which checks through each body point of the snake.
*	The class handles the following:
*		-- Standard Movement: Incrementing the position by one point increment following the compass direction, and removing any trailing points.
*		-- Incrementing Size: Adding a new Point head to the Snake
*		-- Collision Detection: Detects whether the opposing coordinates received come into contact with the snake.
*	@author: Lance Baker.
**/

public class Snake extends ArrayList<Point> implements Constants {
		private static final long serialVersionUID = 1L;
		private static final Point[] BEARING = {new Point(ZERO, -ONE), new Point(ZERO, ONE), new Point(ONE, ZERO), new Point(-ONE, ZERO)}; // Bearings for the compass enum.
		
		// The snake doesnt receive any parametres, and doesn't contain any instance variables. It is itself a ArrayList<Point>.
		public Snake() {

		}
		
		// The nextPoint method is used to calculate a Point position based upon the Compass direction received. 
		// The nextPoint method receives a Compass Enumerated Type. It then determines the new axis for the X and Y coordinates for which the new point will be created with.
		private Point nextPoint(SnakeGame.Compass direction) {
				return new Point(newAxis(this.get(ZERO).x, BEARING[direction.ordinal()].x, PANEL_WIDTH),
										newAxis(this.get(ZERO).y, BEARING[direction.ordinal()].y, PANEL_HEIGHT));
		}
		
		// The newAxis method calculates the next coordinate based upon the BEARING constant value (determined by the direction ordinal position),
		// the position in which the existing coordinate for the snake head lies at, and the boundaries (which is the panel width or height).
		private int newAxis(int position, int bearing, int boundaries) {
				int axis = position + (SNAKE_SIZE * bearing); // The incremented axis
				// Checks whether its within the panel boundaries, if it goes off the edge; the snake appears on the opposite side.
				return (axis < ZERO) ? (axis + boundaries) : ((axis + SNAKE_SIZE) > boundaries) ? (axis - boundaries): axis; 
		}
		
		// The collision method is used for any collision type with the snake. 
		//It checks all the body parts (Points) belonging to the snake and uses the Java Rectangle API instersects method to determine whether there was a collision.
		// The Point point received is the object which gets checked for a collision occurance. It returns a boolean value depicting whether there was a collision or not.
		public boolean collision(Point point, int length, boolean minusHead) {
				Rectangle objectAhead = new Rectangle(point.x, point.y, length, length);
				for (int i = (minusHead) ? ONE : ZERO; i < this.size(); i++) { // Loops through all of the snake body parts.
						Rectangle snakeElement = new Rectangle(this.get(i).x, this.get(i).y, SNAKE_SIZE, SNAKE_SIZE);
						if (snakeElement.intersects(objectAhead)) {
							return true;
						}
				}
				return false;
		}

		// The incrementSize method receives a Compass direction, and adds a new body head (Point) to the snake based upon the value received by the nextPoint(Compass direction) method.
		// This method is used for when the snake is ordinally moving and when the snake eats a item for which will increment its size.
		public void incrementSize(SnakeGame.Compass direction) {
				this.add(ZERO, this.nextPoint(direction)); // increases size of the snake.
		}
		
		// The reduce size method is a wrapper around the ArrayList removeRange method which has protected access restrictions.
		// It receives a fromIndex Integer value which removes from that index point to the max length of the snakes size.
		public void reduceSize(int fromIndex) {
				this.removeRange(fromIndex, (this.size()-1));
		}
		
		// The move method receives a Compass direction and increments the size based upon that direction.
		// If the snake is greater than its initial length, it will remove the trailing tail point. Therefore giving the snake a moving sensation without constanting growing with every movement.		
		public void move(SnakeGame.Compass direction) {
				this.incrementSize(direction); // Increments the snake by one body part.
				if (this.size() > INITAL_LENGTH) {
					this.remove((this.size()-ONE)); // Removes the tail.
				}
		}
}