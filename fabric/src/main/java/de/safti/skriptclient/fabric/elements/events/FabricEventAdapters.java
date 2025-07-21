package de.safti.skriptclient.fabric.elements.events;

import de.safti.skriptclient.api.event.EventBuilder;
import de.safti.skriptclient.fabric.bridge.FabricEventBuilderExtension;
import io.github.syst3ms.skriptparser.lang.SyntaxElement;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join;
import org.jetbrains.annotations.ApiStatus;

import static de.safti.skriptclient.commons.elements.events.SimpleEventRegistry.*;

/**
 * This class is used for setting event redirector and context factory of an {@link de.safti.skriptclient.api.event.EventRegistration}
 * <p>
 * It extends SyntaxElement so it can be loaded by the registered syntax package without throwing.
 *
 * @see de.safti.skriptclient.api.event.EventBuilder
 */
@ApiStatus.NonExtendable
public abstract class FabricEventAdapters implements SyntaxElement {


    static {

        EventBuilder.init(CONNECT, FabricEventBuilderExtension.WrappedFabricEvent.class)
                .contextValuesFactory(event -> event::values)
                .redirector(FabricEventBuilderExtension.redirector(ClientPlayConnectionEvents.JOIN, Join.class, 0))
                .register();

    }


}
