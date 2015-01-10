package com.gbaldera.yts.receivers;


import android.content.Context;
import android.content.Intent;

import com.gbaldera.yts.activities.MainActivity;
import com.parse.ParsePushBroadcastReceiver;

public class YtsParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
