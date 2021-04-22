package ee.smkv.calc.loan.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrei Samkov
 */
public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int PERCENT = 0;
    public static final int VALUE = 1;

    private int loanType = 0;
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal interest = BigDecimal.ZERO;
    private BigDecimal fixedPayment = BigDecimal.ZERO;
    private Integer period = 0;
    private List<Payment> payments = new ArrayList<Payment>();

    private BigDecimal totalInterests = BigDecimal.ZERO;
    private BigDecimal minimalPayment = BigDecimal.ZERO;
    private BigDecimal maximalPayment = BigDecimal.ZERO;

    private BigDecimal downPayment;
    private BigDecimal disposableCommission;
    private BigDecimal monthlyCommission;
    private BigDecimal residue;

    private int downPaymentType;
    private int disposableCommissionType;
    private int monthlyCommissionType;
    private int residueType;

    private BigDecimal downPaymentPayment;
    private BigDecimal disposableCommissionPayment;
    private BigDecimal monthlyCommissionPayment;
    private BigDecimal residuePayment;

    private BigDecimal commissionsTotal = BigDecimal.ZERO;

    private BigDecimal effectiveInterestRate = BigDecimal.ZERO;

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
        totalInterests = BigDecimal.ZERO;
        minimalPayment = BigDecimal.ZERO;
        maximalPayment = BigDecimal.ZERO;
        commissionsTotal = BigDecimal.ZERO;

        if (disposableCommissionPayment != null) {
            commissionsTotal = commissionsTotal.add(disposableCommissionPayment);
        }

        for (Payment payment : payments) {
            totalInterests = totalInterests.add(payment.getInterest());
            if (minimalPayment.equals(BigDecimal.ZERO)) {
                minimalPayment = payment.getAmount();
            } else {
                minimalPayment = minimalPayment.min(payment.getAmount());
            }
            maximalPayment = maximalPayment.max(payment.getAmount());


            if (payment.getCommission() != null) {
                commissionsTotal = commissionsTotal.add(payment.getCommission());
            }
        }
    }

    public BigDecimal getTotalAmount() {
        BigDecimal total = amount.add(totalInterests);

        if( getCommissionsTotal() != null && getCommissionsTotal().compareTo(BigDecimal.ZERO) != 0){
            total = total.add( getCommissionsTotal());
        }
        return total;
    }

    public BigDecimal getTotalInterests() {
        return totalInterests;
    }

    public BigDecimal getMaxMonthlyPayment() {
        return maximalPayment;
    }


    public BigDecimal getMinMonthlyPayment() {
        return minimalPayment;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public List<Payment> getPayments() {
        return new ArrayList<Payment>(payments);
    }

    public BigDecimal getFixedPayment() {
        return fixedPayment;
    }

    public void setFixedPayment(BigDecimal fixedPayment) {
        this.fixedPayment = fixedPayment;
    }

    public int getLoanType() {
        return loanType;
    }

    public void setLoanType(int loanType) {
        this.loanType = loanType;
    }

    public BigDecimal getMonthlyCommission() {
        return monthlyCommission;
    }

    public void setMonthlyCommission(BigDecimal monthlyCommission) {
        this.monthlyCommission = monthlyCommission;
    }

    public BigDecimal getDisposableCommission() {
        return disposableCommission;
    }

    public void setDisposableCommission(BigDecimal disposableCommission) {
        this.disposableCommission = disposableCommission;
    }

    public BigDecimal getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(BigDecimal downPayment) {
        this.downPayment = downPayment;
    }

    public int getDownPaymentType() {
        return downPaymentType;
    }

    public void setDownPaymentType(int downPaymentType) {
        this.downPaymentType = downPaymentType;
    }

    public int getDisposableCommissionType() {
        return disposableCommissionType;
    }

    public void setDisposableCommissionType(int disposableCommissionType) {
        this.disposableCommissionType = disposableCommissionType;
    }

    public int getMonthlyCommissionType() {
        return monthlyCommissionType;
    }

    public void setMonthlyCommissionType(int monthlyCommissionType) {
        this.monthlyCommissionType = monthlyCommissionType;
    }

    public BigDecimal getDownPaymentPayment() {
        return downPaymentPayment;
    }

    public void setDownPaymentPayment(BigDecimal downPaymentPayment) {
        this.downPaymentPayment = downPaymentPayment;
    }

    public BigDecimal getMonthlyCommissionPayment() {
        return monthlyCommissionPayment;
    }

    public void setMonthlyCommissionPayment(BigDecimal monthlyCommissionPayment) {
        this.monthlyCommissionPayment = monthlyCommissionPayment;
    }

    public BigDecimal getDisposableCommissionPayment() {
        return disposableCommissionPayment;
    }

    public void setDisposableCommissionPayment(BigDecimal disposableCommissionPayment) {
        this.disposableCommissionPayment = disposableCommissionPayment;
    }

    public BigDecimal getCommissionsTotal() {
        return commissionsTotal;
    }

    public BigDecimal getEffectiveInterestRate() {
      return effectiveInterestRate;
    }

    public void setEffectiveInterestRate(BigDecimal effectiveInterestRate) {
      this.effectiveInterestRate = effectiveInterestRate;
    }

    public BigDecimal getResiduePayment() {
        return residuePayment;
    }

    public void setResiduePayment(BigDecimal residuePayment) {
        this.residuePayment = residuePayment;
    }

    public int getResidueType() {
        return residueType;
    }

    public void setResidueType(int residueType) {
        this.residueType = residueType;
    }

    public BigDecimal getResidue() {
        return residue;
    }

    public void setResidue(BigDecimal residue) {
        this.residue = residue;
    }
}
