package net.app.civilix;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class InflowTableAdapter extends ArrayAdapter<InflowTable> {
    Context mCtx;
    int listLayoutRes;
    List<InflowTable> inflowList;
    SQLiteDatabase mDatabase;
    AppCompatActivity aca;
    Button Edit,Delete,btnDatePicker,btnTimePicker;
    String name;

    public InflowTableAdapter(Context mCtx, int listLayoutRes, List<InflowTable> inflowList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, inflowList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.inflowList = inflowList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        final InflowTable inflowtable = inflowList.get(position);

        TextView textViewdatetime = view.findViewById(R.id.InflowDate);
        TextView textViewinflow = view.findViewById(R.id.InAmount);
        TextView textViewType = view.findViewById(R.id.InflowType);

        textViewdatetime.setText(inflowtable.getdatetime());
        textViewType.setText(inflowtable.gettype());
        textViewinflow.setText(String.valueOf(inflowtable.getinflow()));

        Button Delete=(Button) view.findViewById(R.id.buttonDeleteInflow);
        Cursor cursorProjects = mDatabase.rawQuery("SELECT * FROM projects WHERE id=" + ProjectActions.id, null);
        cursorProjects.moveToFirst();
        name = cursorProjects.getString(1);
        cursorProjects.close();



        Button buttonEdit = view.findViewById(R.id.buttonEditInflow);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInflow(inflowtable);
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
                        String sql = "DELETE FROM "+name+"_inflow"+" WHERE id = ?";
                        mDatabase.execSQL(sql,new Integer[]{inflowtable.getid()});
                        reloadInflowsFromDatabase();
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

    private void updateInflow(final InflowTable inflowtable) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dialog_update_inflow, null);
        builder.setView(view);


        final EditText editTextDate = view.findViewById(R.id.editTextDate);
        final EditText editInAmount = view.findViewById(R.id.editInAmount);
        final Spinner spinnerinType = view.findViewById(R.id.spinnerinType);

        editTextDate.setText(inflowtable.getdatetime());
        editInAmount.setText(String.valueOf(inflowtable.getinflow()));

        final AlertDialog dialog = builder.create();
        dialog.show();

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



        view.findViewById(R.id.UIB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Date = calculateDate(view.getRootView());
                String inflow = editInAmount.getText().toString().trim();
                String type = spinnerinType.getSelectedItem().toString();


                if (inflow.isEmpty()) {
                    editInAmount.setError("Estimate can't be blank");
                    editInAmount.requestFocus();
                    return;
                }

                String sql = "UPDATE "+name+"_inflow"+"\n"+
                        "SET datetime = ?, \n" +
                        "inflow = ?, \n" +
                        "type = ? \n" +
                        "WHERE id = ?;\n";

                mDatabase.execSQL(sql, new String[]{Date, inflow, type, String.valueOf(inflowtable.getid())});
                Toast.makeText(mCtx, "Entry Updated", Toast.LENGTH_SHORT).show();
                reloadInflowsFromDatabase();

                dialog.dismiss();
            }
        });
    }

    private String calculateDate(View view) {
        String Time=OnCheckboxClicked(view.findViewById(R.id.indatecheckbox),view);
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
        notifyDataSetChanged();
    }



}
