package ee.smkv.calc.loan.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * @author Andrei Samkov
 */
public class ErrorDialogWrapper implements DialogInterface.OnClickListener {

    private AlertDialog dialog;

    public ErrorDialogWrapper(Activity activity, Exception e) {
        this(activity, e.getMessage());
    }

    public ErrorDialogWrapper(Activity activity, String e) {
        dialog = new AlertDialog.Builder(activity)
                .setMessage(e)
                .setPositiveButton("OK", this)
                .create();
    }

    public void show() {
        dialog.show();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        dialog.cancel();
    }
}
