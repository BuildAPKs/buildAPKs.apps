package ee.smkv.calc.loan.export;

import android.content.res.Resources;
import ee.smkv.calc.loan.model.Loan;
import ee.smkv.calc.loan.model.Payment;
import ee.smkv.calc.loan.R;
import ee.smkv.calc.loan.utils.ViewUtil;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author samko
 */
public class TextScheduleCreator extends AbstractScheduleCreator {

    public TextScheduleCreator(Loan loan, Resources resources) {
        super(resources, loan);
    }

    public void appendTextScheduleTable(StringBuilder sb) {
        BigDecimal max = loan.getMaxMonthlyPayment();
        BigDecimal min = loan.getMinMonthlyPayment();
        String monthlyPayment = "";

        if (max.compareTo(min) == 0) {
            monthlyPayment = ViewUtil.formatBigDecimal(max);
        } else {
            monthlyPayment = ViewUtil.formatBigDecimal(max) + " - " + ViewUtil.formatBigDecimal(min);
        }


        sb
                .append(encode(R.string.app_name))
                .append('\n')
                .append('\n')
                .append(encode(R.string.type))
                .append(": ")
                .append(resources.getStringArray(R.array.types)[loan.getLoanType()])
                .append('\n')
                .append(encode(R.string.amount))
                .append(": ")
                .append(ViewUtil.formatBigDecimal(loan.getAmount()))
                .append('\n');
        if (hasDownPayment) {
            String dp = Loan.VALUE == loan.getDownPaymentType() ?
                    ViewUtil.formatBigDecimal(loan.getDownPayment()) :
                    ViewUtil.formatBigDecimal(loan.getDownPayment()) + "% (" +
                            ViewUtil.formatBigDecimal(loan.getDownPaymentPayment()) + ")";

            sb
                    .append(encode(R.string.downPayment))
                    .append(": ")
                    .append(dp)
                    .append('\n');
        }
        sb
                .append(encode(R.string.MonthlyAmountLbl))
                .append(": ")
                .append(monthlyPayment)
                .append('\n')
                .append(encode(R.string.IterestTotalLbl))
                .append(": ")
                .append(ViewUtil.formatBigDecimal(loan.getTotalInterests()))
                .append('\n');
        if (hasDisposableCommission || hasMonthlyCommission) {
            sb
                    .append(encode(R.string.commissionTotalLabel))
                    .append(": ")
                    .append(ViewUtil.formatBigDecimal(loan.getCommissionsTotal()))
                    .append('\n');
        }
        sb
                .append(encode(R.string.AmountTotalLbl))
                .append(": ")
                .append(ViewUtil.formatBigDecimal(loan.getTotalAmount()))
                .append('\n')
                .append(encode(R.string.paymentsCount))
                .append(": ")
                .append(loan.getPeriod())
                .append('\n')
                .append('\n');


        String pnr = encode(R.string.paymentNr);
        String bal = encode(R.string.paymentBalance);
        String pri = encode(R.string.paymentPrincipal);
        String inr = encode(R.string.paymentInterest);
        String com = encode(R.string.paymentCommission);
        String tor = encode(R.string.paymentTotal);
        sb
                .append(pnr)
                .append(' ').append('|').append(' ')
                .append(bal)
                .append(' ').append('|').append(' ')
                .append(pri)
                .append(' ').append('|').append(' ')
                .append(inr)
                .append(' ').append('|').append(' ');
        if (hasDisposableCommission || hasMonthlyCommission) {

            sb
                    .append(com)
                    .append(' ').append('|').append(' ');
        }
        sb
                .append(tor)
                .append("\n");
        int i = 0;
        String amountS = ViewUtil.formatBigDecimal(loan.getAmount());
        int bl = bal.length()< amountS.length()? amountS.length(): bal.length();
        int nl = 3;
        int pl = pri.length();
        int cl = com.length();
        int tl = tor.length();
        if (hasDisposableCommission || hasDownPayment) {
            BigDecimal amount = BigDecimal.ZERO;
            sb
                    .append(pad("0", nl))
                    .append(' ').append('|').append(' ')
                    .append(pad(amountS, bl))
                    .append(' ').append('|').append(' ')
                    .append(pad(hasDownPayment ? ViewUtil.formatBigDecimal(loan.getDownPaymentPayment()) : "", pl))
                    .append(' ').append('|').append(' ')
                    .append(pad("", inr.length()))
                    .append(' ').append('|').append(' ');
            if (hasDisposableCommission) {
                amount = amount.add(loan.getDisposableCommissionPayment());
                sb
                        .append(pad(ViewUtil.formatBigDecimal(loan.getDisposableCommissionPayment()), cl))
                        .append(' ').append('|').append(' ');
            }

            if (hasDownPayment) {
                amount = amount.add(loan.getDownPaymentPayment());
            }

            sb
                    .append(pad(ViewUtil.formatBigDecimal(amount), tl))
                    .append("\n");
        }
        for (Payment payment : loan.getPayments()) {
            sb
                    .append(pad(payment.getNr().toString(), nl))
                    .append(' ').append('|').append(' ')
                    .append(pad(ViewUtil.formatBigDecimal(payment.getBalance()), bl))
                    .append(' ').append('|').append(' ')
                    .append(pad(ViewUtil.formatBigDecimal(payment.getPrincipal()), pl))
                    .append(' ').append('|').append(' ')
                    .append(pad(ViewUtil.formatBigDecimal(payment.getInterest()), inr.length()))
                    .append(' ').append('|').append(' ');
            if (hasDisposableCommission || hasMonthlyCommission) {
                sb
                        .append(pad(ViewUtil.formatBigDecimal(payment.getCommission()), cl))
                        .append(' ').append('|').append(' ');
            }
            sb
                    .append(pad(ViewUtil.formatBigDecimal(payment.getAmount()), tl))
                    .append('\n');
        }
    }

    private String pad(String s, int length) {
        if (s.length() < length) {
            char[] spaces = new char[length - s.length()];
            Arrays.fill(spaces, ' ');
            return new String(spaces) + s;
        }
        return s;
    }


    private String encode(int id) {
        return resources.getString(id);
    }


}
