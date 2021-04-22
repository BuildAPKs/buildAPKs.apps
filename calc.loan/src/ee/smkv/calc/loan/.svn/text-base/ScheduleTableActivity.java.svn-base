package ee.smkv.calc.loan;

import android.app.Activity;
import android.util.Log;
import ee.smkv.calc.loan.export.HtmlScheduleCreator;
import ee.smkv.calc.loan.model.Loan;

/**
 * @author Andrei Samkov
 */
public class ScheduleTableActivity extends AbstractScheduleActivity {

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
                .append("</style>")
                .append("<meta name=\"viewport\" content=\"width="+ width +"\" /></head><body>");
        try {
            creator.appendHtmlScheduleTable(sb);
            creator.appendHtmlButtons(sb);
        } catch (Exception e) {
            Log.v(ScheduleTableActivity.class.getSimpleName(), e.getMessage(), e);
        }


        return sb.append("</body></html>").toString();
    }

    protected Loan getLoan() {
        return ScheduleTabActivity.loan;
    }
}
