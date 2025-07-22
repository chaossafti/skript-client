package de.safti.skriptclient.api.event;

import de.safti.skriptclient.api.event.interfaces.EventRedirector;
import de.safti.skriptclient.commons.standalone.events.CancellableTriggerContext;

import java.util.Map;

public class CancellableEventContext<E> extends EventContext<E> implements CancellableTriggerContext {
    private final E event;
    private final EventRedirector<E> redirector;

    public CancellableEventContext(String eventName, Map<String, Object> values, E event, EventRedirector<E> redirector) {
        super(eventName, values);
        this.event = event;
        this.redirector = redirector;
    }

    @Override
    public void setCancelled(boolean cancelState) {
        redirector.cancelSetter().accept(event, cancelState);
    }

    @Override
    public boolean isCancelled() {
        return redirector.cancelGetter().apply(event);
    }
}
