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

public class InflowTableActivity extends AppCompatActivity {
    List<InflowTable> inflowList;
    SQLiteDatabase mDatabase;
    ListView listViewInflows;
    InflowTableAdapter Iadapter;
    String Time;
    String Amount;
    int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inflow);

        listViewInflows = (ListView) findViewById(R.id.listViewInflows);
        inflowList = new ArrayList<>();

        //opening the database
        mDatabase = openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);

        //this method will display the employees in the list
        showInflowsFromDatabase();
        FloatingActionButton addin=findViewById(R.id.AddInflow);
        addin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                addInflow();
                reloadInflowsFromDatabase();
            }

        });



    }

    public void addInflow() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_addin, null);



        builder.setView(view);
        final EditText editTextDate = (EditText) view.findViewById(R.id.editTextDate);
        final EditText editInAmount = (EditText) view.findViewById(R.id.editInAmount);
        final Spinner spinneroutType = view.findViewById(R.id.spinneroutType);

        editTextDate.setText("");
        editInAmount.setText("");
        final AlertDialog dialog = builder.create();

        dialog.show();

        Button btnDatePicker, btnTimePicker;

        btnDatePicker=(Button)view.findViewById(R.id.Sdate);
        btnTimePicker=(Button)view.findViewById(R.id.Stime);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editTextDate=(EditText)view.getRootView().findViewById(R.id.editTextDate);
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

                                editTextDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editTextDate=(EditText)view.getRootView().findViewById(R.id.editTextDate);
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
                                String temp=editTextDate.getText().toString();


                                temp+=" " +hourOfDay + ":" + minute ;
                                editTextDate.setText(temp);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });






        view.findViewById(R.id.AIB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editInAmount = (EditText) view.getRootView().findViewById(R.id.editInAmount);
                final Spinner spinnerinType = view.getRootView().findViewById(R.id.spinnerinType);




                Time = calculateTime(view.getRootView());
                Amount = editInAmount.getText().toString().trim();
                String inType = spinnerinType.getSelectedItem().toString();

                if (String.valueOf(Amount).isEmpty()) {
                    editInAmount.setError("Please enter Amount");
                    editInAmount.requestFocus();
                    return;
                }

                Cursor cursorProjects = mDatabase.rawQuery("SELECT * FROM projects WHERE id=" + ProjectActions.id, null);
                cursorProjects.moveToFirst();
                String name = cursorProjects.getString(1);
                String sql = "INSERT INTO " + name + "_inflow" + "(datetime,inflow,type) " + "VALUES \n" + "(" + "'" + Time + "'" + "," + "'" + Amount + "'" + "," + "'" + inType + "'" + ")";
                mDatabase.execSQL(sql);
                cursorProjects.close();
                reloadInflowsFromDatabase();
                dialog.dismiss();

            }
        });



    }


    private String calculateTime(View view) {
        Time=OnCheckboxClicked(view.findViewById(R.id.indatecheckbox),view);
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
        EditText editTextDate = (EditText) v.findViewById(R.id.editTextDate);
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.indatecheckbox:
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
    private void showInflowsFromDatabase() {

        Cursor cursorProjects = mDatabase.rawQuery("SELECT * FROM projects WHERE id="+ProjectActions.id, null);
        cursorProjects.moveToFirst();
        String name= String.valueOf(cursorProjects.getString(1));
        Cursor cursorInflows=mDatabase.rawQuery("SELECT * FROM "+name+"_inflow", null);

        if (cursorInflows.moveToFirst()) {

            do {
                //pushing each record in the employee list
                inflowList.add(new InflowTable(
                        cursorInflows.getInt(0),
                        cursorInflows.getString(1),
                        cursorInflows.getInt(2),
                        cursorInflows.getString(3)
                ));
            } while (cursorInflows.moveToNext());
        }
        //closing the cursor
        cursorInflows.close();
        cursorProjects.close();

        //creating the adapter object
        Iadapter = new InflowTableAdapter(this, R.layout.list_layout_inflow, inflowList, mDatabase);

        //adding the adapter to listview
        listViewInflows.setAdapter(Iadapter);
    }
    public void reloadInflowsFromDatabase() {
        Cursor cursorProjects = mDatabase.rawQuery("SELECT * FROM projects WHERE id="+ProjectActions.id, null);
        cursorProjects.moveToFirst();
        String name= String.valueOf(cursorProjects.getString(1));
        Cursor cursorInflows=mDatabase.rawQuery("SELECT * FROM "+name+"_inflow", null);

        if (cursorInflows.moveToFirst()) {
            inflowList.clear();
            do {
                inflowList.add(new InflowTable(
                        cursorInflows.getInt(0),
                        cursorInflows.getString(1),
                        cursorInflows.getInt(2),
                        cursorInflows.getString(3)
                ));
            } while (cursorInflows.moveToNext());
        }
        cursorProjects.close();
        cursorInflows.close();
    }


}
