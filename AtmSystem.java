import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Enum representing transaction types
enum TransactionType {
    DEPOSIT,
    WITHDRAWAL
}

// Class representing an individual transaction record
class Transaction {
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;

    public Transaction(TransactionType type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    @Override
    public String toString() {
        return String.format("Type: %-10s | Amount: $%-8.2f | Balance After: $%.2f", 
                type, amount, balanceAfter);
    }
}

// Class representing a User Account
class Account {
    private final String accountNumber;
    private final String pin;
    private double balance;
    private final List<Transaction> transactionHistory;

    public Account(String accountNumber, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("[-] Deposit amount must be greater than zero.");
            return;
        }
        balance += amount;
        transactionHistory.add(new Transaction(TransactionType.DEPOSIT, amount, balance));
        System.out.printf("[+] Successfully deposited $%.2f. New Balance: $%.2f%n", amount, balance);
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("[-] Withdrawal amount must be greater than zero.");
            return false;
        }
        if (amount > balance) {
            System.out.println("[-] Insufficient funds.");
            return false;
        }
        balance -= amount;
        transactionHistory.add(new Transaction(TransactionType.WITHDRAWAL, amount, balance));
        System.out.printf("[+] Successfully withdrew $%.2f. Remaining Balance: $%.2f%n", amount, balance);
        return true;
    }

    public void printTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        if (transactionHistory.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction t : transactionHistory) {
                System.out.println(t);
            }
        }
    }
}

// Main ATM Controller Class
public class AtmSystem {
    private final List<Account> accounts;
    private Account currentAccount;
    private final Scanner scanner;

    public AtmSystem() {
        accounts = new ArrayList<>();
        scanner = new Scanner(System.in);
        seedAccounts();
    }

    // Seed mock account data for testing
    private void seedAccounts() {
        accounts.add(new Account("1001", "1234", 1500.00));
        accounts.add(new Account("1002", "5678", 500.50));
    }

    private Account findAccount(String accNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accNumber)) {
                return acc;
            }
        }
        return null;
    }

    public void start() {
        System.out.println("====================================");
        System.out.println("    WELCOME TO THE JAVA ATM SYSTEM  ");
        System.out.println("====================================");

        if (authenticateUser()) {
            showMenu();
        } else {
            System.out.println("[-] Authentication failed. Exiting system.");
        }
    }

    private boolean authenticateUser() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            System.out.print("\nEnter Account Number: ");
            String accNum = scanner.nextLine().trim();

            System.out.print("Enter 4-Digit PIN: ");
            String pin = scanner.nextLine().trim();

            Account acc = findAccount(accNum);

            if (acc != null && acc.validatePin(pin)) {
                currentAccount = acc;
                System.out.println("\n[+] Authentication Successful!");
                return true;
            } else {
                attempts++;
                System.out.printf("[-] Invalid Account Number or PIN. Attempt %d of %d.%n", 
                        attempts, MAX_ATTEMPTS);
            }
        }
        return false;
    }

    private void showMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n------------------------------------");
            System.out.println("             MAIN MENU              ");
            System.out.println("------------------------------------");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. View Transaction History");
            System.out.println("5. Exit");
            System.out.print("Choose an option (1-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.printf("%n[i] Current Balance: $%.2f%n", currentAccount.getBalance());
                    break;

                case "2":
                    double depAmount = getValidDoubleInput("Enter deposit amount ($): ");
                    currentAccount.deposit(depAmount);
                    break;

                case "3":
                    double withAmount = getValidDoubleInput("Enter withdrawal amount ($): ");
                    currentAccount.withdraw(withAmount);
                    break;

                case "4":
                    currentAccount.printTransactionHistory();
                    break;

                case "5":
                    System.out.println("\n[+] Thank you for using our ATM. Goodbye!");
                    exit = true;
                    break;

                default:
                    System.out.println("[-] Invalid option. Please enter a number between 1 and 5.");
            }
        }
    }

    private double getValidDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("[-] Invalid input. Please enter a valid numerical value.");
            }
        }
    }

    public static void main(String[] args) {
        AtmSystem atm = new AtmSystem();
        atm.start();
    }
}