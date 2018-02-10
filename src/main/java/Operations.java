import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Operations {

    private static final long WAIT_SEC = 1000;

    static void transfer(Account acc1, Account acc2, int amount) throws InterruptedException {
        if(acc1.getBalance() < amount) {
            System.out.println("Not enough money!");
            return;
        }
        if (acc1.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
            try {
                if (acc2.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                    try {
                        acc1.withdraw(amount);
                        System.out.println("withdraw " + amount);
                        acc2.deposit(amount);
                        System.out.println("deposit " + amount);
                    } finally {
                        acc2.getLock().unlock();
                    }
                } else {
                    acc2.incFailedCount();
                }
            } finally {
                acc1.getLock().unlock();
            }
        } else {
            acc1.incFailedCount();
        }
    }

    public static void main(String[] args) {
        final Account a = new Account(1000);
        final Account b = new Account(2000);

        ExecutorService service = Executors.newFixedThreadPool(3);
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            service.submit(
                    new Transfer(a, b, random.nextInt(400))
            );
        }

        service.shutdown();

        try {
            service.awaitTermination(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
