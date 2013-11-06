import java.awt.event.*;
import javax.swing.Timer;
import java.io.Serializable;

/**
*	The GameTimer class is a wrapper around the functionality of the javax.swing.Timer class.
*	It provides methods for the retrieval of the current game seconds, changing of time states, and performs the correct operations based on the type of clock.
*	@author: Lance Baker.
**/

public class GameTimer implements ActionListener, Serializable {
		public static final long serialVersionUID = 1L;
		public enum Status { TICKING, PAUSED, STOPPED }; // The GameTimer has three states, whether the clock is ticking, paused, or stopped.
		public enum Type { STOPWATCH, COUNTDOWN }; // There are two types of GameTimers - the Stopwatch increments time, whereas the Countdown decrements.
		
		private static final int ONE_SECOND = 1000; // Default value for one second.
		private static final int ZERO = 0;
		
		private Type type; // The type of the GameTimer, whether its a Stopwatch or CountDown clock.
		private int seconds; // Total count of seconds.
		private Timer timer; // The Timer object, which uses the javax.swing.Timer.
		private Status status; // The status of the timer, whether its ticking, paused, or stopped.
		
		public GameTimer (Type type) {
			// Uses the java api javax.swing.Timer class, which executes the actionPerformed every second.
			this.timer = new Timer(ONE_SECOND, this);
			this.timer.setInitialDelay(0);
			this.type = type; // The type of GameTimer.
		}
		
		public void set(int seconds) {
			this.seconds = seconds; // Sets the seconds.
			this.timer.stop(); // Stops the timer.
		}
		
		public void start() {
			this.timer.start(); // Starts the timer.
			this.setStatus(Status.TICKING); // Sets the status to ticking.
		}
		
		public void setStatus(Status status) {
			this.status = status; // Sets the status
		}
				
		public void actionPerformed(ActionEvent e) {
			// Only proceeds if the status of the timer is in a ticking state. The seconds are either incremented or decremented depending on the type of clock.
			if (this.status == Status.TICKING) this.seconds = (type == Type.STOPWATCH ) ? ++this.seconds: --this.seconds;
			if (this.seconds <= ZERO) this.timer.stop(); // If the seconds are less than or equal to zero, it will stop the timer.
		}
		
		public int getTime() {
			return this.seconds; // gets the time.
		}
}