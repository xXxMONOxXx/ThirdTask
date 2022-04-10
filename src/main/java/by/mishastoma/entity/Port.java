package by.mishastoma.entity;

import by.mishastoma.exception.PortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Port {

    private static final Logger logger = LogManager.getLogger();
    private static final int MAX_CAPACITY = 1024;
    private static final int MIN_CAPACITY = 0;
    private static final int NUMBER_OF_PIERS = 8;
    private static final int MAX_TIME_FOR_ONE_CONTAINER = 100;
    private static final int MIN_TIME_FOR_CONTAINER = 30;
    private static final Random random = new Random();

    private static final ReentrantLock piersLock = new ReentrantLock(true);
    private static final ReentrantLock cargoLock = new ReentrantLock(true);
    private static final Condition pierCondition = piersLock.newCondition();
    private static final Condition cargoCondition = cargoLock.newCondition();

    private static final AtomicBoolean wasCreated = new AtomicBoolean(false);
    private static Port instance;

    private Queue<Pier> freePiers;
    private int portFreeCapacity;

    private Port(){
        freePiers = new LinkedList<>();
        for(int i=0;i<NUMBER_OF_PIERS;i++){
            freePiers.add(new Pier());
        }
        this.portFreeCapacity = MAX_CAPACITY;
    }

    public static Port getInstance() {
        if (!wasCreated.get()) {
            piersLock.lock();
            try {
                if (instance == null) {
                    instance = new Port();
                    wasCreated.set(true);
                }
            } finally {
                piersLock.unlock();
            }
        }

        return instance;
    }

    private long getTimeForContainer(){
        return MIN_TIME_FOR_CONTAINER + random.nextLong()%MAX_TIME_FOR_ONE_CONTAINER;
    }

    public void loadShip(int cargoCapacity, int shipId) throws PortException{
        cargoLock.lock();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
            while (portFreeCapacity - cargoCapacity < MIN_CAPACITY) {
                logger.info("Ship {} is waiting for free pier.", shipId);
                cargoCondition.await();
            }
            for(int i=0;i<cargoCapacity;i++){
                TimeUnit.MILLISECONDS.sleep(getTimeForContainer());
            }
            portFreeCapacity -= cargoCapacity;
            cargoCondition.signalAll();
            logger.info("Ship {} was loaded.", shipId);
        } catch (InterruptedException e) {
            logger.error(e);
            Thread.currentThread().interrupt();
            throw new PortException(e.getMessage());
        } finally {
            cargoLock.unlock();
        }
    }

    public void unloadShip(int capacity, int shipId) throws PortException{
        cargoLock.lock();
        try {
            while (portFreeCapacity + capacity > MAX_CAPACITY) {
                logger.info("Ship {} is waiting for free pier.", shipId);
                cargoCondition.await();
            }

            for(int i=0;i<capacity;i++){
                TimeUnit.MILLISECONDS.sleep(getTimeForContainer());
            }
            portFreeCapacity += capacity;
            cargoCondition.signalAll();
            logger.info("Ship {} was unloaded.", shipId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PortException(e);
        } finally {
            cargoLock.unlock();
        }
    }

    public Pier getFreePier() throws PortException {
        Pier pier;
        piersLock.lock();
        try {
            while ((pier = freePiers.poll()) == null) {
                logger.info("Waiting for free pier.");
                pierCondition.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PortException(e);
        } finally {
            piersLock.unlock();
        }
        logger.info("Free pier found, id {}", pier.getId());
        return pier;
    }

    public void addFreePier(Pier freePier){
        piersLock.lock();
        try {
            freePiers.add(freePier);
            pierCondition.signalAll();
            logger.info("Added free pier, id {}", freePier.getId());
        } finally {
            piersLock.unlock();
        }
    }
}
