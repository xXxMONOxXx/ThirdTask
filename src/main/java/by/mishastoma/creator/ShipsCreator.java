package by.mishastoma.creator;

import by.mishastoma.entity.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShipsCreator {

    private static Random random = new Random();

    public static List<Ship> createShips(int minNumberOfShips, int maxNumberOfShips) {
        List<Ship> ships = new ArrayList<>();
        int numberOfShips = minNumberOfShips + random.nextInt() % (maxNumberOfShips - minNumberOfShips);
        for (int i = 0; i < numberOfShips; i++) {
            int loadOrUnload = random.nextInt() % 3;
            if(loadOrUnload == 0){
                ships.add(new Ship(0));
            }
            else {
                ships.add(new Ship(random.nextInt() % Ship.MAX_CAPACITY));
            }
        }
        return ships;
    }
}
