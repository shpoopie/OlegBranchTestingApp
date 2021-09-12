package com.oleg.testingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Calendar;
import java.sql.ResultSet;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.ServerRequestGetLATD;
import io.branch.referral.util.BRANCH_STANDARD_EVENT;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.CurrencyType;
import io.branch.referral.util.LinkProperties;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

   // public static boolean trackingAllowed;

    public void completePurchase (View view){

        new BranchEvent(BRANCH_STANDARD_EVENT.PURCHASE)
                .setAffiliation("test_affiliation")
                .setCustomerEventAlias("my_custom_alias")
                .setCoupon("Coupon Code")
                .setCurrency(CurrencyType.USD)
                .setDescription("Customer added item to cart")
                .setShipping(0.0)
                .setTax(9.75)
                .setRevenue(1.5)
                .setSearchQuery("Test Search query")
                .addCustomDataProperty("Custom_Event_Property_Key1", "Custom_Event_Property_val1")
                .addCustomDataProperty("Custom_Event_Property_Key2", "Custom_Event_Property_val2")
                .logEvent(this);

                //populate firebase events with Branch latd
                Branch.getInstance().getLastAttributedTouchData(
                new ServerRequestGetLATD.BranchLastAttributedTouchDataListener() {
                    @Override
                    public void onDataFetched(JSONObject jsonObject, BranchError error) {

                        if (error == null) {
                            if (jsonObject != null) {

                                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics
                                        .getInstance(getApplicationContext());

                                Bundle bundle = new Bundle();
                                try {
                                    JSONObject latd = jsonObject.getJSONObject("last_attributed_touch_data").getJSONObject("data");
                                    String feature = latd.getString("~feature");
                                    String channel = latd.getString("~channel");
                                    String campaign = latd.getString("~campaign");

                                    bundle.putString("utm_medium", feature);
                                    bundle.putString("utm_source", channel);
                                    bundle.putString("utm_campaign", campaign);

                                    firebaseAnalytics.logEvent("Purchase", bundle);

                                } catch (JSONException ignore)
                                /*try {
                                    JSONObject latd = jsonObject.getJSONObject("last_attributed_touch_data").getJSONObject("data");
                                    String feature = latd.getString("~feature");
                                    String channel = latd.getString("~channel");
                                    String campaign = latd.getString("~campaign");
                                    String keyword = latd.getString("~keyword");

 //                                   if (feature != null) {
                                        bundle.putString("utm_medium", feature);
               //                     }
             //                       if (channel != null) {
                                        bundle.putString("utm_source", channel);
           //                         }
                                    if (campaign.isEmpty()) {
                                    } else {
                                        bundle.putString("utm_campaign", campaign);
                                    }

                                    firebaseAnalytics.logEvent("test_event", bundle);

                                } catch (JSONException ignore)*/ {
                                    //firebaseAnalytics.logEvent("Purchase", bundle);
                                }

                            }
                        }
                    }
                }, 30);
    }


    public void setUser (View view) {
        //Branch.getInstance().disableTracking(false);
        EditText getUserID = findViewById(R.id.userIDField);
        TextView displayUserID = findViewById(R.id.userIDLabel);

        Branch.getInstance().setIdentity(getUserID.getText().toString());

        displayUserID.setText("The User Identity is now " + getUserID.getText().toString());

    }

    public void subscribe (View view) {

        new BranchEvent("Sub")
                .logEvent(MainActivity.this);

    }

    public void showLatd (View view) {

        JSONObject latd = Branch.getInstance().getLatestReferringParams();
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra("latd", latd.toString());
        startActivity(intent);

    }

    public void createLink (View view) {

        final TextView displayLink = findViewById(R.id.userIDLabel);

        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle("My Content Title");

        LinkProperties lp = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")
                .addControlParameter("$desktop_url", "http://example.com/home")
                .addControlParameter("custom", "data")
                .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));

        buo.generateShortUrl(this, lp, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    displayLink.setText(url);
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
/*
    public static class allowTracking extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Allow Tracking?")
                    .setPositiveButton("Track", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           Log.i("Response","User Cancelled");
                        }
                    })
                    .setNegativeButton("Don't track", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.trackingAllowed = true;

                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        Uri referrer = ActivityCompat.getReferrer(MainActivity.this);

        Log.i("intent string", String.valueOf(action));
        Log.i("uri string", String.valueOf(data));
        Log.i("referrer", String.valueOf(referrer));

        if (referrer != null){

            String checkReferrer = referrer.toString();
            String checkData = data.toString();

            if (checkReferrer.matches("(.*)github(.*)")){

                if (checkData.matches("^((?!.*gclid.*).)*$")){

                    Log.i("CHECK", "Organic link 'bout to fire");

                    Intent intentOrganic = new Intent(this, MainActivity.class);
                    intentOrganic.putExtra("branch","https://oleg.app.link/rGRh4TDjz8");
//                    intent.putExtra("branch_force_new_session",true);
                    this.setIntent(intentOrganic);
                }
            }
        }

        /*intent.putExtra("branch_force_new_session",true);
        startActivity(intent);*/

        /*Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null){
            Log.i("uri", data.toString());
        } else
        {
            return;
        }

        DialogFragment newFragment = new allowTracking();
        newFragment.show(getSupportFragmentManager(), "missiles");

            if (trackingAllowed) {
                Branch.sessionBuilder(this).withCallback(new Branch.BranchReferralInitListener() {
                    @Override
                    public void onInitFinished(JSONObject referringParams, BranchError error) {

                        Log.i("show params", String.valueOf(referringParams));

                        if (error == null) {

//                  save data to be used later
                            SharedPreferences preferences = MainActivity.this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                            preferences.edit().putString("branchData", referringParams.toString()).apply();

                            String canonical = referringParams.optString("$canonical_url", "");
                            if (canonical.matches("second")) {

                                //MainActivity(new Intent(this, SecondActivity.class));
                                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(MainActivity.this, "deeplink not recognised", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Log.i("BranchSDK", error.getMessage());
                        }
                    }
                }).withData(this.getIntent().getData()).init();
            } else {
                Log.i("Response", "No SDK for U :(");
            }*/
        Branch.sessionBuilder(this).withCallback(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {

                Log.i("show params", String.valueOf(referringParams));

                if (error == null) {

//                  save data to be used later
                    SharedPreferences preferences = MainActivity.this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    preferences.edit().putString("branchData", referringParams.toString()).apply();

                    String canonical = referringParams.optString("$canonical_url","");
                    if (canonical.matches("second")) {

                        //MainActivity(new Intent(this, SecondActivity.class));
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        startActivity(intent);

                    } else
                    {
                        Toast.makeText(MainActivity.this, "deeplink not recognised", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Log.i("BranchSDK", error.getMessage());
                }
            }
        }).withData(this.getIntent().getData()).init();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //Branch.getInstance().disableTracking(true);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            // do stuff with deep link data (nav to page, display content, etc)
        }

    };

}
