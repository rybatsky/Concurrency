import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private int balance;
    private Lock lock = new ReentrantLock();
    private AtomicInteger failedCounter = new AtomicInteger(0);

    public Account(int balance) {
        this.balance = balance;
    }
    
    public void withdraw(int amount) {
        balance -= amount;
    }    
    
    public void deposit(int amount) {
        balance += amount;
    }

    public void incFailedCount() {
        failedCounter.incrementAndGet();
    }

    public int getBalance() {
        return balance;
    }

    public Lock getLock() {
        return lock;
    }
}
