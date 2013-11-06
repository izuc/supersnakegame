import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This is a serialization class used to serialize and read Java Object files. 
 * The class uses Generics for the passing of the Object datatype.
 * The saveObject and loadObject methods can be accessed publicly.
 * @author Lance Baker
 */
public class Serialize<E> {

/**
 * 	The openOutputFile method is used to open a file, instantiate a object output stream, and return that stream for the ability to write a object to it.
 * 	@author Lance Baker
 * 	@param filename String - This is the filename of the file that you want the object to be stored in.
 * 	@return ObjectOutputStream - returns a instantiated ObjectOutputStream object, for the ability to write a object.
 * 	<p> Sample Code: </p>
 * 	<code>
 * 		ObjectOutputStream oos = openOutputFile(filename);
 *		oos.writeObject(object);
 * 	</code>
 */
  
	private ObjectOutputStream openOutputFile(String filename) throws IOException {
			// Gets the file, opens the file for writing, creates or overwrites the existing file.
			// Then it creates a wrapper stream for storing objects, and returns that stream.
			return new ObjectOutputStream(new FileOutputStream(new File(filename)));
	}
	
/**
 * 	The openInputFile method is used to open a file, instantiate a object input steam, and return that stream in-order to read a object from the file.
 * 	@author Lance Baker
 * 	@param filename String - This is the filename of the file that you want to load.
 * 	@return ObjectInputStream - returns a instantiated ObjectInputStream object, for the ability to read a object.
 * 	<p> Sample Code: </p>
 * 	<code>
 * 		Object obj = openInputFile(filename).readObject();
 * 	</code>
 */
 
	private ObjectInputStream openInputFile(String filename) throws IOException {
			// Gets the file, opens the file reading
			// Then it creates a wrapper stream for reading objects, and returns that stream.
			return new ObjectInputStream(new FileInputStream(new File(filename)));
	}
	
/**
 * 	The saveObject method can be accessed publicly for the ability to serialize a object to a file.
 * 	@author Lance Baker
 * 	@param object E, filename String - receives a object with the generic datatype, and the filename of the file that you want the object to be stored in.
 * 	<p> Sample Code: </p>
 * 	<code>
 * 		serialize.saveObject(object, FILE_NAME);
 * 	</code>
 */
 
	public void saveObject(E object, String filename) {
		try {
			// Creates or overwrites the file, gets a ObjectOutputStream, and writes the object to the stream.
			openOutputFile(filename).writeObject(object);
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
/**
 * 	The loadObject method can be accessed publicly for the ability to load a serialized object from a file.
 * 	@author Lance Baker
 * 	@param filename String - the filename of the file that contains the object you want to receive.
 *	@return object E which was read from the file.
 * 	<p> Sample Code: </p>
 * 	<code>
 * 		serialize.loadObject(FILE_NAME);
 * 	</code>
 */
	@SuppressWarnings("unchecked")
	public E loadObject(String filename) {
		E object = null; // Sets the object to null.
		try {
			// Reads the object from the stream, and casts the object as the Generic datatype.
			object = (E)openInputFile(filename).readObject();
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} catch (ClassNotFoundException ex) {
			System.out.println(ex.getMessage());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return object; // Returns the object.
	}
}
