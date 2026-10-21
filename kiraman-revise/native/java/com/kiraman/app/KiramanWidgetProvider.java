package com.kiraman.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

/** Widget Kiraman : prochaine prière (avec compte à rebours), endurance et XP. */
public class KiramanWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_REFRESH = "com.kiraman.app.WIDGET_REFRESH";
    private static final String PREFS = "kiraman_widget";
    private static final String KEY = "data";

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) {
            mgr.updateAppWidget(id, build(ctx));
        }
        scheduleNext(ctx);
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        super.onReceive(ctx, intent);
        String a = intent == null ? null : intent.getAction();
        if (ACTION_REFRESH.equals(a)
                || Intent.ACTION_BOOT_COMPLETED.equals(a)
                || Intent.ACTION_TIME_CHANGED.equals(a)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(a)) {
            refreshAll(ctx);
        }
    }

    public static void refreshAll(Context ctx) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        int[] ids = mgr.getAppWidgetIds(new ComponentName(ctx, KiramanWidgetProvider.class));
        for (int id : ids) {
            mgr.updateAppWidget(id, build(ctx));
        }
        scheduleNext(ctx);
    }

    private static String readData(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null);
    }

    /** Heure (ms) de la prochaine prière connue, ou 0. */
    private static long nextTime(String raw) {
        if (raw == null) return 0;
        try {
            JSONObject o = new JSONObject(raw);
            JSONArray ts = o.optJSONArray("ts");
            long now = System.currentTimeMillis();
            if (ts != null) {
                for (int i = 0; i < ts.length(); i++) {
                    JSONArray e = ts.getJSONArray(i);
                    long t = e.getLong(1);
                    if (t > now) return t;
                }
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    static void scheduleNext(Context ctx) {
        try {
            AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            if (am == null) return;
            Intent i = new Intent(ctx, KiramanWidgetProvider.class).setAction(ACTION_REFRESH);
            PendingIntent pi = PendingIntent.getBroadcast(
                ctx, 7, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            am.cancel(pi);
            long next = nextTime(readData(ctx));
            if (next <= 0) return;
            long at = next + 2000;
            if (Build.VERSION.SDK_INT >= 23) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, at, pi);
            }
        } catch (Exception ignored) {
        }
    }

    static RemoteViews build(Context ctx) {
        RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.kiraman_widget);
        Intent launch = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
        if (launch != null) {
            PendingIntent pi = PendingIntent.getActivity(
                ctx, 0, launch, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            v.setOnClickPendingIntent(R.id.kw_root, pi);
        }
        String raw = readData(ctx);
        if (raw == null) {
            v.setTextViewText(R.id.kw_name, "Ouvre Kiraman");
            v.setTextViewText(R.id.kw_ptime, "");
            v.setViewVisibility(R.id.kw_cd, View.GONE);
            v.setProgressBar(R.id.kw_end, 100, 0, false);
            v.setTextViewText(R.id.kw_endtxt, "Endurance");
            v.setTextViewText(R.id.kw_xp, "");
            return v;
        }
        try {
            JSONObject o = new JSONObject(raw);
            long now = System.currentTimeMillis();
            String name = null;
            long when = 0;
            JSONArray ts = o.optJSONArray("ts");
            if (ts != null) {
                for (int i = 0; i < ts.length(); i++) {
                    JSONArray e = ts.getJSONArray(i);
                    long t = e.getLong(1);
                    if (t > now) {
                        name = e.getString(0);
                        when = t;
                        break;
                    }
                }
            }
            if (name != null) {
                v.setTextViewText(R.id.kw_name, name);
                v.setTextViewText(R.id.kw_ptime,
                    new SimpleDateFormat("HH:mm", Locale.FRANCE).format(new Date(when)));
                long base = SystemClock.elapsedRealtime() + (when - now);
                v.setChronometer(R.id.kw_cd, base, null, true);
                if (Build.VERSION.SDK_INT >= 24) {
                    v.setChronometerCountDown(R.id.kw_cd, true);
                }
                v.setViewVisibility(R.id.kw_cd, View.VISIBLE);
            } else {
                v.setTextViewText(R.id.kw_name, o.optInt("loc", 0) == 1 ? "Ouvre Kiraman" : "Position non définie");
                v.setTextViewText(R.id.kw_ptime, "");
                v.setViewVisibility(R.id.kw_cd, View.GONE);
            }
            int e = Math.max(0, Math.min(100, o.optInt("e", 0)));
            v.setProgressBar(R.id.kw_end, 100, e, false);
            v.setTextViewText(R.id.kw_endtxt, "Endurance " + e + " %");
            long xp = o.optLong("xp", 0);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(Locale.FRANCE, "%,d XP", xp));
            sb.append(" · ").append(o.optInt("act", 0)).append("/4 artefacts");
            String nx = o.optString("nx", "");
            if (nx.length() > 0) sb.append(" · prochain : ").append(nx);
            v.setTextViewText(R.id.kw_xp, sb.toString());
        } catch (Exception ex) {
            v.setTextViewText(R.id.kw_name, "Ouvre Kiraman");
            v.setViewVisibility(R.id.kw_cd, View.GONE);
        }
        return v;
    }
}
