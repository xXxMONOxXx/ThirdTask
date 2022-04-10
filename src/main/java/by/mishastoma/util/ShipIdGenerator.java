package by.mishastoma.util;

public class ShipIdGenerator {
    private static int nextIdValue = 0;

    public static int generate(){
        return nextIdValue++;
    }
}
