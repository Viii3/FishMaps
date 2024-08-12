package fish.payara.fishmaps.player;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class PlayerCache {
    private final Map<String, BufferedImage> cache = new HashMap<>();

    public void put (String name, BufferedImage profile) {
        this.cache.put(name, profile);
    }

    @Nullable
    public BufferedImage get (String name) {
        if (this.cache.containsKey(name)) return this.cache.get(name);
        return null;
    }
}
