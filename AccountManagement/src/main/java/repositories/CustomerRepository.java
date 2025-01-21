package repositories;

import models.Customer;
import java.util.HashMap;
import java.util.UUID;

public class CustomerRepository {
    private static CustomerRepository instance;
    private HashMap<UUID, Customer> customers;

    private CustomerRepository() {
        customers = new HashMap<>();
    }

    public static CustomerRepository getInstance() {
        if (instance == null) {
            synchronized (CustomerRepository.class) {
                if (instance == null) {
                    instance = new CustomerRepository();
                }
            }
        }
        return instance;
    }

    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    public Customer getCustomer(UUID id) {
        return customers.get(id);
    }

    public boolean removeCustomer(UUID id) {
        if (customers.containsKey(id)) {
            System.out.println("found");
            customers.remove(id);
            return true;
        }
        return false;
    }

    public UUID getCustomerByCPR(String cpr) {
        for (Customer customer : customers.values()) {
            if (customer.getCpr().equalsIgnoreCase(cpr)) {
                return customer.getId();
            }
        }

        return null;
    }
}
