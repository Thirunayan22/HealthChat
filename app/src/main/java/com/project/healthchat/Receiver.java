package com.project.healthchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

public class Receiver extends BroadcastReceiver {



    View parentView;

    public Receiver(){

    }

    public Receiver(View passedParentView){
        this.parentView = passedParentView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);
        Log.e("Network Status", status);

        if(status == context.getString(R.string.device_offline)){
            Snackbar snackbar = Snackbar.make(parentView,R.string.device_offline,Snackbar.LENGTH_LONG);
            snackbar.show();

        }
        else if(status == context.getString(R.string.device_online)){
            Snackbar snackbar = Snackbar.make(parentView,R.string.device_online,Snackbar.LENGTH_SHORT);
            snackbar.show();
        }






        Toast toast = Toast.makeText(context,status,Toast.LENGTH_LONG);
        toast.show();
    }
}
