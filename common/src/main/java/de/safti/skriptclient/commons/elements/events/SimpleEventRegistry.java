package de.safti.skriptclient.commons.elements.events;

import de.safti.skriptclient.api.event.EventBuilder;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientPlayerEvent.ClientPlayerJoin;
import dev.architectury.event.events.common.BlockEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static dev.architectury.event.events.client.ClientPlayerEvent.CLIENT_PLAYER_JOIN;
import static dev.architectury.event.events.client.ClientPlayerEvent.CLIENT_PLAYER_QUIT;

/**
 * This class is used to build events using {@link de.safti.skriptclient.api.event.EventBuilder}
 */
public class SimpleEventRegistry {
    public static final String JOIN = "join server";
    public static final String QUIT = "quit a server";
    public static final String RESPAWN = "respawn";
    public static final String BREAK_BLOCK = "block broken";


    static {

        // connect
        EventBuilder.create(JOIN)
                .pattern("connect [to [a] server]", "join [[a] server]")
                .eventValue("player", LocalPlayer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(CLIENT_PLAYER_JOIN, ClientPlayerJoin.class, 0)
                .register();

        EventBuilder.create(QUIT)
                .pattern("disconnect")
                .eventValue("player", ClientPacketListener.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(CLIENT_PLAYER_QUIT, ClientPlayerEvent.ClientPlayerQuit.class, 0)
                .register();


        // play
        EventBuilder.create(BREAK_BLOCK)
                .pattern("break block")
                .eventValue("world", Level.class)
                .eventValue("pos", BlockPos.class)
                .eventValue("blockstate", BlockState.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(BlockEvent.BREAK, BlockEvent.Break.class, 0, 1, 2)
                .register();


    }

}
