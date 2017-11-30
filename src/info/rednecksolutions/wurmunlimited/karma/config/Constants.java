package info.rednecksolutions.wurmunlimited.karma.config;

public class Constants {
	public static boolean DEBUG = true;
	
	public static int KARMA_ACTION_BURY = 5;
	public static int KARMA_ACTION_GRAVE = 5;
	
	public static boolean KARMA_REWARD_CREATURETYPES = true;
	public static boolean KARMA_REWARD_CREATURETYPES_GRAVES = true;
	
	public static boolean COIN_REWARD_CREATURETYPES = true;
	public static boolean COIN_REWARD_CREATURETYPES_GRAVES = true;
	
	public static boolean FILELOADER_ENABLED = true;
	public static String FILELOADER_FILE = "rewards.txt";
	public static String FILELOADER_PATH = "mods\\karmaactions\\";
	
	//Property Values
	public static String PROP_DEBUG = "debug";
	public static String PROP_KARMA_ACTION_BURY = "action.bury";
	public static String PROP_KARMA_ACTION_GRAVE = "action.grave";
	public static String PROP_KARMA_REWARD_CREATURETYPES = "reward.karma.creatures";
	public static String PROP_KARMA_REWARD_CREATURETYPES_GRAVES = "reward.karma.creatures.graves";
	public static String PROP_COIN_REWARD_CREATURETYPES = "reward.coin.creatures";
	public static String PROP_COIN_REWARD_CREATURETYPES_GRAVES = "reward.coin.creatures.graves";
	public static String PROP_FILELOADER_FILE = "fileloader.file";
	public static String PROP_FILELOADER_PATH = "fileloader.path";
}
