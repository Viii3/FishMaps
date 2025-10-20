package fish.payara.fishmaps.azul.performance.messaging;

import fish.payara.fishmaps.world.block.Block;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(name = "BlockTopic", mappedName = "jms/BlockTopic")
public class BlockReceiver implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            String printable = null;
            if (message.isBodyAssignableTo(String.class)) {
                printable = message.getBody(String.class);
            }
            if (printable == null) {
                return;
            }
            
            Logger.getLogger(BlockReceiver.class.getName())
                .log(Level.INFO, printable);
        }
        catch (JMSException e) {
            Logger.getLogger(BlockReceiver.class.getName())
                .log(Level.SEVERE, "Message received, error was thrown: " + e.getMessage());
        }
    }
}
