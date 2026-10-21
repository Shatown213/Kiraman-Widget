package com.kiraman.app;

import android.content.Context;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/** Pont JS -> widget : l'application web envoie un JSON, le widget l'affiche. */
@CapacitorPlugin(name = "KiramanWidget")
public class KiramanWidgetPlugin extends Plugin {
    @PluginMethod
    public void update(PluginCall call) {
        String json = call.getString("json");
        if (json == null) {
            call.reject("json manquant");
            return;
        }
        Context ctx = getContext();
        ctx.getSharedPreferences("kiraman_widget", Context.MODE_PRIVATE)
            .edit().putString("data", json).apply();
        KiramanWidgetProvider.refreshAll(ctx);
        call.resolve();
    }
}
