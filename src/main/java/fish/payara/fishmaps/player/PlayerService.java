package fish.payara.fishmaps.player;

import fish.payara.fishmaps.world.block.Block;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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

    public void update (String name, int x, int z, String dimension) {
        Player player = this.entityManager.find(Player.class, name);
        player.setLocation(Block.getDescriptor(x, z, dimension));
        player.setTimeLastSeen(System.currentTimeMillis());

        this.add(player);
    }
}
