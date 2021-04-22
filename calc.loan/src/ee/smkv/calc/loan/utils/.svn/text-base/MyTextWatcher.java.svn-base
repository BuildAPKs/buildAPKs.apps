package ee.smkv.calc.loan.utils;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class MyTextWatcher implements TextWatcher {
    CharSequence value;

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        value = charSequence;
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //do nothing
    }

    public void afterTextChanged(Editable editable) {
        if( value == null || !value.toString().equals(editable.toString())){
            onChange(editable);
        }
    }

    public abstract void onChange(Editable editable);

}
