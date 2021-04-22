package ee.smkv.calc.loan.utils;

import android.widget.EditText;
import ee.smkv.calc.loan.calculators.Calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ViewUtil {
    private static final DecimalFormat DECIMAL_FORMAT;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.UK);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(' ');
        DECIMAL_FORMAT = new DecimalFormat("###,##0.00", symbols);

    }

    private ViewUtil() {
    }

    public static int getIntegerValue(EditText editText) throws EditTextNumberFormatException {
        return getBigDecimalValue(editText).intValue();
    }

    public static BigDecimal getBigDecimalValue(EditText editText) throws EditTextNumberFormatException {
        String value = editText.getText().toString().replace(',', '.');
        if (value != null && value.trim().length() > 0) {
            try {
                return new BigDecimal(value);
            } catch (Exception e) {
                throw new EditTextNumberFormatException(editText, e);
            }
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static String formatBigDecimal(BigDecimal number) {
        return DECIMAL_FORMAT.format(number != null ? number.setScale(2 , Calculator.MODE) : BigDecimal.ZERO);
    }
}
