# Kiraman – projet Android (Capacitor)

## Contenu
- `www/index.html` : interface et logique de l'application.
- `assets/icon.png` : icône.
- `capacitor.config.json` : configuration Capacitor.
- `scripts/patch-android.sh` : permissions Android.
- `github/workflows/android.yml` : compilation automatique de l'APK debug avec GitHub Actions.

## Générer l'APK
Ce ZIP ne contient pas de dossier `android/` pré-généré. Le workflow GitHub le crée automatiquement.

1. Crée un dépôt GitHub vide.
2. Décompresse ce ZIP et envoie le contenu du dossier `kiraman-apk` à la racine du dépôt, sur `main`.
3. Ouvre l'onglet **Actions** et sélectionne **Build APK Android**.
4. Lance **Run workflow** si le build ne s'est pas déclenché automatiquement.
5. Quand l'exécution est terminée, ouvre-la et télécharge l'artefact `kiraman-apk`. Il contient `app-debug.apk`.

L'APK debug est destiné aux tests et à l'installation manuelle. Une publication sur Google Play nécessite une configuration de signature de production distincte.

## Modifications apportées dans cette révision
- Ajout de l'export des données Kiraman dans un fichier JSON daté.
- Ajout de l'import d'une sauvegarde JSON Kiraman, avec vérification du format et confirmation avant remplacement des données actuelles.
- Message plus exact lors du partage de position : le calcul local et la recherche de mosquées Mawaqit sont bien utilisés.
- Les commandes de test (+3 000 XP et simulation d'une prière manquée) sont masquées en mode normal. Elles restent accessibles en mode développeur avec `?debug=1`.
- Vérification syntaxique du JavaScript avec Node.js.

## À connaître
- Les données sont conservées localement sur l'appareil. Il faut exporter régulièrement une sauvegarde si l'on souhaite pouvoir restaurer ses données.
- Les horaires de prière et le fonctionnement en arrière-plan doivent être testés sur un appareil Android réel avant diffusion.
- L'APK n'a pas été compilé dans cet environnement : Android SDK/Gradle et l'accès aux dépendances de compilation n'étaient pas disponibles. Utilise le workflow ci-dessus pour obtenir un APK compilé dans GitHub Actions.


## Barres Android (haut et bas)
Le script `scripts/patch-android.sh` configure aussi la barre d'état et la barre de navigation Android en vert foncé `#06221a`, avec des icônes claires, pour les fondre visuellement dans le thème de Kiraman. Android peut toutefois imposer des comportements différents selon sa version, le fabricant, les gestes de navigation et le mode edge-to-edge. Cette modification doit être compilée puis vérifiée sur le téléphone cible.


## Révision bord à bord et boussole Qibla
Cette révision active les barres système transparentes Android via `scripts/patch-android.sh` et ajoute une page Qibla accessible depuis l'accueil, sous le bilan hebdomadaire/mensuel. Le calcul utilise les coordonnées de la Kaaba et la position courante. Le capteur d'orientation dépend du matériel et de la version Android/WebView.

Pour générer l'APK : pousser le projet dans un dépôt GitHub, ouvrir **Actions**, lancer **Build APK Android**, puis récupérer l'artefact `kiraman-apk`. Tester impérativement la navigation par gestes et boutons, les découpes d'écran, la localisation et la boussole sur l'appareil cible. Ce ZIP contient le projet source modifié, pas un APK déjà compilé.


### Correctifs récents
La section « Phase test » est toujours visible dans Réglages. Dans la page Qibla, il est possible d’utiliser le GPS ou de saisir manuellement latitude/longitude pour vérifier le calcul. L’ouverture d’un HTML depuis un fichier local peut bloquer le GPS ; tester le capteur exige un appareil compatible et une origine sécurisée/une application Android. La syntaxe JavaScript a été contrôlée, mais aucun test physique Android n’a été effectué.


## Notifications et widget (v1.1)
- **Notifications** : plugin `@capacitor/local-notifications`. Les rappels sont programmés sur 7 jours à chaque ouverture de l'application (Réglages > Notifications pour activer ou désactiver chaque type). Ouvre l'application au moins une fois par semaine pour qu'ils restent à jour.
- **Widget d'accueil** : appui long sur l'écran d'accueil Android > Widgets > Kiraman. Il affiche la prochaine prière avec compte à rebours, l'endurance et l'XP. Le code natif est dans `native/` et est copié dans le projet Android par `scripts/patch-android.sh`.
- **Workflow** : `ubuntu-24.04`, `actions/checkout@v6`, `setup-node@v6` (Node 22), `setup-java@v5`, `upload-artifact@v6`.
- Sur certains téléphones (Xiaomi, Huawei, Samsung...), désactive l'optimisation de batterie pour Kiraman afin que les notifications arrivent à l'heure exacte.
- Non testé sur appareil : vérifier notifications, alarmes exactes et widget sur ton téléphone.
