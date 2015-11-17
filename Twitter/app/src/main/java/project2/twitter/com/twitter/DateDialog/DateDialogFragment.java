package project2.twitter.com.twitter.DateDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import project2.twitter.com.twitter.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by alexwong on 11/1/15.
 */
public class DateDialogFragment extends DialogFragment
{
    private static final String tag = "datedialog:";
    public static final String EXTRA_DATE = "sqlite.game.datedialogfragment";
    private static final String ARG_DATE = "DATE";
    private DatePicker mDatepicker;

    public static DateDialogFragment newInstance(Date date)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DateDialogFragment dialog = new DateDialogFragment();
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.datedialog, null);
        mDatepicker = (DatePicker)view.findViewById(R.id.datepicker);

        Bundle args = getArguments();
        Date date = (Date) args.getSerializable(ARG_DATE);
        if(date == null)
        {
            date = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDatepicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.Caledar_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatepicker.getYear();
                        int month = mDatepicker.getMonth();
                        int day = mDatepicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Date date) {
        Fragment target = getTargetFragment();
        if(target != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATE, date);
            Log.i(tag, "Date set: " + date);
            target.onActivityResult(getTargetRequestCode(), resultCode, intent);
        }
    }

}
