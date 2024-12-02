package fish.payara.fishmaps;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestConfiguration extends Application {
    public static final String ROLE_ADMIN = "admin";
}
