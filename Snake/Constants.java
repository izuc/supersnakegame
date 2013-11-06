public interface Constants {
		static final String MAIN_HEADING = "Super Snake Game";
		static final String EMPTY_STRING = "";
		static final String SAVE_FILE_NAME = "GameSave.sav";
		
		static final String MAP_PATH = "maps/";
		static final String IMAGES_PATH = "images/";
		static final String SOUND_PATH = "sounds/";
		static final String DEFAULT_IMAGE = "main.jpg";
		static final String ABOUT_BOX_IMAGE = "about.jpg";
		static final String ABOUT = "About";
		static final String PNG = ".png";
		static final String BACKGROUND_IMG = "background" + PNG;
		static final int PANEL_WIDTH = 480;	 // size of panel
		static final int PANEL_HEIGHT = 360;
		
		static final int HUD_HEIGHT = 40;
		static final int HUD_SPACE = 100;
		static final int HUD_GAP = 10;
		static final String POINTS = "POINTS";
		static final String GAME_TIME = "GAME TIME";
		static final String LEVEL = "LEVEL";
		static final String ENERGY = "ENERGY";
		static final String DEFAULT_PLAYER = "Fred";
		static final String LEFT_BRACE = " (";
		static final String PERCENT = "%";
		static final String RIGHT_BRACE = ")";
		static final String PERCENT_BAR = "|";
		static final String MAXED_ENERGY = "OVERCHARGED";
		static final String HUD_FONT = "Tahoma";
		
		static final String BODY_CELL = "body";
		static final String SNAKE_HEAD = "head";
		static final String SCORE = "Score: ";
		static final String POWER_UP_IMAGE = "POWER_UP";
		static final int POWER_UP_POINTS = 200;
		
		static final int ZERO = 0;
		static final int ONE = 1;
		
		static final int WALL_MIN_DELAY = 2;
		static final int WALL_MAX_DELAY = 5;
		
		
		static final int FULLY_MAXED_ENERGY = 1500;
		static final int MAX_ENERGY = 1000;
		static final int DEFAULT_LEVEL_TIME = 30;
		
		static final int[] SNAKE_SPEED = {250, 200, 125};
		static final int GRID_SIZE = 24;
		static final int SNAKE_SIZE = GRID_SIZE/2;

		static final int INITAL_LENGTH = 3; // The inital starting length for the snake.
		static final char WALL_CODE = 'X';
		
		
		static final String[] OPTIONS_MENU = { "File", "Game", "Help" };
		static final String[] OPTIONS_FILE = { "New Game", "Restart Game", "Save Results", "Restore Game", "Exit" };
		static final String[] OPTIONS_GAME = { "Set Level", "Set Player", "View Results" };
		static final String[] OPTIONS_HELP = { "About" };
}