package com.example.cs408_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SendReportActivity extends AppCompatActivity {
    Button report_btn;
    EditText titleText;
    EditText descText;
    RadioGroup category;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);

        report_btn = findViewById(R.id.button_report);
        titleText = (EditText) findViewById(R.id.title_input);
        category = (RadioGroup) findViewById(R.id.category);
        descText = (EditText) findViewById(R.id.desc_input);

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("Latitude", 0);
        double lng = intent.getDoubleExtra("Longitude", 0);
        double rad = intent.getDoubleExtra("Radius", 0);

        Log.e("TAG", Double.toString(lat));
        Log.e("TAG", Double.toString(lng));
        Log.e("TAG", Double.toString(rad));

        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SendReportActivity.this);
                alert.setTitle("Report");
                alert.setMessage("Are you sure?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String title = titleText.getText().toString();
                        List<String> supplierNames = Arrays.asList("opt1", "opt2", "opt3", "opt4", "opt5");
                        int cat_id = category.getCheckedRadioButtonId();
                        RadioButton r = (RadioButton) category.findViewById(cat_id);
                        String cat_str = r.getText().toString();
                        String desc = descText.getText().toString();

                        // TODO: SEND REPORT
                        Toast.makeText(SendReportActivity.this, "title: "+title+"\ncategory: "+cat_str+"\n", Toast.LENGTH_SHORT).show();

                        dialogInterface.dismiss();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.show();
            }
        });
    }
}
