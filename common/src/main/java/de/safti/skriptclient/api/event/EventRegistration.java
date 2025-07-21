package de.safti.skriptclient.api.event;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.SkriptRegistry;
import io.github.syst3ms.skriptparser.types.Type;

import java.lang.reflect.Array;

public final class EventRegistration<T> {
    private final EventBuilder builder;
    private final Class<T> modEventClass;

    private EventContextFactory<T> contextFactory;
    private EventRedirector<T> redirector;

    public EventRegistration(EventBuilder builder, Class<T> modEventClass) {
        this.builder = builder;
        this.modEventClass = modEventClass;
    }

    public EventRegistration<T> contextValuesFactory(EventContextFactory<T> factory) {
        this.contextFactory = factory;
        return this;
    }

    public EventRegistration<T> redirector(EventRedirector<T> redirector) {
        this.redirector = redirector;
        return this;
    }

    public void register() {
        if(contextFactory == null || redirector == null)
            throw new IllegalStateException("Both contextFactory and redirector must be defined.");

        // Register event
        SkriptRegistry.registerEvent(() -> new GeneratedEvent(builder.getEventName()), builder.getPattern());

        // Register values
        for (var entry : builder.getNamedValues().entrySet()) {
            String valueName = entry.getKey();
            Type<?> type = entry.getValue();

            registerEventValue(type, valueName);
        }


        redirector.onEvent(event -> {
            EventContextData data = contextFactory.create(event);
            EventContext context = builder.createContext(data);

            SkriptClient.INSTANCE.getEventManager()
                    .callEvent(builder.getEventName(), context);
        });
    }

    private <T1> void registerEventValue(Type<T1> type, String valueName) {
        // TODO: multi-value expressions

        SkriptRegistry.registerEventValue(
                EventContext.class,
                type.getTypeClass(),
                true,
                valueName,
                ctx -> {
                    Class<T1> clazz = type.getTypeClass();
                    T1[] arr = (T1[]) Array.newInstance(clazz, 1); // only of length one because this doesn't support multi-value event values yet
                    T1 value = (T1) ctx.get(valueName);
                    arr[0] = value;
                    return arr;
                });
    }

}

