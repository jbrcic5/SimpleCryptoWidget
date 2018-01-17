package josip.bitfinexwidgetapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Implementation of App Widget functionality.
 */
public class CurrencyPairsWidget extends AppWidgetProvider {

    public static final String ACTION_UPDATE_CLICK = "josip.bitfinexwidgetapp.action.UPDATE_CLICK";
    public static int counter = 0;
    public static String lastIotaPrice = "NULL";
    public static String lastBtcPrice = "NULL";
    public static String lastEthPrice = "NULL";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, getClass());
            intent.setAction(ACTION_UPDATE_CLICK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.currency_pairs_widget);


            thread.start();
            threadBtc.start();
            threadEth.start();

            try {
                thread.join();
                threadBtc.join();
                threadEth.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            views.setTextViewText(R.id.appwidget_text, lastBtcPrice + "\n" + lastEthPrice + "\n" + lastIotaPrice);
            views.setOnClickPendingIntent(R.id.refresh_button, pendingIntent );

            //updateAppWidget(context, appWidgetManager, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void onUpdate(Context context)
    {
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidgetComponentName = new ComponentName(context.getPackageName(),getClass().getName());
        int[] awi = awm.getAppWidgetIds(thisAppWidgetComponentName);

        onUpdate(context, awm, awi);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);


        if (ACTION_UPDATE_CLICK.equals(intent.getAction())) {
            Log.d("error", "error");

            onUpdate(context);
        }


    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try
            {
                String url = "https://api.bitfinex.com/v1/pubticker/iotusd";
                InputStream response = new URL(url).openStream();

                StringBuilder sb = new StringBuilder();
                try (Scanner scanner = new Scanner(response)) {
                    sb.append(scanner.useDelimiter("\\A").next());
                }

                JSONObject obj = new JSONObject(sb.toString());
                lastIotaPrice = obj.getString("last_price");
            }
            catch(Exception ex)
            {
                lastIotaPrice = "ERROR";
            }
        }
    });

    Thread threadBtc = new Thread(new Runnable() {
        @Override
        public void run() {
            try
            {
                String url = "https://api.bitfinex.com/v1/pubticker/btcusd";
                InputStream response = new URL(url).openStream();

                StringBuilder sb = new StringBuilder();
                try (Scanner scanner = new Scanner(response)) {
                    sb.append(scanner.useDelimiter("\\A").next());
                }

                JSONObject obj = new JSONObject(sb.toString());
                lastBtcPrice = obj.getString("last_price");
                int pointIndex = lastBtcPrice.indexOf(".");
                lastBtcPrice = lastBtcPrice.substring(0, pointIndex);
            }
            catch(Exception ex)
            {
                lastBtcPrice = "ERROR";
            };
        }
    });

    Thread threadEth = new Thread(new Runnable() {
        @Override
        public void run() {
            try
            {
                String url = "https://api.bitfinex.com/v1/pubticker/ethusd";
                InputStream response = new URL(url).openStream();

                StringBuilder sb = new StringBuilder();
                try (Scanner scanner = new Scanner(response)) {
                    sb.append(scanner.useDelimiter("\\A").next());
                }

                JSONObject obj = new JSONObject(sb.toString());
                lastEthPrice = obj.getString("last_price");
            }
            catch(Exception ex)
            {
                lastEthPrice = "ERROR";
            }
        }
    });

}

