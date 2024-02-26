package zsilver_csci201_Assignment2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SharedBalance {
    private int balance;
    private Lock lock = new ReentrantLock();

    public SharedBalance() {
    }
    public void setBalance(int initialBalance) {
        this.balance = initialBalance;

    }

    //used chatGPT for withdraw and deposit
    public int deposit(int amount) {
        lock.lock();
        try {
            balance += amount;
            return balance;
        } finally {
            lock.unlock();
        }
    }

    public int withdraw(int amount) {
        lock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
                return balance;
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }


    public int getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}


