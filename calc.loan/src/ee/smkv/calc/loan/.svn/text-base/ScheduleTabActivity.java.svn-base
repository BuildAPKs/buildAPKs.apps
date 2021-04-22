package ee.smkv.calc.loan;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import ee.smkv.calc.loan.model.Loan;

public class ScheduleTabActivity extends TabActivity {
    public static Loan loan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        TabHost.TabSpec tableTabSpec = getTabHost()
                .newTabSpec("table")
                .setIndicator(getResources().getString(R.string.tabSchedule)
                        , getResources().getDrawable(R.drawable.ic_tab_table))
                .setContent(new Intent(this, ScheduleTableActivity.class));

        TabHost.TabSpec chartTabSpec = getTabHost()
                .newTabSpec("chart")
                .setIndicator(getResources().getString(R.string.tabChart), getResources().getDrawable(R.drawable.ic_tab_chart))
                .setContent(new Intent(this, ScheduleChartActivity.class));

        getTabHost().addTab(tableTabSpec);
        getTabHost().addTab(chartTabSpec);
    }
}
