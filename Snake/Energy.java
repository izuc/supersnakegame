import javax.swing.ImageIcon;
import java.util.Random;

/**
*	The Energy Class extends the Obstacle Supertype.
*	The Energy class offers additional functionality for items which need to have a delay peroid, and a maximum duration before the item becomes expired.
*	It also offers a way to set the imageicon for the energy item.
*	Also the class enforces that any proceeding energy item has Points for when it is collected.
*	@author: Lance Baker
**/
public abstract class Energy extends Obstacle {
		private static final long serialVersionUID = 1L;
		private GameTimer duration; // GameTimer for the amount of time to display the item for.
		private GameTimer delay; // The delay before the item becomes active.
		private ImageIcon image; // The image icon for the item.

		public Energy(int x, int y) {
				super(x, y); // Instantiates the Super Class, and passes the coordinates for the Point.
				this.duration = new GameTimer(GameTimer.Type.COUNTDOWN); // Timer used to count down the remaining time until the item disappears.
				this.delay = new GameTimer(GameTimer.Type.COUNTDOWN); // Timer used to delay the appearance of the item.
		}

		public void setTime(int min, int max) {
				int duration = min + (int)(Math.random() * ((max - min) + 1)); // Calculates a random number between the range received.
				this.duration.set(duration); // Sets the duration for the amount of time to display the item for. 
				this.setDelay(min); // Sets the delay for the appearance based off a random number in the range of the minimum amount of time.
		}
		
		protected void setImage(String imagename) {
				// Sets the image icon for the energy item based on the received image name.
				this.image = new ImageIcon(this.getClass().getClassLoader().getResource(IMAGES_PATH + imagename + PNG));
		}
		
		private void setDelay(int min) {
				Random random = new Random();
				this.delay.set(random.nextInt(min)); // Sets a random delay based on the value received.
				this.delay.start(); // Starts the delay count down timer.
		}
		
		private boolean isReady() {
				boolean ready = (this.delay.getTime() <= 0); // If the item's delay is less than or equal to zero, it can now be visible.
				if (ready) this.duration.start(); // Starts the duration for the amount of time to display the item for.
				return ready; // Returns a boolean depicting if the item delay period is up.
		}
		
		public boolean isAvailable() { // Returns a boolean if the item is available. Whether the delay period is up, and the item hasn't expired in time.
				return ((!this.hasExpired()) && (this.isReady()));
		}
		
		public boolean hasExpired() {
				return (this.duration.getTime() <= 0); // If the item duration is less than or equal to zero, it is now expired.
		}
		
		public abstract int getPoints(); // Every item must give a certain amount of points.
		
		public ImageIcon getIcon() {
				return this.image; // Gets the ImageIcon for the Energy Item.
		}
}