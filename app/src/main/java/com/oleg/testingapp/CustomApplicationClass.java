package com.oleg.testingapp;

import android.app.Application;
import android.util.Log;

import io.branch.referral.Branch;

public class CustomApplicationClass extends Application {

    @Override
    public void onCreate() {

        Log.i("response", "oncreate called");

        // Branch logging for debugging
        Branch.enableLogging();

        // Branch object initialization
        Branch.getAutoInstance(this);

        //Branch.expectDelayedSessionInitialization(true);

 //       Branch.getInstance().disableTracking(true);
        // Disable Branch object initialization
        /*Branch b = Branch.getAutoInstance(this);
        b.disableTracking(true);*/
    }
}
