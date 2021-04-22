package ee.smkv.calc.loan.chart;

import ee.smkv.calc.loan.model.Loan;
import ee.smkv.calc.loan.model.Payment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author samko
 */
public class LoanChart {
    public final static int INTERESTS = 0, PRINCIPAL = 1, PAYMENT = 2, COMMISSION = 3, LABEL = 4;

    public static String getScript() {
        StringBuilder sb = new StringBuilder();
        sb.append(new String(convertStreamToBytes(LoanChart.class.getResourceAsStream("RGraph.common.core.js"))));
        sb.append(new String(convertStreamToBytes(LoanChart.class.getResourceAsStream("RGraph.line.js"))));
        sb.append(new String(convertStreamToBytes(LoanChart.class.getResourceAsStream("RGraph.pie.js"))));
        sb.append(new String(convertStreamToBytes(LoanChart.class.getResourceAsStream("chart.js"))));
        return sb.toString();
    }

    public static float[] getPoints(Loan loan, int type) {
        int max = (type == LABEL) ? 2 : 20;
        int total = loan.getPeriod();
        boolean less = total < max;
        boolean even = less || total % max == 0;
        float step = less ? 1f : ((float) total / (float) max);

        float[] result = new float[less ? total : even ? max : max + 1];
        result[0] = getValue(loan, 0, type);
        for (int i = 1; i < result.length; i++) {
            int index = i * (int) step;
            float value = getValue(loan, index, type);
            if ( !even &&( index + 1) < total) {
               value = (getValue(loan, index + 1, type) + value) / 2;
            }
            result[i] = value;
        }
        return result;
    }

    private static float getValue(Loan loan, int index, int type) {
        Payment p = loan.getPayments().get(index);
        switch (type) {
            case INTERESTS:
                return p.getInterest().floatValue();
            case PRINCIPAL:
                return p.getPrincipal().floatValue();
            case PAYMENT:
                return p.getAmount().floatValue();
            case COMMISSION:
                return p.getCommission().floatValue();
            case LABEL:
                return p.getNr().floatValue();
        }
        return 0;
    }

    public static byte[] convertStreamToBytes(InputStream is) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (Exception e) {

        } finally {
            try {
                is.close();
            } catch (IOException e) {

            }
        }
        return out.toByteArray();
    }

}
