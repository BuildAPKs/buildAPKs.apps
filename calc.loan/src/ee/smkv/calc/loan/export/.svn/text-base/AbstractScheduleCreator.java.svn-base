package ee.smkv.calc.loan.export;

import android.content.res.Resources;
import ee.smkv.calc.loan.model.Loan;

import java.math.BigDecimal;

public abstract class AbstractScheduleCreator {

    int mode = BigDecimal.ROUND_HALF_EVEN;
    Resources resources;
    Loan loan;
    boolean hasDownPayment;
    boolean hasMonthlyCommission;
    boolean hasDisposableCommission;

    public AbstractScheduleCreator(Resources resources, Loan loan) {
        this.loan = loan;
        this.resources = resources;
        hasDownPayment = loan.getDownPaymentPayment() != null && loan.getDownPaymentPayment().compareTo(BigDecimal.ZERO) != 0;
        hasDisposableCommission = loan.getDisposableCommissionPayment() != null && loan.getDisposableCommissionPayment().compareTo(BigDecimal.ZERO) != 0;
        hasMonthlyCommission = loan.getMonthlyCommissionPayment() != null && loan.getMonthlyCommissionPayment().compareTo(BigDecimal.ZERO) != 0;
    }


}
