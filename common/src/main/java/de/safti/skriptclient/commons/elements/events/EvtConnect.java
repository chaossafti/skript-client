package de.safti.skriptclient.commons.elements.events;

import de.safti.skriptclient.api.SkriptRegistry;
import de.safti.skriptclient.api.pattern.PatternBundle;
import de.safti.skriptclient.api.pattern.PatternBundleBuilder;
import de.safti.skriptclient.api.synatxes.AbstractEvent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EvtConnect extends AbstractEvent {
    public static PatternBundle PATTERNS = PatternBundleBuilder.builder()
            .newPattern()
                .literal("connect")
                .build()
            .build();

    public record ConnectContext(ClientPacketListener connection) implements TriggerContext {

        static {
            SkriptRegistry.registerEventValue(ConnectContext.class, ClientPacketListener.class, ConnectContext::connection);
        }

        @Override
        public String getName() {
            return "connect to server";
        }
    }

    @Override
    public @NotNull PatternBundle getPatternBundle() {
        return PATTERNS;
    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        return "connect to server";
    }
}
