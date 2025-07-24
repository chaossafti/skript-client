package de.safti.skriptclient.api.synatxes.generated;

public class GeneratedResultingEvent<R> extends GeneratedEvent {
    private final Class<R> resultClass;

    public GeneratedResultingEvent(String eventName, Class<R> resultClass) {
        super(eventName, false);
        this.resultClass = resultClass;
    }

    public Class<R> getResultClass() {
        return resultClass;
    }

}
