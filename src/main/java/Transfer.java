import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Transfer implements Callable<Boolean>{
    private  Account accountFrom;
    private  Account accountTo;
    private int amount;

    private static final long WAIT_SEC = 1000;

    public Transfer(Account accountFrom, Account accountTo, int amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    @Override
    public Boolean call() throws Exception {
        if(accountFrom.getBalance() < amount) {
            System.out.println("Not enough money!");
            return false;
        }
        if (accountFrom.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
            try {
                if (accountTo.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                    try {
                        accountFrom.withdraw(amount);
                        System.out.println("withdraw " + amount);
                        accountTo.deposit(amount);
                        System.out.println("deposit " + amount);
                        Thread.sleep(WAIT_SEC);
                    } finally {
                        accountTo.getLock().unlock();
                    }
                } else {
                    accountTo.incFailedCount();
                }
            } finally {
                accountFrom.getLock().unlock();
                return true;
            }
        } else {
            accountFrom.incFailedCount();
            return false;
        }
    }
}
