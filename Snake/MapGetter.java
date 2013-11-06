import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
*	The MapGetter reads a file in the Game Map format. It adds any walls to itself.
*	@author: Lance Baker.
**/

public class MapGetter extends ArrayList<Wall> implements Constants {
		private static final long serialVersionUID = 1L;
		
		// Recieves a File object.
		public MapGetter(File file) throws FileNotFoundException, IOException { // Throws File Exceptions
			if (!file.exists()) throw new FileNotFoundException(file.getName()); // Checks if the file object received exists in the game directory
			
			int y = 0; // counter for the y coordinates.
			BufferedReader br = new BufferedReader(new FileReader(file));
			for (String line = br.readLine(); line != null; line = br.readLine(), y++) { // Iterates for each read line. Increments the y coordinate as it moves down.
				if (line != null) { // Ensures the following isnt done if the line value is null.
					for (int x = 0; x < line.length(); x++) { // For each character on the line, it is a Grid location. Increments the x coordinate as it moves across.
						char character = line.charAt(x); // Gets the character at the x coordinate.
						// Therefore if the character is equal to the WALL_CODE - it adds the wall at the given position to itself.
						if (character == WALL_CODE) this.add( new Wall((x  * GRID_SIZE) , (y  * GRID_SIZE)) );
					}
				}
			}
		}
}
