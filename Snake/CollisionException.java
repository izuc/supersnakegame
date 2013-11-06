/**
*	The CollisionException class is a custom exception which gets thrown when the snake either collides with its own body, or with a wall block.
* 	@author: Lance Baker.
**/

public class CollisionException extends Exception {
		public static final long serialVersionUID = 1L;
		public enum CollideType { SNAKE_BODY, WALL }; // The different collide types.
		private static final String[] MESSAGE = {"Collided with Snake Body", "Collided with Wall"}; // The exception message
		private CollideType type; // The CollideType of the collision.
		
		public CollisionException(CollideType type) { // Recieves the CollideType
				super(MESSAGE[type.ordinal()]); // Sets the super message based on the array and the ordinal position of the type.
				this.type = type; // Sets the type.
		}
		
		public CollideType getCollideType() {
				return this.type; // Provides a getter
		}

}