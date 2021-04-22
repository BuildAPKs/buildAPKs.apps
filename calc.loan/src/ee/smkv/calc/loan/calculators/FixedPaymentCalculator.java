package ee.smkv.calc.loan.calculators;

import ee.smkv.calc.loan.model.Loan;
import ee.smkv.calc.loan.model.Payment;

import java.math.BigDecimal;
import java.util.ArrayList;

public class FixedPaymentCalculator extends AbstractCalculator {


    public void calculate(Loan loan) {
        BigDecimal currentAmount = calculateAmountWithDownPayment(loan);
        loan.setResiduePayment(getResiduePayment(loan));
        boolean hasResidue = loan.getResiduePayment().compareTo( BigDecimal.ZERO) > 0;

        addDisposableCommission(loan, currentAmount);

        BigDecimal interestMonthly = loan.getInterest().divide(new BigDecimal("1200"), SCALE, MODE);
        BigDecimal monthlyAmount = loan.getFixedPayment();

        BigDecimal ma = monthlyAmount;
        BigDecimal interest = BigDecimal.ZERO;
        BigDecimal payment = BigDecimal.ZERO;
        int i = 0;

        if (loan.getAmount().divide(loan.getFixedPayment(), 0, MODE).intValue() > 1000) {
            throw new RuntimeException("Too small fixed payment part. Count of payments is over 1000 and mobile device can't calculate too big periods.");
        }
        ArrayList<Payment> payments = new ArrayList<Payment>();
        while (currentAmount.compareTo(loan.getResiduePayment()) > 0) {

            if (currentAmount.compareTo(ma) < 0) {
                ma = currentAmount;
            }

            interest = currentAmount.multiply(interestMonthly);
            payment = interest.add(ma);

            Payment p = new Payment();
            p.setNr(i + 1);
            p.setInterest(interest);
            p.setPrincipal(ma);
            p.setBalance(currentAmount);

            addPaymentWithCommission(loan, p, payment);

            payments.add(p);

            currentAmount = currentAmount.subtract(ma);
            i++;
        }
        if(hasResidue){
            interest = currentAmount.multiply(interestMonthly);
            payment = currentAmount.add(interest);
            ma = currentAmount;


            Payment p = new Payment();
            p.setNr(i + 1);
            p.setInterest(interest);
            p.setPrincipal(ma);
            p.setBalance(currentAmount);

            addPaymentWithCommission(loan, p, payment);

            payments.add(p);

            currentAmount = currentAmount.subtract(ma);
            i++;
        }

        loan.setPeriod(i);
        loan.setPayments(payments);
        loan.setEffectiveInterestRate(calculateEffectiveInterestRate(loan));
    }

}
