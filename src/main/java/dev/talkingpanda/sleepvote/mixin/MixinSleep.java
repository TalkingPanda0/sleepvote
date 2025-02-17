package dev.talkingpanda.sleepvote.mixin;

import dev.talkingpanda.sleepvote.SleepVote;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;




@Mixin(WorldServer.class)
public abstract class MixinSleep extends World implements IThreadListener {
    private final double REQUIREDPLAYERS = 0.3;

    @Shadow private boolean allPlayersSleeping;

    protected MixinSleep(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updateAllPlayersSleepingFlag() {
        this.allPlayersSleeping = false;

        if (!this.playerEntities.isEmpty()) {
            int spectators = 0;
            int asleepPlayers = 0;

            for (EntityPlayer entityplayer : this.playerEntities) {
                if (entityplayer.isSpectator()) spectators++;
                else if (entityplayer.isPlayerSleeping()) asleepPlayers++;
            }
            if(asleepPlayers == 0) return;

            long requiredAsleepPlayers = Math.max(Math.round((this.playerEntities.size() - spectators) * REQUIREDPLAYERS),1);
            this.allPlayersSleeping = asleepPlayers >= requiredAsleepPlayers;

            String msg = "Skipping Night (" + asleepPlayers + "/" + requiredAsleepPlayers + ")";
            this.playerEntities.forEach((player)  -> player.sendStatusMessage(new TextComponentTranslation(msg),true));
            SleepVote.logger.info(msg);
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean areAllPlayersAsleep() {
       if(!this.allPlayersSleeping || this.isRemote) return false;
       long asleepPlayers = 0;
       long spectators = 0;
        for (EntityPlayer entityplayer : this.playerEntities) {
            if(entityplayer.isPlayerFullyAsleep()) asleepPlayers++;
            else if (entityplayer.isSpectator()) spectators++;
            else continue;

            if (asleepPlayers >= Math.round((this.playerEntities.size() - spectators) * REQUIREDPLAYERS)) return true;
            String msg = "Skipping Night (" + asleepPlayers + "/" + Math.round((this.playerEntities.size() - spectators) * REQUIREDPLAYERS) + ")";
            SleepVote.logger.info(msg);
        }
        return false;
    }
}
