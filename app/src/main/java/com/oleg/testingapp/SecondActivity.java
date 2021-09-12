package com.oleg.testingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import io.branch.referral.Branch;

public class SecondActivity extends AppCompatActivity {

    String latd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        TextView textLATD = findViewById(R.id.textView);
        Intent intent=getIntent();
        latd = intent.getStringExtra("latd");
        textLATD.setText(latd);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

      /*  TextView textLATD = findViewById(R.id.textView);
        JSONObject latd = Branch.getInstance().getLatestReferringParams();

        Log.i("BranchSDK", String.valueOf(latd));

        textLATD.setText((CharSequence) latd);*/
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        //Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }


}
