
package dtu.ws.fastmoney;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dtu.ws.fastmoney package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CreateAccountWithBalance_QNAME = new QName("http://fastmoney.ws.dtu/", "createAccountWithBalance");
    private final static QName _CreateAccountWithBalanceResponse_QNAME = new QName("http://fastmoney.ws.dtu/", "createAccountWithBalanceResponse");
    private final static QName _GetAccount_QNAME = new QName("http://fastmoney.ws.dtu/", "getAccount");
    private final static QName _GetAccountByCprNumber_QNAME = new QName("http://fastmoney.ws.dtu/", "getAccountByCprNumber");
    private final static QName _GetAccountByCprNumberResponse_QNAME = new QName("http://fastmoney.ws.dtu/", "getAccountByCprNumberResponse");
    private final static QName _GetAccountResponse_QNAME = new QName("http://fastmoney.ws.dtu/", "getAccountResponse");
    private final static QName _RetireAccount_QNAME = new QName("http://fastmoney.ws.dtu/", "retireAccount");
    private final static QName _RetireAccountResponse_QNAME = new QName("http://fastmoney.ws.dtu/", "retireAccountResponse");
    private final static QName _TransferMoneyFromTo_QNAME = new QName("http://fastmoney.ws.dtu/", "transferMoneyFromTo");
    private final static QName _TransferMoneyFromToResponse_QNAME = new QName("http://fastmoney.ws.dtu/", "transferMoneyFromToResponse");
    private final static QName _BankServiceException_QNAME = new QName("http://fastmoney.ws.dtu/", "BankServiceException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dtu.ws.fastmoney
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link dtu.ws.fastmoney.CreateAccountWithBalance }
     * 
     */
    public dtu.ws.fastmoney.CreateAccountWithBalance createCreateAccountWithBalance() {
        return new dtu.ws.fastmoney.CreateAccountWithBalance();
    }

    /**
     * Create an instance of {@link CreateAccountWithBalanceResponse }
     * 
     */
    public CreateAccountWithBalanceResponse createCreateAccountWithBalanceResponse() {
        return new CreateAccountWithBalanceResponse();
    }

    /**
     * Create an instance of {@link dtu.ws.fastmoney.GetAccount }
     * 
     */
    public dtu.ws.fastmoney.GetAccount createGetAccount() {
        return new dtu.ws.fastmoney.GetAccount();
    }

    /**
     * Create an instance of {@link GetAccountByCprNumber }
     * 
     */
    public GetAccountByCprNumber createGetAccountByCprNumber() {
        return new GetAccountByCprNumber();
    }

    /**
     * Create an instance of {@link GetAccountByCprNumberResponse }
     * 
     */
    public GetAccountByCprNumberResponse createGetAccountByCprNumberResponse() {
        return new GetAccountByCprNumberResponse();
    }

    /**
     * Create an instance of {@link GetAccountResponse }
     * 
     */
    public GetAccountResponse createGetAccountResponse() {
        return new GetAccountResponse();
    }

    /**
     * Create an instance of {@link dtu.ws.fastmoney.RetireAccount }
     * 
     */
    public dtu.ws.fastmoney.RetireAccount createRetireAccount() {
        return new dtu.ws.fastmoney.RetireAccount();
    }

    /**
     * Create an instance of {@link RetireAccountResponse }
     * 
     */
    public RetireAccountResponse createRetireAccountResponse() {
        return new RetireAccountResponse();
    }

    /**
     * Create an instance of {@link dtu.ws.fastmoney.TransferMoneyFromTo }
     * 
     */
    public dtu.ws.fastmoney.TransferMoneyFromTo createTransferMoneyFromTo() {
        return new dtu.ws.fastmoney.TransferMoneyFromTo();
    }

    /**
     * Create an instance of {@link dtu.ws.fastmoney.TransferMoneyFromToResponse }
     * 
     */
    public dtu.ws.fastmoney.TransferMoneyFromToResponse createTransferMoneyFromToResponse() {
        return new dtu.ws.fastmoney.TransferMoneyFromToResponse();
    }

    /**
     * Create an instance of {@link dtu.ws.fastmoney.BankServiceException }
     * 
     */
    public dtu.ws.fastmoney.BankServiceException createBankServiceException() {
        return new dtu.ws.fastmoney.BankServiceException();
    }

    /**
     * Create an instance of {@link dtu.ws.fastmoney.Account }
     * 
     */
    public dtu.ws.fastmoney.Account createAccount() {
        return new Account();
    }

    /**
     * Create an instance of {@link Transaction }
     * 
     */
    public Transaction createTransaction() {
        return new Transaction();
    }

    /**
     * Create an instance of {@link User }
     * 
     */
    public User createUser() {
        return new User();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.CreateAccountWithBalance }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.CreateAccountWithBalance }{@code >}
     */
    @XmlElementDecl(namespace = "http://fastmoney.ws.dtu/", name = "createAccountWithBalance")
    public JAXBElement<dtu.ws.fastmoney.CreateAccountWithBalance> createCreateAccountWithBalance(dtu.ws.fastmoney.CreateAccountWithBalance value) {
        return new JAXBElement<dtu.ws.fastmoney.CreateAccountWithBalance>(_CreateAccountWithBalance_QNAME, CreateAccountWithBalance.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateAccountWithBalanceResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CreateAccountWithBalanceResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://fastmoney.ws.dtu/", name = "createAccountWithBalanceResponse")
    public JAXBElement<CreateAccountWithBalanceResponse> createCreateAccountWithBalanceResponse(CreateAccountWithBalanceResponse value) {
        return new JAXBElement<CreateAccountWithBalanceResponse>(_CreateAccountWithBalanceResponse_QNAME, CreateAccountWithBalanceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.GetAccount }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.GetAccount }{@code >}
     */
    @XmlElementDecl(namespace = "http://fastmoney.ws.dtu/", name = "getAccount")
    public JAXBElement<dtu.ws.fastmoney.GetAccount> createGetAccount(dtu.ws.fastmoney.GetAccount value) {
        return new JAXBElement<dtu.ws.fastmoney.GetAccount>(_GetAccount_QNAME, GetAccount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAccountByCprNumber }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetAccountByCprNumber }{@code >}
     */
    @XmlElementDecl(namespace = "http://fastmoney.ws.dtu/", name = "getAccountByCprNumber")
    public JAXBElement<GetAccountByCprNumber> createGetAccountByCprNumber(GetAccountByCprNumber value) {
        return new JAXBElement<GetAccountByCprNumber>(_GetAccountByCprNumber_QNAME, GetAccountByCprNumber.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAccountByCprNumberResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetAccountByCprNumberResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://fastmoney.ws.dtu/", name = "getAccountByCprNumberResponse")
    public JAXBElement<GetAccountByCprNumberResponse> createGetAccountByCprNumberResponse(GetAccountByCprNumberResponse value) {
        return new JAXBElement<GetAccountByCprNumberResponse>(_GetAccountByCprNumberResponse_QNAME, GetAccountByCprNumberResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAccountResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetAccountResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://fastmoney.ws.dtu/", name = "getAccountResponse")
    public JAXBElement<GetAccountResponse> createGetAccountResponse(GetAccountResponse value) {
        return new JAXBElement<GetAccountResponse>(_GetAccountResponse_QNAME, GetAccountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.RetireAccount }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.RetireAccount }{@code >}
     */
    @XmlElementDecl(namespace = "http://fastmoney.ws.dtu/", name = "retireAccount")
    public JAXBElement<dtu.ws.fastmoney.RetireAccount> createRetireAccount(dtu.ws.fastmoney.RetireAccount value) {
        return new JAXBElement<dtu.ws.fastmoney.RetireAccount>(_RetireAccount_QNAME, RetireAccount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetireAccountResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RetireAccountResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://fastmoney.ws.dtu/", name = "retireAccountResponse")
    public JAXBElement<RetireAccountResponse> createRetireAccountResponse(RetireAccountResponse value) {
        return new JAXBElement<RetireAccountResponse>(_RetireAccountResponse_QNAME, RetireAccountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.TransferMoneyFromTo }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.TransferMoneyFromTo }{@code >}
     */
    @XmlElementDecl(namespace = "http://fastmoney.ws.dtu/", name = "transferMoneyFromTo")
    public JAXBElement<dtu.ws.fastmoney.TransferMoneyFromTo> createTransferMoneyFromTo(dtu.ws.fastmoney.TransferMoneyFromTo value) {
        return new JAXBElement<dtu.ws.fastmoney.TransferMoneyFromTo>(_TransferMoneyFromTo_QNAME, TransferMoneyFromTo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.TransferMoneyFromToResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.TransferMoneyFromToResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://fastmoney.ws.dtu/", name = "transferMoneyFromToResponse")
    public JAXBElement<dtu.ws.fastmoney.TransferMoneyFromToResponse> createTransferMoneyFromToResponse(dtu.ws.fastmoney.TransferMoneyFromToResponse value) {
        return new JAXBElement<dtu.ws.fastmoney.TransferMoneyFromToResponse>(_TransferMoneyFromToResponse_QNAME, TransferMoneyFromToResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.BankServiceException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link dtu.ws.fastmoney.BankServiceException }{@code >}
     */
    @XmlElementDecl(namespace = "http://fastmoney.ws.dtu/", name = "BankServiceException")
    public JAXBElement<dtu.ws.fastmoney.BankServiceException> createBankServiceException(dtu.ws.fastmoney.BankServiceException value) {
        return new JAXBElement<dtu.ws.fastmoney.BankServiceException>(_BankServiceException_QNAME, BankServiceException.class, null, value);
    }

}
