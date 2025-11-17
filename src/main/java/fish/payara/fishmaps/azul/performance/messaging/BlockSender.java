package fish.payara.fishmaps.azul.performance.messaging;

import fish.payara.fishmaps.azul.performance.DurationLogger;
import fish.payara.fishmaps.world.block.Block;
import fish.payara.fishmaps.world.block.BlockEvent;
import fish.payara.fishmaps.world.block.BlockListEvent;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Observes;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.JMSProducer;
import jakarta.jms.ObjectMessage;
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
        long t1 = System.nanoTime();
        try (JMSContext context = connectionFactory.createContext()) {
            JMSProducer producer = context.createProducer();
            ObjectMessage message = context.createObjectMessage();
            message.setObject(blocks.toArray(new Block[0]));
            message.setLongProperty("time", System.nanoTime());
            producer.send(topic, message);
        }
        catch (JMSException e) {
            Logger.getLogger(BlockSender.class.getName())
                .log(Level.SEVERE, "[JMS/Sending] Encountered error: ", e);
        }
        
        long duration = System.nanoTime() - t1;
        DurationLogger.log("JMS/Sending", blocks.size(), duration);
    }
}
