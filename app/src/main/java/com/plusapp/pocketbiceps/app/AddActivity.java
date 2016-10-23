package com.plusapp.pocketbiceps.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;

public class AddActivity extends AppCompatActivity {

    EditText etTitle;
    EditText etDescription;
    Button btnSave;

    String dbTitle;
    String dbDescription;
    String dbLongitude;
    String dbLatitude;
    int dbCounter = 0;
    long dbvCurrTime;


    MarkerDataSource data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        data = new MarkerDataSource(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.dbvCurrTime = extras.getLong("currTime");
            //The key argument here must match that used in the other activity
        }

        etTitle = (EditText) findViewById(R.id.editTitle);
        etDescription = (EditText) findViewById(R.id.editDescription);
        btnSave = (Button) findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(etTitle.getText().toString().trim())) {
                    etTitle.setError("Gib einen Titel ein");
                    return;
                }

                if (TextUtils.isEmpty(etDescription.getText().toString().trim())) {
                    etTitle.setError("Gib eine Beschreibung ein");
                    return;
                }

                dbTitle = etTitle.getText().toString();
                dbDescription = etDescription.getText().toString();
                dbCounter = 1;
                data.open();
                data.addMarker(new MyMarkerObj(dbTitle,dbDescription,"position",dbvCurrTime,dbCounter));
                data.close();

                Intent intent = new Intent(AddActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });


    }
}
