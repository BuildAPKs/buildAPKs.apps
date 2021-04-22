package ee.smkv.calc.loan.export;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import ee.smkv.calc.loan.model.Loan;
import ee.smkv.calc.loan.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author samko
 */
public class Exporter {
    public static void sendToEmail(Loan loan, Resources resources, Activity activity) throws FileCreationException {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/text");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name) + " - export");

        StringBuilder sb = new StringBuilder();
        new TextScheduleCreator(loan, resources).appendTextScheduleTable(sb);
        emailIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());

        CSVScheduleCreator csvScheduleCreator = new CSVScheduleCreator(loan, resources);
        File file = createTmpFile(csvScheduleCreator, activity);
        saveToFile(file, csvScheduleCreator);

        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        emailIntent.setType("text/csv");
        activity.startActivity(Intent.createChooser(emailIntent, resources.getString(R.string.sendEmail)));
    }

    private static File createTmpFile(CSVScheduleCreator csvScheduleCreator, Activity activity) throws FileCreationException {
        String fileName = csvScheduleCreator.getFileName();
        File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(externalStorageDirectory, fileName);
        file.deleteOnExit();
        return file;
    }

    public static File exportToCSVFile(Loan loan, Resources resources, Activity activity) throws FileCreationException {
        CSVScheduleCreator csvScheduleCreator = new CSVScheduleCreator(loan, resources);
        csvScheduleCreator.checkExternalStorageState();
        String fileName = csvScheduleCreator.getFileName();

        File externalStorageDirectory = Environment.getExternalStoragePublicDirectory("");
        File file = new File(externalStorageDirectory, fileName);
        saveToFile(file, csvScheduleCreator);
        return file;
    }

    private static void saveToFile(File file, CSVScheduleCreator csvScheduleCreator) throws FileCreationException {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), CSVScheduleCreator.ENCODING);
            csvScheduleCreator.createSchedule(writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new FileCreationException("Creating file fail: " + e.getMessage(), e);
        }
    }
}
