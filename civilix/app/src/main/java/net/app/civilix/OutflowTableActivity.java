package net.app.civilix;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OutflowTableActivity extends AppCompatActivity {
    List<OutflowTable> outflowList;
    SQLiteDatabase mDatabase;
    ListView listViewOutflows;
    OutflowTableAdapter Iadapter;
    String Time;
    String Amount;
    int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outflow);

        listViewOutflows = (ListView) findViewById(R.id.listViewOutflows);
        outflowList = new ArrayList<>();

        //opening the database
        mDatabase = openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);

        //this method will display the employees in the list
        showOutflowsFromDatabase();
        FloatingActionButton addin=findViewById(R.id.AddOutflow);
        addin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                addOutflow();
            }

        });


    }

    public void addOutflow() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_addout, null);



        builder.setView(view);
        final EditText editOutDate = (EditText) view.findViewById(R.id.editOutDate);
        final EditText editOutAmount = (EditText) view.findViewById(R.id.editOutAmount);
        final Spinner spinneroutType = view.findViewById(R.id.spinneroutType);

        editOutDate.setText("");
        editOutAmount.setText("");
        final AlertDialog dialog = builder.create();

        dialog.show();

        Button btnDatePicker, btnTimePicker;

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






        view.findViewById(R.id.AOB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editOutAmount = (EditText) view.getRootView().findViewById(R.id.editOutAmount);
                final Spinner spinneroutType = view.getRootView().findViewById(R.id.spinneroutType);




                Time = calculateTime(view.getRootView());
                Amount = editOutAmount.getText().toString().trim();
                String outType = spinneroutType.getSelectedItem().toString();

                if (String.valueOf(Amount).isEmpty()) {
                    editOutAmount.setError("Please enter Amount");
                    editOutAmount.requestFocus();
                    return;
                }

                Cursor cursorProjects = mDatabase.rawQuery("SELECT * FROM projects WHERE id=" + ProjectActions.id, null);
                cursorProjects.moveToFirst();
                String name = cursorProjects.getString(1);
                String sql = "INSERT INTO " + name + "_outflow" + "(datetime,outflow,type) " + "VALUES \n" + "(" + "'" + Time + "'" + "," + "'" + Amount + "'" + "," + "'" + outType + "'" + ")";
                mDatabase.execSQL(sql);
                cursorProjects.close();
                reloadOutflowsFromDatabase();
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


    }


    private String calculateTime(View view) {
        Time=onCheckboxClicked(view.findViewById(R.id.outdatecheckbox),view);
        return Time;
    }

    public static String setTime() {
        String Time;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Time = sdf.format(cal.getTime());
        return Time;
    }


    public String onCheckboxClicked(View view,View v){
        String time=null;
        EditText editOutDate = (EditText) v.findViewById(R.id.editOutDate);
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.outdatecheckbox:
                if(checked){
                    time=OutflowTableActivity.setTime();
                }
                else{
                    time= editOutDate.getText().toString();
                }
                break;
            default:
                break;
        }
        return time;
    }
    private void showOutflowsFromDatabase() {

        Cursor cursorProjects = mDatabase.rawQuery("SELECT * FROM projects WHERE id="+ProjectActions.id, null);
        cursorProjects.moveToFirst();
        String name= String.valueOf(cursorProjects.getString(1));
        Cursor cursorOutflows=mDatabase.rawQuery("SELECT * FROM "+name+"_outflow", null);

        if (cursorOutflows.moveToFirst()) {

            do {
                //pushing each record in the employee list
                outflowList.add(new OutflowTable(
                        cursorOutflows.getInt(0),
                        cursorOutflows.getString(1),
                        cursorOutflows.getInt(2),
                        cursorOutflows.getString(3)
                ));
            } while (cursorOutflows.moveToNext());
        }
        //closing the cursor
        cursorOutflows.close();
        cursorProjects.close();

        //creating the adapter object
        Iadapter = new OutflowTableAdapter(this, R.layout.list_layout_outflow, outflowList, mDatabase);

        //adding the adapter to listview
        listViewOutflows.setAdapter(Iadapter);
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
    }

}
