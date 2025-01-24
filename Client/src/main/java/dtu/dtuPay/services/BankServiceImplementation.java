/**
 * @author Mahdi El Dirani s233031
 */
package dtu.dtuPay.services;

import dtu.ws.fastmoney.*;
import dtu.dtuPay.services.interfaces.IBankService;

import java.math.BigDecimal;

public class BankServiceImplementation implements IBankService {

    BankServiceService bankServiceService = new BankServiceService();
    BankService bankService = bankServiceService.getBankServicePort();

    @Override
    public String createAccount(String firstName, String lastName, String cpr, BigDecimal initialBalance) throws BankServiceException_Exception {
            // Create a new User object
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setCprNumber(cpr);

            // Return the account ID
            return bankService.createAccountWithBalance(user, initialBalance);
    }

    @Override
    public Account getAccount(String accountId) throws BankServiceException_Exception {
        return bankService.getAccount(accountId);
    }

    public Account getAccountByCPR(String Cpr) throws BankServiceException_Exception {
        return bankService.getAccountByCprNumber(Cpr);
    }

    public void deleteAccount(String accountId) {
        try {
            bankService.retireAccount(accountId);
            System.out.println("Deleted account: " + accountId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete account: " + accountId);
        }
    }

}
