package by.mishastoma.entity;

import by.mishastoma.exception.PortException;
import by.mishastoma.util.ShipIdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ship implements Runnable {

    public static final int MAX_CAPACITY = 64;
    public static final int MIN_CAPACITY = 0;
    private static final Logger logger = LogManager.getLogger();
    private final int id;
    private Pier usingPier;

    public enum ShipState {
        WAITING, RUNNING, DONE
    }

    private int capacity;
    private ShipState state;

    public Ship(int capacity) {
        this.capacity = capacity;
        state = ShipState.WAITING;
        id = ShipIdGenerator.generate();
    }

    public void setState(ShipState state) {
        this.state = state;
    }

    public void load() throws PortException {
        Port.getInstance().loadShip(capacity, id);
        capacity = MAX_CAPACITY;
    }

    public void unload() throws PortException {
        Port.getInstance().unloadShip(capacity, id);
        capacity = MIN_CAPACITY;
    }

    private boolean isShipEmpty() {
        return capacity == MIN_CAPACITY;
    }

    @Override
    public void run() {
        try {
            Port port = Port.getInstance();
            setState(ShipState.WAITING);
            usingPier = port.getFreePier();
            setState(ShipState.RUNNING);
            if (isShipEmpty()) {
                load();
            } else {
                unload();
            }
            Pier freePier = usingPier;
            usingPier = null;
            port.addFreePier(freePier);
            state = ShipState.DONE;
        } catch (PortException e) {
            logger.error(e);
        }
    }
}
