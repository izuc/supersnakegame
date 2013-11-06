import java.util.*;
import javax.swing.*;

/**
*	The Menu extends the JMenuBar for easy implementation to a Frame or JDialog. 
*	It receives the menuText in a form of ArrayList of String[] which contains the names for the JMenuItems.
*	@author: Lance Baker.
**/

public class Menu extends JMenuBar { // The menu extends the JMenuBar, for easy implementation.
		public static final long serialVersionUID = 1L;
		private ArrayList<JMenu> menuOptions = new ArrayList<JMenu>(); // Stores the JMenu options in a ArrayList<JMenu>
		
		// Receives the ArrayList of String[] , and the Corresponding Action with the Commands.
		public Menu(ArrayList<String[]> menuText, Action action, Object[] commands) {
				JPopupMenu.setDefaultLightWeightPopupEnabled(false); // Sets it so the components are not lightweight, so therefore it appears ontop of other heavyweight components.
				
				// The first string[] array is the JMenu (for instance: File, Game, Help.. etc)
				String[] optionsMenu = menuText.get(0);
				for (int index = 0; index < optionsMenu.length; index++) {
					this.menuOptions.add(new JMenu(optionsMenu[index]));
				}
				
				// The following String[] arrays after the first, is then added to the JMenu. Which therefore creates the associated JMenuItems.
				for (int index = 1; index < menuText.size(); index++) { // Loops for each String[] after the first headings.
					String[] menuItems = menuText.get(index); // Gets the array for the JMenuItems to be created from.
					for (int i = 0; i < menuItems.length; i++) { // Iterates for each JMenuItem to be created,
						this.menuOptions.get(index - 1).add(new JMenuItem(menuItems[i])); // Adds each item to the corresponding JMenu
					}
				}
				for (JMenu menu : this.menuOptions) {
					this.add(menu); // Adds the JMenus to itself.
				}
				
				int command = 0; // a counter used to get the appropriate action corresponding to the JMenuItem
				for (JMenu menu : this.menuOptions) { // Loops for each JMenu
					for (int i = 0; i < menu.getMenuComponentCount(); i++) { // For every component inside of the JMenu
						((JMenuItem)menu.getMenuComponent(i)).addActionListener(action); // Adds the Action Listener to the action Received.
						if (command < commands.length) { // Checks to see if there is a action command for this JMenuItem
							// Sets the ActionCommand for each JMenuItem in the JMenu components. The action command is based on the index of the command Object[] received.
							((JMenuItem)menu.getMenuComponent(i)).setActionCommand(commands[command].toString());
							command++; // Increments to the next action
						}
					}
				}
		}
				
		public ArrayList<JMenu> getMenu() {
				return this.menuOptions; // Returns the ArrayList<JMenu> for further use else where.
		}
		
		public JMenu getMenuAt(int index) {
				return this.menuOptions.get(index); // Gets the JMenu at a given index.
		}
}