package ee.smkv.calc.loan.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Andrei Samkov
 */
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer nr = 0;
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal principal = BigDecimal.ZERO;
    private BigDecimal interest = BigDecimal.ZERO;
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal commission = BigDecimal.ZERO;

    public Payment(Integer nr, BigDecimal balance, BigDecimal principal, BigDecimal interest, BigDecimal amount) {
        this.nr = nr;
        this.balance = balance;
        this.principal = principal;
        this.interest = interest;
        this.amount = amount;
    }

    public Payment() {
    }

    public Integer getNr() {
        return nr;
    }

    public void setNr(Integer nr) {
        this.nr = nr;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }
}
