package com.iiui.craapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.iiui.craapp.R;

public class MainActivity extends AppCompatActivity {

    Button btnCrimeReport, btnTakeInterview, btnReportEvidence, btnReportSuspect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.cra_launcher);
*/
        btnCrimeReport = findViewById(R.id.btnCreateCrimeReport);
        btnTakeInterview = findViewById(R.id.btnTakeInterview);
        btnReportEvidence = findViewById(R.id.btnCreateEvidenceReport);
        btnReportSuspect = findViewById(R.id.btnReportSuspect);

    }

    public void buttonClickHandler(View view) {
        switch (view.getId()) {
            case R.id.btnCreateCrimeReport:
                Toast.makeText(getApplicationContext(),"Create Report", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(),ReportActivity.class);
                startActivity(i);

                break;
            case R.id.btnTakeInterview:
                Toast.makeText(getApplicationContext(),"Create Interview", Toast.LENGTH_SHORT).show();

                Intent j = new Intent(getApplicationContext(),InterviewActivity.class);
                startActivity(j);


                break;
            case R.id.btnCreateEvidenceReport:
                Toast.makeText(getApplicationContext(),"Create Evidence", Toast.LENGTH_SHORT).show();

                Intent k = new Intent(getApplicationContext(),EvidenceActivity.class);
                startActivity(k);



                break;
            case R.id.btnReportSuspect:
                Toast.makeText(getApplicationContext(),"Create Suspect", Toast.LENGTH_SHORT).show();

                Intent l = new Intent(getApplicationContext(),ReportSuspectActivity.class);
                startActivity(l);

                break;

        }

    }

}


