package info.rednecksolutions.wurmunlimited.karma.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import info.rednecksolutions.wurmunlimited.karma.KarmaActions;

public class FileLoader 
{
	Logger logger = Logger.getLogger(this.getClass().getName());	
	String path, file;
	
	public FileLoader(String path, String file)
	{
		this.path = path;
		this.file = file;
		
		readFile();
	}
	
	public void readFile()
	{
		File f = new File(path+file);
		try(BufferedReader br = new BufferedReader(new FileReader(path+file)))
		{
			if(f.exists())
			{
				for(String lines; (lines = br.readLine()) != null;)
				{
					String[] line = lines.split(",");
					KarmaActions.rewards.add(new Reward(Integer.parseInt(line[0]), Integer.parseInt(line[1]), Integer.parseInt(line[2])));
				}
			}
			else
			{
				f.createNewFile();
				logger.log(Level.INFO, "[KA]"+path+file+" did not exist, creating.");
			}
		}
		catch(IOException ex) { logger.log(Level.SEVERE, ex.getMessage(), ex); }
	}
	
	public static class Reward
	{
		int creatureid, karma, coin;
		
		public Reward(int creatureid, int karma, int coin)
		{
			this.creatureid = creatureid;
			this.karma = karma;
			this.coin = coin;
		}
		
		public int getCreatureID() { return creatureid; }
		public int getKarmaRewardAmount() { return karma; }
		public int getCoinRewardAmount() { return coin; }
	}
}
