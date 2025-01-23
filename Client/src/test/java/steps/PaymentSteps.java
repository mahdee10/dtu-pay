package steps;

import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import models.dtos.PaymentRequestDto;
import models.dtos.TokenRequestDto;
import models.dtos.UserRequestDto;
import org.junit.jupiter.api.Assertions;
import services.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentSteps {
    User userCustomer;
    User userMerchant;
    private String customerBankAccountId;
    private String merchantBankAccountId;
    private UUID customerId;
    private UUID customerToken;
    private UUID merchantId;
    private boolean paymentIsSuccessful;
    private String exceptionMessage;
    BankServiceImplementation bankService = new BankServiceImplementation();

    private static List<String> createdAccountIds = new ArrayList<>();
    private CustomerService customerService = new CustomerService();
    private MerchantService merchantService = new MerchantService();
    private PaymentService paymentService = new PaymentService();
    private TokenService tokenService = new TokenService();

    @io.cucumber.java.After
    public void cleanupAccounts() throws BankServiceException_Exception {
        for (String accountId : createdAccountIds) {
            bankService.deleteAccount(accountId);
        }

        createdAccountIds.clear();
    }

    @Given("customer with name {string}, last name {string}, and CPR {string}")
    public void customer_with_name_last_name_and_cpr(String firstName, String lastName, String cpr) {
        userCustomer = new User();
        userCustomer.setFirstName(firstName);
        userCustomer.setLastName(lastName);
        userCustomer.setCprNumber(cpr);
    }

    @Given("customer is registered with the bank with an initial balance of {double} kr")
    public void customer_is_registered_with_the_bank_with_an_initial_balance_of_kr(Double balance) throws BankServiceException_Exception {
        try {
            customerBankAccountId = bankService.createAccount(
                    userCustomer.getFirstName(),
                    userCustomer.getLastName(),
                    userCustomer.getCprNumber(),
                    new BigDecimal(balance)
            );
        } catch (BankServiceException_Exception e) {
            customerBankAccountId = bankService.getAccountByCPR(userCustomer.getCprNumber()).getId();
        }

        createdAccountIds.add(customerBankAccountId);
    }

    @Given("customer is registered with Simple DTU Pay using their bank account")
    public void customer_is_registered_with_simple_dtu_pay_using_their_bank_account() throws Exception {
        UserRequestDto payloadUser = new UserRequestDto();
        payloadUser.setFirstName(userCustomer.getFirstName());
        payloadUser.setLastName(userCustomer.getLastName());
        payloadUser.setCpr(userCustomer.getCprNumber());
        payloadUser.setBankAccountId(customerBankAccountId);

        customerId = customerService.createCustomer(payloadUser);
        assertNotNull(customerId, "Customer ID should not be null");
    }

    @Given("merchant with name {string}, last name {string}, and CPR {string}")
    public void merchant_with_name_last_name_and_cpr(String firstName, String lastName, String cpr) {
        userMerchant = new User();
        userMerchant.setFirstName(firstName);
        userMerchant.setLastName(lastName);
        userMerchant.setCprNumber(cpr);
    }

    @Given("merchant is registered with the bank with an initial balance of {double} kr")
    public void merchant_is_registered_with_the_bank_with_an_initial_balance_of_kr(Double balance) throws BankServiceException_Exception {
        try {
            merchantBankAccountId = bankService.createAccount(
                    userMerchant.getFirstName(),
                    userMerchant.getLastName(),
                    userMerchant.getCprNumber(),
                    new BigDecimal(balance)
            );
        } catch (BankServiceException_Exception e) {
            merchantBankAccountId = bankService.getAccountByCPR(userMerchant.getCprNumber()).getId();
        }

        createdAccountIds.add(merchantBankAccountId);
    }

    @Given("merchant is registered with Simple DTU Pay using their bank account")
    public void merchant_is_registered_with_simple_dtu_pay_using_their_bank_account() throws Exception {
        UserRequestDto payloadUser = new UserRequestDto();
        payloadUser.setFirstName(userMerchant.getFirstName());
        payloadUser.setLastName(userMerchant.getLastName());
        payloadUser.setCpr(userMerchant.getCprNumber());
        payloadUser.setBankAccountId(merchantBankAccountId);

        merchantId = merchantService.createMerchant(payloadUser);
        assertNotNull(merchantId, "merchant ID should not be null");
    }

    @Given("the customer request {int} tokens")
    public void theCustomerRequestTokens(Integer int1) throws Exception {
        int nTokensCreated = tokenService.createTokens(new TokenRequestDto(customerId, int1));
        assertEquals(nTokensCreated, int1.intValue());
    }

    @When("the merchant initiates a payment for {int} kr by using the customer token")
    public void theMerchantInitiatesAPaymentForKrByTheCustomerToken(Integer amount) throws Exception {
        customerToken = tokenService.getTokens(customerId).getFirst();
        paymentIsSuccessful = paymentService.pay(
                new PaymentRequestDto(customerToken, merchantId, amount)
        );
    }

    @Then("the payment is successful")
    public void the_payment_is_successful() {
        assertTrue(paymentIsSuccessful);
    }

    @Then("the balance of the customer at the bank is {int} kr")
    public void the_balance_of_the_customer_at_the_bank_is_kr(Integer balance) throws Throwable {
        BigDecimal expectedBalance = BigDecimal.valueOf(balance).stripTrailingZeros();
        BigDecimal actualBalance = bankService.getAccount(customerBankAccountId).getBalance().stripTrailingZeros();

        assertEquals(expectedBalance, actualBalance);
    }

    @Then("the balance of the merchant at the bank is {int} kr")
    public void the_balance_of_the_merchant_at_the_bank_is_kr(Integer balance) throws Throwable {
        BigDecimal expectedBalance = BigDecimal.valueOf(balance).stripTrailingZeros();
        BigDecimal actualBalance = bankService.getAccount(merchantBankAccountId).getBalance().stripTrailingZeros();

        assertEquals(expectedBalance, actualBalance);
    }

    @Then("the customer has {int} tokens")
    public void theCustomerHasTokens(Integer int1) throws Exception {
        List<UUID> availableTokens = tokenService.getTokens(customerId);
        assertEquals(availableTokens.size(), int1.intValue());
    }

    @When("the merchant initiates a new payment for {int} kr reusing the customer token")
    public void the_merchant_initiates_a_new_payment_for_kr_reusing_the_customer_token(Integer amount) {
        try {
            paymentIsSuccessful = paymentService.pay(
                    new PaymentRequestDto(customerToken, merchantId, amount)
            );
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }

    }

    @Then("the payment is unsuccessful and the exception message {string} is returned")
    public void thePaymentIsUnsuccessfulAndTheExceptionMessageIsReturned(String mesasage) {
        assertEquals(mesasage, exceptionMessage);
    }

}
