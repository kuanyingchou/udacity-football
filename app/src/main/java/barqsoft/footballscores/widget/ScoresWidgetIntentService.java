/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.Utilies;

public class ScoresWidgetIntentService extends IntentService {

    public ScoresWidgetIntentService() {
        super("ScoresWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ScoresWidgetProvider.class));

        // Get today's data from the ContentProvider

        Cursor data = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, new String[]{ Utilies.getToday() }, null);
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }


        // Extract the weather data from the Cursor
        String home = data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
        String away = data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
        String homeScore =
                data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
        String awayScore =
                data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));

        data.close();

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            ScoresWidgetProvider.update(this, appWidgetManager, appWidgetId,
                    String.format("%s %s:%s %s", home, homeScore, awayScore, away));
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
//        views.setContentDescription(R.id.widget_icon, description);
    }
}
