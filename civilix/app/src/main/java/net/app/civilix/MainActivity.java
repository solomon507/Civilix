package net.app.civilix;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String DATABASE_NAME = "projectdatabase";

    TextView textViewViewProjects;
    EditText editTextName, editTextEstimate;
    Spinner spinnerType;

    SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewViewProjects = (TextView) findViewById(R.id.textViewViewProjects);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEstimate = (EditText) findViewById(R.id.editTextEstimate);
        spinnerType = (Spinner) findViewById(R.id.spinnerType);

        findViewById(R.id.buttonAddProject).setOnClickListener(this);
        textViewViewProjects.setOnClickListener(this);

        //creating a database
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        createProjectTable();
    }


    //this method will create the InflowTable
    //as we are going to call this method everytime we will launch the application
    //I have added IF NOT EXISTS to the SQL
    //so it will only create the InflowTable when the InflowTable is not already created
    private void createProjectTable() {
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS projects (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT projects_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    name varchar(200) NOT NULL,\n" +
                        "    type varchar(200) NOT NULL,\n" +
                        "    joiningdate datetime NOT NULL,\n" +
                        "    estimate double NOT NULL\n" +
                        ");"
        );
    }

    //this method will validate the name and salary
    //dept does not need validation as it is a spinner and it cannot be empty
    private boolean inputsAreCorrect(String name, String Estimate) {
        if (name.isEmpty()) {
            editTextName.setError("Please enter a name");
            editTextName.requestFocus();
            return false;
        }

        if (Estimate.isEmpty() || Integer.parseInt(Estimate) <= 0) {
            editTextEstimate.setError("Please enter Estimate");
            editTextEstimate.requestFocus();
            return false;
        }
        return true;
    }

    //In this method we will do the create operation
    private void addProject() {

        String name = editTextName.getText().toString().trim();
        String estimate = editTextEstimate.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();

        //getting the current time for joining date
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String joiningDate = sdf.format(cal.getTime());

        //validating the inptus
        if (inputsAreCorrect(name, estimate)) {

            String insertSQL = "INSERT INTO projects \n" +
                    "(name, type, joiningdate, estimate)\n" +
                    "VALUES \n" +
                    "(?, ?, ?, ?);";

            //using the same method execsql for inserting values
            //this time it has two parameters
            //first is the sql string and second is the parameters that is to be binded with the query
            mDatabase.execSQL(insertSQL, new String[]{name, type, joiningDate, estimate});
            mDatabase.execSQL("CREATE TABLE "+name+"_inflow"+" (id INTEGER NOT NULL CONSTRAINT projects_pk PRIMARY KEY AUTOINCREMENT,datetime TEXT,inflow INTEGER,type TEXT)");
            mDatabase.execSQL("CREATE TABLE "+name+"_outflow"+" (id INTEGER NOT NULL CONSTRAINT projects_pk PRIMARY KEY AUTOINCREMENT,datetime TEXT,outflow INTEGER,type TEXT)");
            Toast.makeText(this, "Project Added Successfully", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddProject:

                addProject();

                break;
            case R.id.textViewViewProjects:

                startActivity(new Intent(this, ProjectActivity.class));

                break;
        }
    }
}
