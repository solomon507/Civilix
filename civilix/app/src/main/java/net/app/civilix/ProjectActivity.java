package net.app.civilix;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends AppCompatActivity {

    List<Project> projectList;
    SQLiteDatabase mDatabase;
    ListView listViewProjects;
    ProjectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        listViewProjects = (ListView) findViewById(R.id.listViewProjects);
        projectList = new ArrayList<>();

        //opening the database
        mDatabase = openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);

        //this method will display the employees in the list
        showProjectsFromDatabase();
    }

    private void showProjectsFromDatabase() {
        //we used rawQuery(sql, selectionargs) for fetching all the employees
        Cursor cursorProjects = mDatabase.rawQuery("SELECT * FROM projects", null);

        //if the cursor has some data
        if (cursorProjects.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the employee list
                projectList.add(new Project(
                        cursorProjects.getInt(0),
                        cursorProjects.getString(1),
                        cursorProjects.getString(2),
                        cursorProjects.getString(3),
                        cursorProjects.getDouble(4)
                ));
            } while (cursorProjects.moveToNext());
        }
        //closing the cursor
        cursorProjects.close();

        //creating the adapter object
        adapter = new ProjectAdapter(this, R.layout.list_layout_project, projectList, mDatabase);

        //adding the adapter to listview
        listViewProjects.setAdapter(adapter);
    }

}
