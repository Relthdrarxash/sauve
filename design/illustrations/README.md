# Schémas des gestes de premiers secours

Pictogrammes médicaux sobres, affichés au-dessus des consignes dans l'appli (massage cardiaque, étouffement, hémorragie, PLS).

## Fichiers

| Fichier | Geste |
|---|---|
| `cpr_adult.svg` | Massage cardiaque adulte, deux mains, bras tendus |
| `cpr_child.svg` | Massage cardiaque enfant, une main |
| `cpr_infant.svg` | Massage cardiaque nourrisson, deux doigts (vue du dessus) |
| `choking_back_blows.svg` | Claques dans le dos (adulte, enfant) |
| `choking_heimlich.svg` | Compressions abdominales (Heimlich) |
| `choking_infant.svg` | Tapes dans le dos du nourrisson sur l'avant-bras |
| `bleeding_pressure.svg` | Compression directe d'une hémorragie |
| `recovery_position.svg` | PLS en 4 vignettes (viewBox 640x200) |

## Style

- viewBox `0 0 320 200` (sauf la PLS), fond transparent, thème sombre de l'appli (`#121212`).
- Trait 3 px, contour `#ECEFF1`, remplissages `#37474F` (victime) et `#546E7A` (sauveteur).
- Une seule couleur d'accent, `#E53935` : mains qui appuient, flèche de mouvement, point d'appui.
- Silhouettes simplifiées, ni visage, ni sang, ni blessure. Aucun texte : les légendes sont dans l'appli, traduites via `tr(fr, en)`.
- Formes limitées à `path`, `circle`, `ellipse`, `rect` (pas de dégradé, de tirets ni de marqueurs), pour une conversion fiable en Vector Drawable.

## Régénérer les ressources Android / Compose

Les SVG sont convertis en Vector Drawable dans `shared/src/commonMain/composeResources/drawable/` (ressources Compose Multiplatform, classe générée `fr.sauve.app.resources.Res`) :

```
for f in design/illustrations/*.svg; do
  n=$(basename "$f" .svg)
  npx -y svg2vectordrawable@2.9.1 -i "$f" -o "shared/src/commonMain/composeResources/drawable/$n.xml"
done
```

Après modification d'un SVG, régénérer le XML correspondant et recompiler. Ne modifier que les SVG : les XML sont dérivés.

## Licence

MPL-2.0, comme le reste du dépôt (voir `LICENSE`). Dessins originaux, sans élément tiers.
