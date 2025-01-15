package steps;

import dtu.ws.fastmoney.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import services.BankServiceImplementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MerchantRegistration {
    User userMerchant;
    private String accountId;
    BankServiceImplementation bankService = new BankServiceImplementation();

    private static List<String> createdAccountIds = new ArrayList<>();


    @io.cucumber.java.After
    public void cleanupAccounts() {
        for (String accountId : createdAccountIds) {
            try {
                bankService.deleteAccount(accountId);
                System.out.println("Account " + accountId + " deleted after test");
            } catch (Exception e) {
                System.out.println("Failed to delete account " + accountId + ": " + e.getMessage());
            }
        }
        createdAccountIds.clear();
    }

    private void registerAccount(String accountId) {
        createdAccountIds.add(accountId);  // Track created account ids
    }


    @When("a merchant with name {string}, last name {string}, and CPR {string}")
    public void a_Merchant_with_name_last_name_and_cpr(String firstName, String lastName, String cpr) {
        userMerchant = new User();
        userMerchant.setFirstName(firstName);
        userMerchant.setLastName(lastName);
        userMerchant.setCprNumber(cpr);
    }
    @Then("the merchant is registered with the bank with an initial balance of {double} kr")
    public void the_Merchant_is_registered_with_the_bank_with_an_initial_balance_of_kr(Double balance) {
        accountId = bankService.createAccount(
                userMerchant.getFirstName(),
                userMerchant.getLastName(),
                userMerchant.getCprNumber(),
                new BigDecimal(balance)
        );
        registerAccount(accountId);
    }
}
