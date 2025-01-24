/**
 * @author Mahdi El Dirani s233031
 */
package steps;

import dtu.dtuPay.dtos.TokenRequestDto;
import dtu.dtuPay.services.TokenService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import dtu.dtuPay.dtos.UserRequestDto;
import dtu.dtuPay.services.BankServiceImplementation;
import dtu.dtuPay.services.CustomerService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerRegistration {
    User userCustomer;
    private String accountId;
    private UUID customerId;
    private boolean isCustomerUnregistered;
    private String exceptionMessage;

    BankServiceImplementation bankService = new BankServiceImplementation();

    private List<String> createdAccountIds = new ArrayList<>();
    CustomerService customerService = new CustomerService();
    private TokenService tokenService = new TokenService();


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


    @When("a customer with name {string}, last name {string}, and CPR {string}")
    public void a_customer_with_name_last_name_and_cpr(String firstName, String lastName, String cpr) {
        userCustomer = new User();
        userCustomer.setFirstName(firstName);
        userCustomer.setLastName(lastName);
        userCustomer.setCprNumber(cpr);
    }

    @Then("the customer is registered with the bank with an initial balance of {double} kr")
    public void the_customer_is_registered_with_the_bank_with_an_initial_balance_of_kr(Double balance) throws BankServiceException_Exception {
        try {
            accountId = bankService.createAccount(
                    userCustomer.getFirstName(),
                    userCustomer.getLastName(),
                    userCustomer.getCprNumber(),
                    new BigDecimal(balance)
            );
        } catch (BankServiceException_Exception e) {
            accountId = bankService.getAccountByCPR(userCustomer.getCprNumber()).getId();
        }

        registerAccount(accountId);
    }

    @Then("the customer is registered with DTU Pay using their bank account")
    public void the_customer_is_registered_with_dtu_pay_using_their_bank_account() throws Exception {
        UserRequestDto payloadUser = new UserRequestDto();
        payloadUser.setFirstName(userCustomer.getFirstName());
        payloadUser.setLastName(userCustomer.getLastName());
        payloadUser.setCpr(userCustomer.getCprNumber());
        payloadUser.setBankAccountId(accountId);

        customerId = customerService.createCustomer(payloadUser);
        assertNotNull(customerId, "Customer ID should not be null");
    }

    @When("the customer unregisters")
    public void theCustomerUnregisters() throws Exception {
        isCustomerUnregistered = customerService.unregisterCustomer(customerId);
    }

    @Then("the customer is not registered anymore")
    public void theCustomerIsNotRegisteredAnymore() {
        assertTrue(isCustomerUnregistered);
    }

    @Given("the customer has already {int} tokens")
    public void theCustomerHasAlreadyTokens(Integer nTokens) throws Exception {
        int nTokensCreated = tokenService.createTokens(new TokenRequestDto(customerId, nTokens));
        assertEquals(nTokens, nTokensCreated);
    }


    @When("the customer requests to create {int} more tokens")
    public void theCustomerRequestsToCreateMoreTokens(Integer nTokens) {
        try {
            tokenService.createTokens(new TokenRequestDto(customerId, nTokens));
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
    }

    @When("the customer requests to get tokens")
    public void theCustomerRequestsToGetTokens() {
        try {
            tokenService.getTokens(customerId);
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
    }

    @Then("the the request is unsuccessful and the exception message {string} is returned")
    public void theTheRequestIsUnsuccessfulAndTheExceptionMessageIsReturned(String expectedExceptionMessage) {
        assertEquals(expectedExceptionMessage, exceptionMessage);
    }

}
