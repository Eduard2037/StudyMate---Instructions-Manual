package com.studymate.threads;

/**
 * Classic synchronized bank account example (Lab 8, exercise 2).
 */
public class BankAccount {

    private long balance;

    public BankAccount(long initialBalance) {
        this.balance = initialBalance;
    }

    public synchronized void deposit(long amount) {
        balance += amount;
    }

    public synchronized boolean withdraw(long amount) {
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public synchronized long getBalance() {
        return balance;
    }
}
