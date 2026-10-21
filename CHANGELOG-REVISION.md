# Kiraman — révision

## Améliorations
- Sauvegarde exportable JSON avec date dans le nom du fichier.
- Restauration depuis un fichier JSON avec validation minimale, confirmation explicite et retour utilisateur.
- Les erreurs de stockage local ne sont plus silencieuses : un avertissement est affiché.
- Le texte d'accueil sur la géolocalisation correspond désormais aux fonctionnalités présentes.
- Les actions de test sont cachées par défaut et conditionnées au paramètre `?debug=1`.
- Vérification syntaxique JavaScript réussie (`node --check`).

## Limites restantes
- Aucun test instrumenté sur téléphone n'a été réalisé.
- La compilation APK doit être effectuée via Android SDK/Gradle ou le workflow GitHub Actions.
- Le système d'horaires, les notifications Android et le podomètre nécessitent des tests fonctionnels sur appareils réels.


## Révision bord à bord + Qibla
- Affichage Android bord à bord : barres système transparentes, icônes claires et gestion des marges de sécurité CSS.
- Ajout d'un accès « Direction de la Qibla » sous le bilan hebdomadaire et mensuel de l'accueil.
- Nouvelle page boussole aux couleurs vert émeraude et or de Kiraman.
- Calcul de l'azimut de la Qibla à partir de la position, avec lecture du capteur d'orientation lorsque le téléphone le prend en charge.
- La boussole et la géolocalisation nécessitent des autorisations et doivent être testées sur un appareil réel.
- APK non compilé dans cet environnement ; utiliser le workflow GitHub Actions fourni pour générer et tester l'APK.


## Correctifs après retour utilisateur
- Rétablissement permanent de la section « Phase test » dans Réglages.
- Boussole : la rose entière tourne avec le cap du téléphone et le repère de la Qibla suit l’azimut calculé.
- Ajout d’une saisie manuelle latitude/longitude pour tester la Qibla lorsque le navigateur bloque la géolocalisation (fichier local/permissions).
- Messages d’erreur GPS plus explicites et meilleure gestion du listener d’orientation.
- Validation de syntaxe JavaScript avec Node.js ; tests réels GPS/capteur Android encore nécessaires.


## Ajustements demandés — accueil
- Carte « Prochaine prière » enrichie et placée après Endurance / XP.
- Fond général, inventaire sacré, et bloc Endurance / XP conservés sans modification de leur style.
- Les artefacts restent uniquement dans l’inventaire sacré ; aucun rang d’artefacts n’est ajouté sous le logo.


## Revision 21 octobre 2026
- Ajout du nom arabe كرامان sous le titre KIRAMAN.
- Suppression du pictogramme étoile dans l'emblème SVG principal.
- Conservation de la section Phase test, de l'inventaire sacré et des coordonnées manuelles pour la Qibla.
- Icône PNG ajustée pour retirer l'étoile; une vérification visuelle reste recommandée.


## Retouches esthétiques (logo, titre, inventaire) — v2
- Logo d'en-tête : croissant sans étoile, plume et livre, repris de la capture de référence (image transparente intégrée dans `www/index.html`).
- Les artefacts sous le titre sont supprimés.
- « كرامان » : texte arabe en police Amiri (Google Fonts, comme Fraunces), dégradé doré identique à KIRAMAN.
- Inventaire sacré : boussole, clé ancienne, livre et sablier en images dorées reprises de la capture.
- Icône (`assets/icon.png`) : logo sans étoile sur fond vert.
- Correctif CSS : une séquence `\n` littérale cassait la règle de style du titre `h1`.


## v1.1 — notifications, widget, workflow
- 12 types de notifications configurables (prières, 15 min avant, rapport du soir, rappel d'hier, artefact en danger, Jumu'a, Coran, bilan hebdo, adhkar, Tahajjud, eau, sport).
- Widget Android natif 4x2 (prochaine prière + compte à rebours, endurance, XP).
- Workflow GitHub Actions mis à jour (ubuntu-24.04, actions récentes, Node 22).


## v1.2 — en-tête compact et style 16-bit
- Logo recomposé : plus large, moins haut, croissant descendu à gauche de la plume (icône de l'application mise à jour).
- Espaces réduits entre le logo, KIRAMAN et le titre arabe.
- Le style « Rétro 8-bit » devient « Rétro 16-bit » : polices Pixelify Sans, cadres biseautés aux coins légèrement arrondis, boutons et jauges dégradés, logo et artefacts en pixel art plus fin.
