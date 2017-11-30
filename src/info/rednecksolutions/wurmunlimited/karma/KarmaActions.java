package info.rednecksolutions.wurmunlimited.karma;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import com.wurmonline.server.items.Item;

import info.rednecksolutions.wurmunlimited.karma.config.Constants;
import info.rednecksolutions.wurmunlimited.karma.utilities.FileLoader;
import info.rednecksolutions.wurmunlimited.karma.utilities.FileLoader.Reward;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class KarmaActions implements WurmServerMod, Configurable, ServerStartedListener, Initable
{
	Logger logger = Logger.getLogger(this.getClass().getName());
	
	public static ArrayList<Reward> rewards = new ArrayList<Reward>();
	FileLoader fileLoader = null;

	@Override public void onServerStarted()
	{
		
	}

	@Override public void configure(Properties props)
	{
		Constants.DEBUG = Boolean.parseBoolean(props.getProperty(Constants.PROP_DEBUG));
		Constants.KARMA_ACTION_BURY = Integer.parseInt(props.getProperty(Constants.PROP_KARMA_ACTION_BURY));
		Constants.KARMA_ACTION_GRAVE = Integer.parseInt(props.getProperty(Constants.PROP_KARMA_ACTION_GRAVE));
		Constants.KARMA_REWARD_CREATURETYPES = Boolean.parseBoolean(props.getProperty(Constants.PROP_KARMA_REWARD_CREATURETYPES));
		Constants.KARMA_REWARD_CREATURETYPES_GRAVES = Boolean.parseBoolean(props.getProperty(Constants.PROP_KARMA_REWARD_CREATURETYPES_GRAVES));
		Constants.COIN_REWARD_CREATURETYPES = Boolean.parseBoolean(props.getProperty(Constants.PROP_COIN_REWARD_CREATURETYPES));
		Constants.COIN_REWARD_CREATURETYPES_GRAVES = Boolean.parseBoolean(props.getProperty(Constants.PROP_COIN_REWARD_CREATURETYPES_GRAVES));
		Constants.FILELOADER_FILE = props.getProperty(Constants.PROP_FILELOADER_FILE);
		Constants.FILELOADER_PATH = props.getProperty(Constants.PROP_FILELOADER_PATH);
		
		fileLoader = new FileLoader(Constants.FILELOADER_PATH, Constants.FILELOADER_FILE);
		
		if(Constants.DEBUG)
		{
			logger.log(Level.INFO, "[KA] Karma Action Bury: "+Constants.KARMA_ACTION_BURY);
			logger.log(Level.INFO, "[KA] Karma Action Grave: "+Constants.KARMA_ACTION_GRAVE);
			logger.log(Level.INFO, "[KA] Karma reward for creature types: "+Constants.KARMA_REWARD_CREATURETYPES);
			logger.log(Level.INFO, "[KA] Karma reward for creature types graves: "+Constants.KARMA_REWARD_CREATURETYPES_GRAVES);
			logger.log(Level.INFO, "[KA] Coin reward for creature types: "+Constants.COIN_REWARD_CREATURETYPES);
			logger.log(Level.INFO, "[KA] Coin reward for creature types graves: "+Constants.COIN_REWARD_CREATURETYPES_GRAVES);
			logger.log(Level.INFO, "[KA] FileLoader Enabled: "+Constants.FILELOADER_ENABLED);
			logger.log(Level.INFO, "[KA] FileLoader Path: "+Constants.FILELOADER_PATH);
			logger.log(Level.INFO, "[KA] FileLoader File: "+Constants.FILELOADER_FILE);
		}
	}
	
	@Override public void init() 
	{	
		try
		{
			String descriptBuryMethods = Descriptor.ofMethod(CtClass.booleanType, new CtClass[] {
					HookManager.getInstance().getClassPool().get("com.wurmonline.server.behaviours.Action"),
					HookManager.getInstance().getClassPool().get("com.wurmonline.server.creatures.Creature"),
					HookManager.getInstance().getClassPool().get("com.wurmonline.server.items.Item"),
					HookManager.getInstance().getClassPool().get("com.wurmonline.server.items.Item"),
					CtClass.floatType,
					CtClass.shortType
			});
			CtMethod ctBury = HookManager.getInstance().getClassPool().get("com.wurmonline.server.behaviours.CorpseBehaviour").getMethod("bury", descriptBuryMethods);
			/*ctBury.insertAfter("performer.setKarma(performer.getKarma() + info.rednecksolutions.wurmunlimited.karma.config.Constants.KARMA_ACTION_BURY);"+
                "performer.getCommunicator().sendNormalServerMessage(\"You gain \"+info.rednecksolutions.wurmunlimited.karma.config.Constants.KARMA_ACTION_BURY+\" karma for burying the \"+corpse.getName()+\".\"); break;");
                */
			ctBury.instrument(new ExprEditor( ) {
				public void edit(MethodCall m) throws CannotCompileException
				{
					if("com.wurmonline.server.creatures.Creature".equals(m.getClassName()) && m.getMethodName().equals("checkCoinAward"))
					{
						m.replace("performer.setKarma(performer.getKarma() + info.rednecksolutions.wurmunlimited.karma.config.Constants.KARMA_ACTION_BURY); "
								+ "performer.getCommunicator().sendNormalServerMessage(\"You gain \"+info.rednecksolutions.wurmunlimited.karma.config.Constants.KARMA_ACTION_BURY+\" karma for burying the \"+corpse.getName()+\".\");"
								+ "com.wurmonline.server.behaviours.KarmaActions.rewardPlayer(performer, corpse, false);"
								+ "$_ = $proceed($$);");
					}
				}
			});
			
			String descriptGraveMethods = Descriptor.ofMethod(CtClass.booleanType, new CtClass[] {
					HookManager.getInstance().getClassPool().get("com.wurmonline.server.behaviours.Action"),
					HookManager.getInstance().getClassPool().get("com.wurmonline.server.creatures.Creature"),
					HookManager.getInstance().getClassPool().get("com.wurmonline.server.items.Item"),
					HookManager.getInstance().getClassPool().get("com.wurmonline.server.items.Item"),
					CtClass.floatType
			});
			CtMethod ctCreateGrave = HookManager.getInstance().getClassPool().get("com.wurmonline.server.behaviours.CorpseBehaviour").getMethod("createGrave", descriptGraveMethods);
			ctCreateGrave.instrument(new ExprEditor( ) {
				public void edit(MethodCall m) throws CannotCompileException
				{
					if("com.wurmonline.server.creatures.Creature".equals(m.getClassName()) && m.getMethodName().equals("achievement"))
					{
						m.replace("performer.setKarma(performer.getKarma() + info.rednecksolutions.wurmunlimited.karma.config.Constants.KARMA_ACTION_GRAVE); "
								+ "performer.getCommunicator().sendNormalServerMessage(\"You gain \"+info.rednecksolutions.wurmunlimited.karma.config.Constants.KARMA_ACTION_BURY+\" karma for creating a grave.\");"
								+ "com.wurmonline.server.behaviours.KarmaActions.rewardPlayer(performer, corpse, true);"
								+ "$_ = $proceed($$);");
					}
				}
			});
		}
		catch(CannotCompileException ex) { logger.log(Level.SEVERE, ex.getMessage(), ex.getStackTrace()); }
		catch(Exception ex) { logger.log(Level.SEVERE, ex.getMessage(), ex.getStackTrace()); }
	}
	
	public static void rewardPlayer(Creature performer, Item corpse, boolean isGrave) throws NoSuchCreatureTemplateException
	{
		CreatureTemplate template = CreatureTemplateFactory.getInstance().getTemplate(corpse.getData1());
		
		if(rewards.size() > 1)
		{
			for(Reward reward : rewards)
			{
				Logger.getLogger(KarmaActions.class.getName()).log(Level.INFO, "Creature ID: "+reward.getCreatureID()+", Karma Reward: "+reward.getKarmaRewardAmount()+", Coin Reward: "+reward.getCoinRewardAmount());
				if(template.getTemplateId() == reward.getCreatureID())
				{
					performer.getCommunicator().sendSafeServerMessage("matched! Creature ID: "+reward.getCreatureID());
				}
			}
		}
	}
}
