package ee.smkv.calc.loan.export;

import android.content.res.Resources;
import ee.smkv.calc.loan.model.Loan;
import ee.smkv.calc.loan.model.Payment;
import ee.smkv.calc.loan.R;
import ee.smkv.calc.loan.utils.ViewUtil;

import java.math.BigDecimal;

/**
 * @author samko
 */
public class HtmlScheduleCreator extends AbstractScheduleCreator {


    public HtmlScheduleCreator(Loan loan, Resources resources) {
        super(resources, loan);
    }

    protected String encode(int id) {
        return resources.getString(id);
    }

    public void appendHtmlScheduleTable(StringBuilder sb) {
        sb
                .append("<table><tr class=\"odd\"><th>")
                .append(encode(R.string.paymentNr))
                .append("</th><th>")
                .append(encode(R.string.paymentBalance))
                .append("</th><th>")
                .append(encode(R.string.paymentPrincipal))
                .append("</th><th>")
                .append(encode(R.string.paymentInterest))
                .append("</th><th>");
        if (hasDisposableCommission || hasMonthlyCommission) {
            sb
                    .append(encode(R.string.paymentCommission))
                    .append("</th><th>");
        }
        sb
                .append(encode(R.string.paymentTotal))
                .append("</th></tr>");
        int i = 0;

        if (hasDisposableCommission || hasDownPayment) {
            BigDecimal amount = BigDecimal.ZERO;
            sb.append("<tr class=\"")
                    .append(i++ % 2 == 0 ? "even" : "odd")
                    .append("\"><td>0</td><td>")
                    .append(ViewUtil.formatBigDecimal(loan.getAmount()))
                    .append("</td><td>")
                    .append(hasDownPayment ? ViewUtil.formatBigDecimal(loan.getDownPaymentPayment()) : "")
                    .append("</td><td></td><td>");
            if (hasDisposableCommission) {
                amount = amount.add(loan.getDisposableCommissionPayment());
                sb
                        .append(ViewUtil.formatBigDecimal(loan.getDisposableCommissionPayment()))
                        .append("</td><td>");
            }

            if (hasDownPayment) {
                amount = amount.add(loan.getDownPaymentPayment());
            }

            sb
                    .append(ViewUtil.formatBigDecimal(amount))
                    .append("</td></tr>");
        }

        for (Payment payment : loan.getPayments()) {
            sb.append("<tr class=\"")
                    .append(i++ % 2 == 0 ? "even" : "odd")
                    .append("\"><td>")
                    .append(payment.getNr().toString())
                    .append("</td><td>")
                    .append(ViewUtil.formatBigDecimal(payment.getBalance()))
                    .append("</td><td>")
                    .append(ViewUtil.formatBigDecimal(payment.getPrincipal()))
                    .append("</td><td>")
                    .append(ViewUtil.formatBigDecimal(payment.getInterest()))
                    .append("</td><td>");
            if (hasDisposableCommission || hasMonthlyCommission) {
                sb
                        .append(ViewUtil.formatBigDecimal(payment.getCommission()))
                        .append("</td><td>");
            }
            sb

                    .append(ViewUtil.formatBigDecimal(payment.getAmount()))
                    .append("</td></tr>");
        }
        sb
                .append("<tr><th>&nbsp;</th><th>&nbsp;</th><th>")
                .append(ViewUtil.formatBigDecimal(loan.getAmount()))
                .append("</th><th>")
                .append(ViewUtil.formatBigDecimal(loan.getTotalInterests()))
                .append("</th><th>");
        if (hasDisposableCommission || hasMonthlyCommission) {
            sb
                    .append(ViewUtil.formatBigDecimal(loan.getCommissionsTotal()))
                    .append("</th><th>");
        }
        sb
                .append(ViewUtil.formatBigDecimal(loan.getTotalAmount()))
                .append("</th></tr>")
                .append("</table>");
    }


    public void appendHtmlButtons(StringBuilder sb) {
        sb.append("<button id='closeBtn' onclick='window.schedule.finish();'>")
                .append("<span style='display:inline-block;width:100%;'>")
                .append("<img align=\"absmiddle\" src='data:image/png;base64,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'/> ")
                .append("<span style='display:inline-block;width:50%;text-align:center;'>")
                .append(encode(R.string.close))
                .append("</span>")
                .append("</span>")
                .append("</button>");
    }

    public void appendHtmlChart(StringBuilder sb, int width, int height) {
        sb
                .append("<canvas id=\"pie\" width=\"")
                .append(width)
                .append("\" height=\"")
                .append(height)
                .append("\"><br />Pie diagramm</canvas>")
                .append("<canvas id=\"line\" width=\"")
                .append(width)
                .append("\" height=\"")
                .append(height)
                .append("\">Loan amotrization</canvas>");
    }


}
