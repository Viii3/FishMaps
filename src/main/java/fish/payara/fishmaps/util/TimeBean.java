package fish.payara.fishmaps.util;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Named(value = "TimeBean")
@RequestScoped
public class TimeBean {
    public String formatMillis (long millis) {
        Instant lastSeenTime = Instant.ofEpochMilli(millis);
        ZonedDateTime zonedDateTime = lastSeenTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }
}
