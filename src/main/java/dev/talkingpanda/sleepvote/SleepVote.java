package dev.talkingpanda.sleepvote;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = SleepVote.MOD_ID, acceptableRemoteVersions = "*", acceptableSaveVersions = "*")
public class SleepVote {
    public static final String MOD_ID = "sleepvote";

    public static Logger logger;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("Sleep Vote Loaded.");
    }
}
