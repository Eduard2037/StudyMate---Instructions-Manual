package com.studymate.threads;

/**
 * Convenience main class which runs the BankAccount example.
 */
public class ThreadBenchmarkRunner {

    public static void main(String[] args) throws InterruptedException {
        BankAccount account = new BankAccount(1_000);

        BankClientThread t1 = new BankClientThread("MobileApp", account);
        BankClientThread t2 = new BankClientThread("ATM", account);
        BankClientThread t3 = new BankClientThread("CardPayment", account);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("Final balance=" + account.getBalance());
    }
}
