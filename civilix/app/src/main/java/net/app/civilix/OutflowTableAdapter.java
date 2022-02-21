package net.app.civilix;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class OutflowTableAdapter extends ArrayAdapter<OutflowTable> {
    Context mCtx;
    int listLayoutRes;
    List<OutflowTable> outflowList;
    SQLiteDatabase mDatabase;
    String name;
    Button btnDatePicker,btnTimePicker;

    public OutflowTableAdapter(Context mCtx, int listLayoutRes, List<OutflowTable> outflowList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, outflowList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.outflowList = outflowList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        final OutflowTable outflowtable = outflowList.get(position);


        TextView textViewdatetime = view.findViewById(R.id.OutflowDate);
        TextView textViewoutflow = view.findViewById(R.id.OutAmount);
        TextView textViewType = view.findViewById(R.id.OutflowType);
        Button Delete=(Button) view.findViewById(R.id.buttonDeleteOutflow);

        textViewdatetime.setText(outflowtable.getdatetime());
        textViewType.setText(outflowtable.gettype());
        textViewoutflow.setText(String.valueOf(outflowtable.getoutflow()));

        Cursor cursorProjects = mDatabase.rawQuery("SELECT * FROM projects WHERE id=" + ProjectActions.id, null);
        cursorProjects.moveToFirst();
        name = cursorProjects.getString(1);
        cursorProjects.close();

        Button buttonEdit = view.findViewById(R.id.buttonEditOutflow);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOutflow(outflowtable);
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM "+name+"_outflow"+" WHERE id = ?";
                        mDatabase.execSQL(sql,new Integer[]{outflowtable.getid()});
                        reloadOutflowsFromDatabase();
                    }


            });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
        }
        });
        return view;
    }


    private void updateOutflow(final OutflowTable outflowtable) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dialog_update_outflow, null);
        builder.setView(view);


        final EditText editOutDate = view.findViewById(R.id.editOutDate);
        final EditText editOutAmount = view.findViewById(R.id.editOutAmount);
        final Spinner spinneroutType = view.findViewById(R.id.spinneroutType);

        editOutDate.setText(outflowtable.getdatetime());
        editOutAmount.setText(String.valueOf(outflowtable.getoutflow()));

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnDatePicker=(Button)view.findViewById(R.id.Sdate);
        btnTimePicker=(Button)view.findViewById(R.id.Stime);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editOutDate=(EditText)view.getRootView().findViewById(R.id.editOutDate);
                int mYear, mMonth, mDay;
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getRootView().getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                editOutDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editOutDate=(EditText)view.getRootView().findViewById(R.id.editOutDate);
                int mHour, mMinute;
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getRootView().getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String temp=editOutDate.getText().toString();


                                temp+=" " +hourOfDay + ":" + minute ;
                                editOutDate.setText(temp);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });



        view.findViewById(R.id.UOB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Date = calculateDate(view.getRootView());
                String outflow = editOutAmount.getText().toString().trim();
                String type = spinneroutType.getSelectedItem().toString();


                if (outflow.isEmpty()) {
                    editOutAmount.setError("Estimate can't be blank");
                    editOutAmount.requestFocus();
                    return;
                }

                String sql = "UPDATE "+name+"_outflow"+"\n"+
                        "SET datetime = ?, \n" +
                        "outflow = ?, \n" +
                        "type = ? \n" +
                        "WHERE id = ?;\n";

                mDatabase.execSQL(sql, new String[]{Date, outflow, type, String.valueOf(outflowtable.getid())});
                Toast.makeText(mCtx, "Entry Updated", Toast.LENGTH_SHORT).show();
                reloadOutflowsFromDatabase();

                dialog.dismiss();
            }
        });
    }

    private String calculateDate(View view) {
        String Time=OnCheckboxClicked(view.findViewById(R.id.outdatecheckbox),view);
        return Time;
    }

    public static String setTime() {
        String Time;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Time = sdf.format(cal.getTime());
        return Time;
    }

    public String OnCheckboxClicked(View view,View v){
        String time=null;
        EditText editTextDate = (EditText) v.findViewById(R.id.editOutDate);
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.outdatecheckbox:
                if(checked){
                    time=InflowTableActivity.setTime();
                }
                else{
                    time= editTextDate.getText().toString();
                }
                break;
            default:
                break;
        }
        return time;
    }




    private void reloadOutflowsFromDatabase() {
        Cursor cursorProjects = mDatabase.rawQuery("SELECT * FROM projects WHERE id="+ProjectActions.id, null);
        cursorProjects.moveToFirst();
        String name= String.valueOf(cursorProjects.getString(1));
        Cursor cursorOutflows=mDatabase.rawQuery("SELECT * FROM "+name+"_outflow", null);

        if (cursorOutflows.moveToFirst()) {
            outflowList.clear();
            do {
                outflowList.add(new OutflowTable(
                        cursorOutflows.getInt(0),
                        cursorOutflows.getString(1),
                        cursorOutflows.getInt(2),
                        cursorOutflows.getString(3)
                ));
            } while (cursorOutflows.moveToNext());
        }
        cursorProjects.close();
        cursorOutflows.close();
        notifyDataSetChanged();
    }
}
