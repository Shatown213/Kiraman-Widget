#!/usr/bin/env bash
# Ajoute les permissions nécessaires à Kiraman dans le manifeste Android :
#  - VIBRATE : vibrations (navigator.vibrate)
#  - ACCESS_*_LOCATION : position pour les horaires de prière / Mawaqit
#  - POST_NOTIFICATIONS, SCHEDULE_EXACT_ALARM, USE_EXACT_ALARM, RECEIVE_BOOT_COMPLETED : notifications à l'heure exacte
set -euo pipefail
M="android/app/src/main/AndroidManifest.xml"
[ -f "$M" ] || { echo "Manifeste introuvable : lance d'abord 'npx cap add android'"; exit 1; }

add() {
  grep -q "android.permission.$1" "$M" && return 0
  perl -0pi -e "s|<application|<uses-permission android:name=\"android.permission.$1\" />\n    <application|" "$M"
}
add VIBRATE
add ACCESS_COARSE_LOCATION
add ACCESS_FINE_LOCATION
add INTERNET
add POST_NOTIFICATIONS
add SCHEDULE_EXACT_ALARM
add USE_EXACT_ALARM
add RECEIVE_BOOT_COMPLETED
add WAKE_LOCK
echo "Permissions OK :"; grep "uses-permission" "$M"

# Harmonise les barres système Android avec le fond vert de Kiraman.
# Capacitor configure le fond de la WebView, mais les barres système sont
# dessinées par Android et doivent être configurées séparément dans le thème.
python3 - <<'PYTHON'
from pathlib import Path
import re

root = Path("android/app/src/main/res")
files = list(root.glob("values*/styles.xml"))
if not files:
    print("Aucun styles.xml trouvé : couleurs système non modifiées")
else:
    items = {
        "android:statusBarColor": "#00000000",
        "android:navigationBarColor": "#00000000",
        "android:windowLightStatusBar": "false",
        "android:windowLightNavigationBar": "false",
        "android:windowDrawsSystemBarBackgrounds": "true",
    }
    for path in files:
        text = path.read_text()
        # Appliquer aux thèmes de l'application, sans toucher aux thèmes d'écran de lancement.
        matches = list(re.finditer(r'<style\s+name="(?!.*SplashScreen)[^"]*"[^>]*>.*?</style>', text, re.S))
        changed = False
        for match in reversed(matches):
            block = match.group(0)
            if not re.search(r'name="AppTheme"', block):
                continue
            for name, value in items.items():
                pattern = rf'<item\s+name="{re.escape(name)}"[^>]*>.*?</item>'
                replacement = f'<item name="{name}">{value}</item>'
                if re.search(pattern, block, re.S):
                    block = re.sub(pattern, replacement, block, count=1, flags=re.S)
                else:
                    block = block.replace('</style>', f'        {replacement}\n    </style>')
                changed = True
            text = text[:match.start()] + block + text[match.end():]
        if changed:
            path.write_text(text)
            print(f"Barres système harmonisées : {path}")
        else:
            print(f"Thème AppTheme introuvable dans {path}")
PYTHON


# Copie le code natif de Kiraman : affichage bord à bord (MainActivity), widget d'accueil
# et icône de notification. Les sources sont dans le dossier native/.
APP_DIR="android/app/src/main"
JAVA_DIR="$APP_DIR/java/com/kiraman/app"
mkdir -p "$JAVA_DIR"
cp native/java/com/kiraman/app/*.java "$JAVA_DIR/"
cp -R native/res/. "$APP_DIR/res/"
echo "Code natif copié (MainActivity, widget, icône de notification)"

# Déclare le widget dans le manifeste (une seule fois).
python3 - <<'PYTHON'
from pathlib import Path
m = Path("android/app/src/main/AndroidManifest.xml")
t = m.read_text()
if "KiramanWidgetProvider" in t:
    print("Widget déjà déclaré dans le manifeste")
else:
    receiver = """
        <receiver android:name=".KiramanWidgetProvider" android:exported="true" android:label="Kiraman">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <action android:name="android.intent.action.TIME_SET" />
                <action android:name="android.intent.action.TIMEZONE_CHANGED" />
            </intent-filter>
            <meta-data android:name="android.appwidget.provider" android:resource="@xml/kiraman_widget_info" />
        </receiver>
"""
    t = t.replace("</application>", receiver + "    </application>", 1)
    m.write_text(t)
    print("Widget déclaré dans le manifeste")
PYTHON
