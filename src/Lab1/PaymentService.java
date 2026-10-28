package Lab1;

import Lab1.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PaymentService {
    private Map<Integer, Customer> customers = new HashMap<>();
    private List<Transaction> transactionHistory = new ArrayList<>();
    private Set<String> processedReferenceIds = new HashSet<>();
    private List<String> failedLogs = new ArrayList<>();

    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    public Customer getCustomer(int id) {
        return customers.get(id);
    }

    public boolean process(Account account, Transaction t) {

        if (processedReferenceIds.contains(t.getReferenceId())) {
            System.out.println("Duplicate transaction rejected: " + t.getReferenceId());
            failedLogs.add("DUPLICATE rejected: " + t.getReferenceId());
            return false;
        }

        boolean success = account.processTransaction(t);

        if (success) {
            processedReferenceIds.add(t.getReferenceId());
            transactionHistory.add(t);
        } else {
            failedLogs.add("FAILED: " + t.getReferenceId() + " (invalid amount, limit or low balance)");
        }
        return success;
    }

    public List<Transaction> getTransactionHistory() { return transactionHistory; }
    public List<String> getFailedLogs() { return failedLogs; }
    public Set<String> getProcessedReferenceIds() { return processedReferenceIds; }
}