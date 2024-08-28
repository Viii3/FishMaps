package fish.payara.fishmaps.event.request;

import fish.payara.fishmaps.event.Event;

public class EventOutputRequest extends EventRequest {
    private long timestamp;

    public EventOutputRequest () {

    }

    public EventOutputRequest (Event event) {
        super(event);
        this.timestamp = event.getTimeStamp();
    }

    public static EventOutputRequest fromEvent (Event event) {
        return new EventOutputRequest(event);
    }

    public long getTimestamp () {
        return this.timestamp;
    }

    public void setTimestamp (long timestamp) {
        this.timestamp = timestamp;
    }
}
