package ee.smkv.calc.loan.calculators;

import ee.smkv.calc.loan.model.Loan;
import ee.smkv.calc.loan.model.Payment;

import java.math.BigDecimal;
import java.util.ArrayList;

public class DifferentiatedCalculator extends AbstractCalculator {
    private static final BigDecimal TWO = new BigDecimal(2);

    public void calculate(Loan loan) {
        BigDecimal amount = calculateAmountWithDownPayment(loan);
        loan.setResiduePayment(getResiduePayment(loan));
        boolean hasResidue = loan.getResiduePayment().compareTo( BigDecimal.ZERO) > 0;
        addDisposableCommission(loan, amount);

        BigDecimal interestMonthly = loan.getInterest().divide(new BigDecimal("1200"), SCALE, MODE);
        Integer period = hasResidue ?  loan.getPeriod() - 1 : loan.getPeriod();
        BigDecimal residueInterest = hasResidue? loan.getResiduePayment().multiply(interestMonthly) : BigDecimal.ZERO;

        BigDecimal monthlyAmount = amount.subtract(loan.getResiduePayment()).divide(new BigDecimal(period), SCALE, MODE);
        BigDecimal currentAmount = amount;
        ArrayList<Payment> payments = new ArrayList<Payment>();
        int i = 0 ;
        for (; i < period; i++) {
            BigDecimal interest = currentAmount.multiply(interestMonthly);
            BigDecimal payment = interest.add(monthlyAmount);

            Payment p = new Payment();
            p.setNr(i + 1);
            p.setInterest(interest);
            p.setPrincipal(monthlyAmount);
            p.setBalance(currentAmount);

            addPaymentWithCommission(loan, p, payment);

            payments.add(p);

            currentAmount = currentAmount.subtract(monthlyAmount);
        }
        if(hasResidue){
            BigDecimal payment = loan.getResiduePayment();
            BigDecimal principal = payment;
            Payment p = new Payment();
            p.setNr(i + 1);
            p.setBalance(currentAmount);
            p.setInterest(residueInterest);
            p.setPrincipal(principal);

            addPaymentWithCommission(loan, p, payment.add(residueInterest));

            payments.add(p);
            currentAmount = currentAmount.subtract(principal);

        }
        loan.setPayments(payments);
        loan.setEffectiveInterestRate(calculateEffectiveInterestRate(loan));
    }



}
