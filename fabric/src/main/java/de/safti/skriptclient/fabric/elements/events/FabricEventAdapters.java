package de.safti.skriptclient.fabric.elements.events;

import de.safti.skriptclient.api.event.EventBuilder;
import de.safti.skriptclient.fabric.bridge.FabricEventBuilderExtension.WrappedFabricEvent;
import io.github.syst3ms.skriptparser.lang.SyntaxElement;
import net.fabricmc.fabric.api.event.Event;
import org.jetbrains.annotations.ApiStatus;

import static de.safti.skriptclient.fabric.bridge.FabricEventBuilderExtension.redirector;

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

    }

    private static <T> void initEvent(String name, Event<T> event, Class<T> eventClass, int... supportedArguments) {
        EventBuilder.init(name, WrappedFabricEvent.class)
                .contextValuesFactory(e -> e::values)
                .redirector(redirector(event, eventClass, supportedArguments))
                .register();
    }


}
