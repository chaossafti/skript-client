package de.safti.skriptclient.api.event;

import de.safti.skriptclient.api.event.interfaces.EventRedirector;
import de.safti.skriptclient.commons.standalone.events.CancellableTriggerContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ResultingEventContext<E, R> extends EventContext<E> implements CancellableTriggerContext {
    private final E event;
    private final EventRedirector<E> redirector;
    private final Class<R> resultClass;

    private R result;

    public ResultingEventContext(String eventName, Map<String, Object> values, E event, EventRedirector<E> redirector, Class<R> resultClass, @Nullable R initialResult) {
        super(eventName, values, event, redirector);
        this.event = event;
        this.redirector = redirector;
        this.resultClass = resultClass;
        this.result = initialResult;
    }

    @Override
    public void setCancelled(boolean cancelState) {
        redirector.cancelSetter().accept(event, cancelState);
    }

    @Override
    public boolean isCancelled() {
        return redirector.cancelGetter().apply(event);
    }

    public Class<R> getResultClass() {
        return resultClass;
    }

    public R getResult() {
        return result;
    }

    public void setResult(R result) {
        this.result = result;
    }
}
