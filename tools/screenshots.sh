#!/usr/bin/env bash
# Régénère les captures d'écran de l'appli (docs/marketing/captures/*.png) sans émulateur ni téléphone.
# Paparazzi rend les écrans Compose sur la JVM ; le script lance les tests de capture puis copie les PNG bruts (1080x2340).
# Usage : ./tools/screenshots.sh
set -euo pipefail

racine="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$racine"

sortie="docs/marketing/captures"
# Dossier où le mode « record » de Paparazzi écrit ses images.
images="app/src/test/snapshots/images"

# Repart d'un dossier d'images propre et force le rendu : une capture périmée ne doit jamais passer pour récente.
rm -rf "$images"
./gradlew :app:cleanTestDebugUnitTest :app:recordPaparazziDebug --no-daemon

mkdir -p "$sortie"

# Paparazzi nomme les fichiers <classe>_<méthode>_<nom du snapshot>.png : on les retrouve par le nom du snapshot.
for nom in accueil arret_cardiaque massage etouffement hemorragie pls guide apropos; do
  source=$(find "$images" -name "*_${nom}.png" | head -n 1)
  if [ -z "$source" ]; then
    echo "Capture introuvable : ${nom}" >&2
    exit 1
  fi
  cp "$source" "$sortie/${nom}.png"
  echo "OK ${sortie}/${nom}.png"
done
