package ee.smkv.calc.loan.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * @author Andrei Samkov
 */
public class OkDialogWrapper implements  DialogInterface.OnClickListener{

  private AlertDialog dialog;

  public OkDialogWrapper(Activity activity, String text) {
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setMessage(text);
    builder.setPositiveButton("OK", this);
    dialog = builder.create();
  }

  public void show(){
    dialog.show();
  }

  public void onClick(DialogInterface dialogInterface, int i) {
     dialog.cancel();
  }
}
