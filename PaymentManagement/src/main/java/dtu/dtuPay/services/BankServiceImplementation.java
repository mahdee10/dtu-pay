package dtu.dtuPay.services;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;

import java.math.BigDecimal;

public class BankServiceImplementation {
    BankServiceService bankServiceService = new BankServiceService();
    BankService bankService = bankServiceService.getBankServicePort();

    public void transferMoney(String debtorAccountId, String creditorAccountId, BigDecimal amount, String description)
            throws BankServiceException_Exception {
            bankService.transferMoneyFromTo(debtorAccountId, creditorAccountId, amount, description);
    }


}