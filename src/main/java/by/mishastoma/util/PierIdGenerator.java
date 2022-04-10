package by.mishastoma.util;

public class PierIdGenerator {
    private static int nextIdValue = 0;

    public static int generate(){
        return nextIdValue++;
    }
}
