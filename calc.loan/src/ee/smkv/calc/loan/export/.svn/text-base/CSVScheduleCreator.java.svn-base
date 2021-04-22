package ee.smkv.calc.loan.export;

import android.content.res.Resources;
import android.os.Environment;
import ee.smkv.calc.loan.model.Loan;
import ee.smkv.calc.loan.model.Payment;
import ee.smkv.calc.loan.R;
import ee.smkv.calc.loan.utils.ViewUtil;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

/**
 * @author samko
 */
public class CSVScheduleCreator extends AbstractScheduleCreator {
    public final static String ENCODING = "UTF-16LE";


    public CSVScheduleCreator(Loan loan, Resources resources) {
        super(resources, loan);
    }


    public void checkExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // OK
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            throw new RuntimeException("We can only read the media"); //TODO localize message
        } else {
            throw new RuntimeException("media not available"); //TODO localize message
        }

    }


    public void createSchedule(Writer out) throws IOException {
        out.write('\uFEFF');
        out.write(encode(R.string.paymentNr));
        char tab = '\t';
        char ln = '\n';
        out.write(tab);
        out.write(encode(R.string.paymentBalance));
        out.write(tab);
        out.write(encode(R.string.paymentPrincipal));
        out.write(tab);
        out.write(encode(R.string.paymentInterest));
        out.write(tab);
        if (hasDisposableCommission || hasMonthlyCommission) {
            out.write(encode(R.string.paymentCommission));
            out.write(tab);
        }
        out.write(encode(R.string.paymentTotal));
        out.write(ln);

        int i = 0;

        if (hasDisposableCommission || hasDownPayment) {
            BigDecimal amount = BigDecimal.ZERO;
            out.write("0");
            out.write(tab);
            out.write(ViewUtil.formatBigDecimal(loan.getAmount()));
            out.write(tab);
            out.write(hasDownPayment ? ViewUtil.formatBigDecimal(loan.getDownPaymentPayment()) : "0.00");
            out.write(tab);
            out.write("0.00");
            out.write(tab);
            if (hasDisposableCommission) {
                amount = amount.add(loan.getDisposableCommissionPayment());
                out.write(ViewUtil.formatBigDecimal(loan.getDisposableCommissionPayment()));
                out.write(tab);
            }

            if (hasDownPayment) {
                amount = amount.add(loan.getDownPaymentPayment());
            }

            out.write(ViewUtil.formatBigDecimal(amount));
            out.write(tab);
            out.write(ln);
        }

        for (Payment payment : loan.getPayments()) {
            out.write(encode(payment.getNr().toString()));
            out.write(tab);
            out.write(encode(ViewUtil.formatBigDecimal(payment.getBalance())));
            out.write(tab);
            out.write(encode(ViewUtil.formatBigDecimal(payment.getPrincipal())));
            out.write(tab);
            out.write(encode(ViewUtil.formatBigDecimal(payment.getInterest())));
            out.write(tab);
            if (hasDisposableCommission || hasMonthlyCommission) {
                out.write(ViewUtil.formatBigDecimal(payment.getCommission()));
                out.write(tab);
            }
            out.write(encode(ViewUtil.formatBigDecimal(payment.getAmount())));
            out.write(ln);
        }

    }


    private char[] encode(int id) {
        return resources.getString(id).toCharArray();
    }

    private char[] encode(String text) {
        return text.toCharArray();
    }

    public String getFileName() {
        return "loan-" + loan.getAmount() + "-" + loan.getInterest() + "%-" + loan.getPeriod() + "M.csv";
    }
}
