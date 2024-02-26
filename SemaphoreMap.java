package zsilver_csci201_Assignment2;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.HashMap;
import java.util.Map;

public class SemaphoreMap {
    private static ConcurrentHashMap<String, Semaphore> semaphoreMap = new ConcurrentHashMap<>();

    public synchronized void createSemaphore(String ticker, int num) {
        Semaphore semaphore = new Semaphore(num);

        semaphoreMap.put(ticker, semaphore);
    }

    public static Semaphore getSemaphore(String ticker) {
        return semaphoreMap.get(ticker);
    }
}
