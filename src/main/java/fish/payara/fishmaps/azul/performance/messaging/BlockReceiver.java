package fish.payara.fishmaps.azul.performance.messaging;

import fish.payara.fishmaps.azul.performance.DurationLogger;
import fish.payara.fishmaps.world.block.Block;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(name = "BlockTopic", mappedName = "jms/BlockTopic")
public class BlockReceiver implements MessageListener {
    @Override
    public void onMessage (Message message) {
        try {
            if (message instanceof ObjectMessage objectMessage) {
                Block[] block = (Block[])objectMessage.getObject();
                long duration = System.nanoTime() - objectMessage.getLongProperty("time");
                DurationLogger.log("JMS/Receiving", block.length, duration);
            }
        }
        catch (JMSException e) {
            Logger.getLogger(BlockReceiver.class.getName())
                .log(Level.SEVERE, "Message received, error was thrown: ", e);
        }
    }
}
