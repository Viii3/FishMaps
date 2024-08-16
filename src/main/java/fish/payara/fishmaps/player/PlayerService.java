package fish.payara.fishmaps.player;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class PlayerService {
    @PersistenceContext
    private EntityManager entityManager;

    public Player get (String name) {
        return this.entityManager.find(Player.class, name);
    }

    public int count () {
        try {
            Number number = (Number)this.entityManager.createNamedQuery(Player.QUERY_COUNT).getSingleResult();
            return number.intValue();
        }
        catch (Exception e) {
            return 0;
        }
    }

    public void add (Player player) {
        Player preexisting = this.get(player.getName());
        if (preexisting == null) this.entityManager.persist(player);
        else {
            this.entityManager.remove(preexisting);
            this.entityManager.flush();
            this.entityManager.persist(player);
        }
    }

    public List<Player> get (int minX, int maxX, int minZ, int maxZ, String dimension) {
        return this.entityManager.createNamedQuery(Player.QUERY_POSITION, Player.class)
            .setParameter("minX", minX)
            .setParameter("maxX", maxX)
            .setParameter("minZ", minZ)
            .setParameter("maxZ", maxZ)
            .setParameter("dimension", dimension)
            .getResultList();
    }
}
