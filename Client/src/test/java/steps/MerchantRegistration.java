package steps;

import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.User;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import dtu.dtuPay.dtos.UserRequestDto;
import dtu.dtuPay.services.BankServiceImplementation;
import dtu.dtuPay.services.MerchantService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MerchantRegistration {
    User userMerchant;
    private String accountId;
    private boolean isMerchantUnregistered;
    BankServiceImplementation bankService = new BankServiceImplementation();

    private List<String> createdAccountIds = new ArrayList<>();
    private MerchantService merchantService =new MerchantService();
    private UUID merchantId;



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
    public void the_Merchant_is_registered_with_the_bank_with_an_initial_balance_of_kr(Double balance) throws BankServiceException_Exception {
        try {
            accountId = bankService.createAccount(
                    userMerchant.getFirstName(),
                    userMerchant.getLastName(),
                    userMerchant.getCprNumber(),
                    new BigDecimal(balance)
            );
        } catch (BankServiceException_Exception e) {
            accountId = bankService.getAccountByCPR(userMerchant.getCprNumber()).getId();
        }

        registerAccount(accountId);
    }
    @Then("the merchant is registered with Simple DTU Pay using their bank account")
    public void the_merchant_is_registered_with_simple_dtu_pay_using_their_bank_account() throws Exception {
        UserRequestDto payloadUser = new UserRequestDto();
        payloadUser.setFirstName(userMerchant.getFirstName());
        payloadUser.setLastName(userMerchant.getLastName());
        payloadUser.setCpr(userMerchant.getCprNumber());
        payloadUser.setBankAccountId(accountId);

        merchantId = merchantService.createMerchant(payloadUser);
        assertNotNull(merchantId, "merchant ID should not be null");
    }

    @When("the merchant unregisters")
    public void theMerchantUnregisters() throws Exception {
        isMerchantUnregistered = merchantService.unregisterCustomer(merchantId);
    }

    @Then("the merchant is not registered anymore")
    public void theMerchantIsNotRegisteredAnymore() {
        assertTrue(isMerchantUnregistered);
    }
}
