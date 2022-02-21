package net.app.civilix;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


public class ProjectActions extends AppCompatActivity implements View.OnClickListener {
    View view;
    public static Integer id;
    public static String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_actions);

        Button inflow=(Button) findViewById(R.id.ButtonInflow);
        Button outflow=(Button)findViewById(R.id.ButtonOutflow);
        inflow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openInflow();
            }
        });

        outflow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openOutflow();
            }
        });
    }
    public void openInflow(){
        startActivity(new Intent(this, InflowTableActivity.class));
    }

    public void openOutflow(){
        startActivity(new Intent(this,OutflowTableActivity.class));
    }


    public void onCheckboxClicked(View view){
        EditText editTextDate = (EditText) view.findViewById(R.id.editTextDate);
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

    }

    public void OnCheckboxClicked(View view){
        EditText editTextDate = (EditText) view.findViewById(R.id.editTextDate);
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

    }


    @Override
    public void onClick(View view) {
        //
    }
}



