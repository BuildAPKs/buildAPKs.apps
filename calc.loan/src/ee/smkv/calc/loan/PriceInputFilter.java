package ee.smkv.calc.loan;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;


public class PriceInputFilter implements InputFilter {

  public CharSequence filter(CharSequence source, int start, int end,  Spanned dest, int dstart, int dend) {

    String checkedText = dest.toString() + source.toString();


    try {

      new BigDecimal(checkedText.replace(',','.'));
    }
    catch (Exception e) {
      return "";
    }


    return null;
  }

}