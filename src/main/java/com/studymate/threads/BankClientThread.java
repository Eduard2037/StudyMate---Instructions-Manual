package com.studymate.threads;

import java.util.Random;

/**
 * Simulates a client performing random deposit/withdraw operations on a
 * shared BankAccount, demonstrating the need for synchronization.
 */
public class BankClientThread extends Thread {

    private final BankAccount account;
    private final Random random = new Random();

    public BankClientThread(String name, BankAccount account) {
        super(name);
        this.account = account;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            boolean deposit = random.nextBoolean();
            long amount = random.nextInt(100);

            if (deposit) {
                account.deposit(amount);
            } else {
                account.withdraw(amount);
            }
        }
        System.out.println(getName() + " finished. Balance now=" + account.getBalance());
    }
}
