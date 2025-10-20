package fish.payara.fishmaps.azul.performance.messaging;

import fish.payara.fishmaps.world.block.Block;
import fish.payara.fishmaps.world.block.BlockEvent;
import fish.payara.fishmaps.world.block.BlockListEvent;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Observes;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.jms.Topic;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class BlockSender {
    @Resource(lookup = "jms/ConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup = "jms/BlockTopic")
    private Topic topic;

    public void add (@Observes BlockEvent event) {
        this.sendBlockMessage(List.of(event.block()));
    }

    public void add (@Observes BlockListEvent event) {
        this.sendBlockMessage(event.blocks());
    }
    
    private void sendBlockMessage (List<Block> blocks) {
        try (JMSContext context = connectionFactory.createContext()) {
            Logger.getLogger(BlockSender.class.getName())
                .log(Level.INFO, "Preparing " + blocks.size() + " JMS messages.");
            long startTime = System.nanoTime();
            for (Block block : blocks) {
                context.createProducer().send(topic, block);
            }
            long duration = System.nanoTime() - startTime;
            context.createProducer().send(topic, "Duration nanos: " + duration);
            context.createProducer().send(topic, context.createMessage());
        }
    }
}
