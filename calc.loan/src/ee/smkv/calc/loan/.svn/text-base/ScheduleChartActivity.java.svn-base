package ee.smkv.calc.loan;

import android.app.Activity;
import android.util.Log;
import android.util.TypedValue;
import ee.smkv.calc.loan.chart.LoanChart;
import ee.smkv.calc.loan.export.HtmlScheduleCreator;
import ee.smkv.calc.loan.model.Loan;

import java.math.BigDecimal;
import java.util.Arrays;


/**
 * @author Andrei Samkov
 */
public class ScheduleChartActivity extends AbstractScheduleActivity {
    @Override
    protected Activity getActivity() {
        return this;
    }


    protected String createHtml(Loan loan) {
        HtmlScheduleCreator creator = new HtmlScheduleCreator(loan, getResources());
        int width = webview.getWidth() ;
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><style>")
                .append(getStyle())
                .append("</style><script>")
                .append(LoanChart.getScript())
                .append("</script><meta name=\"viewport\" content=\"width="+ width +"\" /></head><body>");

        try {

          creator.appendHtmlChart(sb, width- 10, 200);
            creator.appendHtmlButtons(sb);
        } catch (Exception e) {
            Log.v(ScheduleTableActivity.class.getSimpleName(), e.getMessage(), e);
        }


        return sb.append("</body></html>").toString();
    }



    public float getTotalInterestPercent() {
        return Math.round(getLoan().getTotalInterests().floatValue() * 100 / getLoan().getTotalAmount().floatValue());
    }

    protected Loan getLoan() {
        return ScheduleTabActivity.loan;
    }


    public String getInterestPointsData() {
        float[] points = LoanChart.getPoints(getLoan(), LoanChart.INTERESTS);
        return Arrays.toString(points);
    }

    public String getCommissionPointsData() {
        if (hasCommission()) {
            float[] points = LoanChart.getPoints(getLoan(), LoanChart.COMMISSION);
            return Arrays.toString(points);
        } else {
            return "[0]";
        }
    }

    public String getPrincipalPointsData() {
        float[] points = LoanChart.getPoints(getLoan(), LoanChart.PRINCIPAL);
        return Arrays.toString(points);
    }

    public String getPaymentPointsData() {
        float[] points = LoanChart.getPoints(getLoan(), LoanChart.PAYMENT);
        return Arrays.toString(points);
    }

    public String getXLabels() {
//        float[] points = LoanChart.getPoints(loan, LoanChart.LABEL);
//        String[] labels = new String[points.length];
//        for (int i = 0; i < points.length; i++) {
//            labels[i] = "" + Float.valueOf(points[i]).intValue();
//        }
//        return Arrays.toString(labels);
        return "[]";
    }

    public String getLegend() {
        return "[\"" +
                getResources().getString(R.string.paymentPrincipal) + "\",\"" +
                getResources().getString(R.string.paymentInterest) + "\",\"" +
                getResources().getString(R.string.paymentTotal) +
                (hasCommission() ? "\",\"" + getResources().getString(R.string.paymentCommission): "")
                + "\"]";
    }

    public String getPieTitle() {
        return "";
    }

    public String getPieLabels(){
        return "[\"" +
              getResources().getString(R.string.amount)  + "\",\""
              + getResources().getString(R.string.paymentInterest) +
              (hasCommission() ? "\",\"" +  getResources().getString(R.string.paymentCommission) : "")
              + "\"]";

    }

    private boolean hasCommission() {
        return getLoan().getCommissionsTotal() != null && getLoan().getCommissionsTotal().compareTo(BigDecimal.ZERO) != 0;
    }

    public String getPieValues(){
        return "[" + getLoan().getAmount().floatValue() + ","
               + getLoan().getTotalInterests().floatValue() +
               ( hasCommission() ? "," +  getLoan().getCommissionsTotal().floatValue() : "") + "]";
    }

}
