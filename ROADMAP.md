# Feuille de route — Sauve

> Dernière mise à jour : novembre 2026.

## 1. Fonctionnalités à venir
- **v1.0 (validation & publication)** :
  - Relecture complète des protocoles (ERC 2021 / PSC1) par un formateur PSC1 ou un médecin urgentiste.
  - Première release étiquetée sur GitHub et soumission F-Droid / Google Play.
- **v1.x** :
  - Enrichissement des illustrations vectorielles de gestes d'urgence et vérification des annonces vocales hors-ligne sur différents moteurs TTS.

## 2. Aspect visuel & UX
- Contrôle de lisibilité en situation d'urgence réelle (thème noir OLED absolu, cibles tactiles larges, zéro distraction).
- Tests de captures d'écran multi-langues et vérification TalkBack / VoiceOver.

## 3. Qualité de code & sécurité
- Sauvegarde Android configurable par application (`-Ppilmil.allowAutoBackup=false` par défaut, aucune permission `INTERNET`).
- Exécution de `testReleaseUnitTest` et `:app:lintDebug` en CI.
- **Prioritaire** : augmenter la couverture de tests unitaires dans `shared/src/commonTest` (protocoles de secourisme, états d'urgence, cadence RCP) et ajouter `ktlint` / `detekt`.

## 4. Feuille de route iOS
- **État actuel (appli pilote KMP)** : `shared/src/iosMain` (`AVSpeechSynthesizer`, `UIImpactFeedbackGenerator`, `UNUserNotificationCenter`) et `iosApp/` (`SauveApp.swift`, `SauveWidget.swift`, `project.yml`) compilent déjà en CI (`macos-15`, artefact `Sauve-simulateur.zip`).
- **Ce qu'il reste** :
  1. Tester sur iPhone réel / simulateur : audio du métronome, synthèse vocale, haptique et widget WidgetKit.
  2. Configurer le compte Apple Developer, signer l'archive et publier sur TestFlight / App Store.
