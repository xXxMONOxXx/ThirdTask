package by.mishastoma;

import by.mishastoma.creator.ShipsCreator;
import by.mishastoma.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App
{

    private static final Logger logger = LogManager.getLogger();
    public static void main( String[] args )
    {
        int maxNumberOfShips = 30;
        int minNumberOfShips = 15;
        try {
            List<Ship> ships = ShipsCreator.createShips(minNumberOfShips, maxNumberOfShips);
            ExecutorService executor = Executors.newScheduledThreadPool(8);
            for (int i = 0; i < ships.size(); i++) {
                executor.execute(ships.get(i));
            }
            executor.shutdown();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
