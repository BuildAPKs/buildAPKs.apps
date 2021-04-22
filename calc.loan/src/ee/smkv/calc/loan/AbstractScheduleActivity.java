package ee.smkv.calc.loan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import ee.smkv.calc.loan.export.Exporter;
import ee.smkv.calc.loan.export.FileCreationException;
import ee.smkv.calc.loan.model.Loan;
import ee.smkv.calc.loan.utils.ErrorDialogWrapper;
import ee.smkv.calc.loan.utils.OkDialogWrapper;

import java.io.File;


public abstract class AbstractScheduleActivity extends Activity {
  protected static final String UTF = "UTF-8";
  WebView webview = null;
  private AsyncTask generatingTask;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    webview = new WebView(getActivity());
    WebSettings webSettings = webview.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setSupportZoom(true);
    webSettings.setUseWideViewPort(true);
    webSettings.setLoadWithOverviewMode(true);

    webview.addJavascriptInterface(getActivity(), "schedule");

    setContentView(webview);
    generatingTask = new AsyncTask() {
      private ProgressDialog dialog = null;

      @Override
      protected void onPreExecute() {
        this.dialog = new ProgressDialog(getActivity());
        this.dialog.setMessage(getResources().getString(R.string.scheduleLoading));
        this.dialog.show();
      }

      @Override
      protected Object doInBackground(Object... objects) {
        final Loan loan = getLoan();
        webview.loadDataWithBaseURL(null, createHtml(loan), "text/html", UTF, "about:blank");
        return null;
      }

      @Override
      protected void onPostExecute(final Object unused) {
        if (this.dialog != null && this.dialog.isShowing()) {
          this.dialog.dismiss();
          this.dialog = null;
        }
      }

    };
    generatingTask.execute();
  }

  @Override
  protected void onStop() {
    if (generatingTask != null) {
      generatingTask.cancel(true);
    }
    super.onStop();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.schedulemenu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    try {
      switch (item.getItemId()) {
        case R.id.exportEmailMenu:
          Exporter.sendToEmail(getLoan(), getResources(), getActivity());
          break;
        case R.id.exportExcelMenu:
          File file = Exporter.exportToCSVFile(getLoan(), getResources(), getActivity());
          new OkDialogWrapper(this, getResources().getString(R.string.fileCreated) + ' ' + file.getAbsolutePath()).show();
          break;
        case R.id.donateMenu:
          Uri uri = Uri.parse("http://samkov.pri.ee/simple-loan-calculator/");
          startActivity(new Intent(Intent.ACTION_VIEW, uri));
          break;

      }
    }
    catch (FileCreationException e) {
      new ErrorDialogWrapper(this, e.getMessage()).show();
    }
    return super.onOptionsItemSelected(item);
  }

  protected String getStyle() {
    return "body{background:#FFF;color:#111; font-family:Verdana;}table{border-spacing:0px 0px; font-size:11px; width:100%}td{padding:5px;white-space:nowrap; }th{padding:5px;text-align:left;white-space:nowrap;}.odd{background:#EEE;}.odd th{border-bottom: 1px solid black;} .odd td{border-bottom: 1px solid #CCC;}.even{background:#FAFAFA;}#closeBtn{width:100%;padding:10px} table{border-radius: 5px;}";
  }

  protected abstract Activity getActivity();

  protected abstract String createHtml(Loan loan);

  protected abstract Loan getLoan();
}
