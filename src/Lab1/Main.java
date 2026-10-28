package Lab1;

public class Main {
    public static void main(String[] args) {
        PaymentService service = new PaymentService();

        Customer alice = new Customer(1, "Alice Uwase", "alice@mail.com", "0788000000");
        WalletAccount wallet = new WalletAccount(101, 50000);
        SavingsAccount savings = new SavingsAccount(102, 100000);
        alice.addAccount(wallet);
        alice.addAccount(savings);
        service.addCustomer(alice);

        System.out.println(alice);
        System.out.println(wallet);
        System.out.println(savings);

        System.out.println("\n--- Wallet: deposit 10000 (REF-001) ---");
        System.out.println("Success? " + service.process(wallet, new Transaction(1, "REF-001", 10000, "DEPOSIT")));

        System.out.println("\n--- Wallet: withdraw 5000 (REF-002) ---");
        System.out.println("Success? " + service.process(wallet, new Transaction(2, "REF-002", 5000, "WITHDRAW")));

        System.out.println("\n--- Savings: withdraw 10000 (REF-003), fee applies ---");
        System.out.println("Success? " + service.process(savings, new Transaction(3, "REF-003", 10000, "WITHDRAW")));

        System.out.println("\n--- Savings: deposit 50 (REF-004), below the minimum ---");
        System.out.println("Success? " + service.process(savings, new Transaction(4, "REF-004", 50, "DEPOSIT")));

        System.out.println("\n--- DUPLICATE: same REF-002 sent again (timeout retry) ---");
        System.out.println("Success? " + service.process(wallet, new Transaction(5, "REF-002", 5000, "WITHDRAW")));

        System.out.println("\n--- Wallet: withdraw too much (REF-006) ---");
        System.out.println("Success? " + service.process(wallet, new Transaction(6, "REF-006", 999999, "WITHDRAW")));

        System.out.println("\nBalances now:");
        System.out.println(wallet);
        System.out.println(savings);

        System.out.println("\nTransaction history:");
        for (Transaction t : service.getTransactionHistory()) {
            System.out.println(t);
        }

        System.out.println("\nProcessed reference IDs: " + service.getProcessedReferenceIds());

        System.out.println("\nFailed logs:");
        for (String log : service.getFailedLogs()) {
            System.out.println(log);
        }
    }
}