package de.safti.skriptclient.fabric.bridge;

import de.safti.skriptclient.api.event.EventRedirector;
import net.fabricmc.fabric.api.event.Event;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class FabricEventBuilderExtension {


    // God, please forgive me for this absolutely disguising code I wrote.

    /**
     *
     * @param event The event to register
     * @param listenerClass the class of the listener
     * @param <L> The Listener type
     * @return An eventRedirector that will define the consumer consuming the invoked WrappedFabricEvent
     */
    public static <L> EventRedirector<WrappedFabricEvent> redirector(Event<L> event, Class<L> listenerClass, int... supportedArguments) {
        // the event consumer will in the end invoke SkriptEventManager#call
        // check it out in EventRegistration#register
        AtomicReference<Consumer<WrappedFabricEvent>> eventConsumerRef = new AtomicReference<>();

        // the EventRedirector will set the event consumer used for this event
        // its essentially a completable future
        AtomicReference<EventRedirector<WrappedFabricEvent>> eventRedirectorRef = new AtomicReference<>(eventConsumerRef::set);


        // Create a dynamic proxy of listener interface
        // this proxy will listen for when the event is called, wrap all arguments into a WrappedFabricEvent
        // and dispatch the WrappedFabricEvent to the eventConsumer stored in eventConsumerRef
        L listenerProxy = listenerClass.cast(Proxy.newProxyInstance(
                listenerClass.getClassLoader(),
                new Class[]{listenerClass},
                (proxy, method, args) -> {

                    WrappedFabricEvent wrappedFabricEvent = new WrappedFabricEvent(copySelectedIndices(args, supportedArguments));
                    eventConsumerRef.get().accept(wrappedFabricEvent);
                    return null;
                }
        ));

        // register our proxy lisener
        event.register(listenerProxy);

        // return our redirector
        return eventRedirectorRef.get();
    }

    public static Object[] copySelectedIndices(Object[] source, int[] indices) {
        Object[] result = new Object[indices.length];
        for (int i = 0; i < indices.length; i++) {
            result[i] = source[indices[i]];
        }
        return result;
    }


    public record WrappedFabricEvent(Object[] values) {

    }


    private FabricEventBuilderExtension() {

    }

}
