# Flower Bone Meal Duplication (Fabric, Minecraft 26.2)

Using bone meal on a flower makes it drop one copy of itself, for the flowers vanilla Java
doesn't already handle. The flower stays in place, each use costs one bone meal, and it works
by hand and from dispensers. Fabric API is **not** required.

## Which flowers

**Always duplicate (while the mod is enabled):**
- Small flowers: allium, azure bluet, blue orchid, cornflower, dandelion, open/closed eyeblossom,
  lily of the valley, oxeye daisy, poppy, and the 4 tulips
- Cherry leaves, chorus flower, flowering azalea leaves, spore blossom, cactus flower

**Controlled by a setting** (see below): mangrove propagule, flowering azalea, regular azalea,
torchflower, pitcher plant, wither rose, golden dandelion.

**Left to vanilla**, since vanilla already does something useful with them:
- Lilac, peony, rose bush, sunflower: vanilla already drops a copy
- Pink petals, wildflowers: vanilla already grows a petal

## Settings

| Setting | Choices | Default |
|---|---|---|
| Mod Enabled | Yes / No | Yes |
| Mangrove Propagule Drops Copy | Yes / No | No |
| Azalea Duplication | Flowering Only / Flowering and Regular / Neither | Flowering Only |
| Torchflower & Pitcher Plant Drop Copy | Yes / No | Yes |
| Wither Rose & Golden Dandelion | Wither Rose Only / Golden Dandelion Only / Both / Off | Both |

- **Mod Enabled: No** switches the whole mod off, and bone meal works exactly like vanilla.
- **Mangrove Propagule: No** means vanilla behaviour (it grows toward a tree).
- **Azalea:**
  - *Flowering Only*: flowering azalea drops a copy, and regular azalea grows a tree.
  - *Flowering and Regular*: both drop a copy, and neither grows a tree from bone meal.
  - *Neither*: both grow an azalea tree (vanilla).
- **Torchflower & Pitcher Plant: No** means bone meal does nothing to them (vanilla).
- **Wither Rose & Golden Dandelion:** chooses which of the two drop a copy. *Off* means neither
  does (vanilla).

**With [Mod Menu](https://modrinth.com/mod/modmenu)** (optional): go to Mods → Flower Bone Meal
Duplication → Configure. Each setting has its name on the left and a button on the right.
Clicking the button moves to the next choice, and hovering shows what each choice does. While
Mod Enabled is No, the other settings are greyed out. Changes apply immediately.

**Without Mod Menu** (or on a dedicated server), edit `.minecraft/config/flowerbonemeal.json`
(created on first launch), then restart:

```json
{
  "modEnabled": true,
  "mangrovePropaguleDropsCopy": false,
  "azaleaMode": "FLOWERING_ONLY",
  "torchflowerAndPitcherPlantDropCopy": true,
  "witherRoseAndGoldenDandelion": "BOTH"
}
```

Valid values are `FLOWERING_ONLY` / `FLOWERING_AND_REGULAR` / `NEITHER` for `azaleaMode`, and
`WITHER_ROSE_ONLY` / `GOLDEN_DANDELION_ONLY` / `BOTH` / `OFF` for
`witherRoseAndGoldenDandelion`.

On a multiplayer server, the **server's** config file decides what happens.

### Modded flowers

Flowers from other mods that are in the vanilla `#minecraft:flowers` tag are handled
automatically, based on the vanilla code they're built on:

- **Adds petals** like pink petals (`FlowerBedBlock`): left to vanilla
- **Tall flower that already drops a copy** (bone-mealable `DoublePlantBlock`): left to vanilla
- **Grows in stages** like a mangrove propagule (`MangrovePropaguleBlock`): Mangrove Propagule setting
- **Grows into a tree** like flowering azalea (`AzaleaBlock` / `SaplingBlock`): treated as
  flowering azalea in the Azalea setting
- **Anything else:** always drops a copy

To put a modded flower under a particular setting by hand, add it with a datapack to the
matching tag in `data/flowerbonemeal/tags/block/`:

| Tag | Setting |
|---|---|
| `behaves_like_mangrove_propagule` | Mangrove Propagule |
| `behaves_like_flowering_azalea` | Azalea, flowering side |
| `behaves_like_azalea` | Azalea, regular side |
| `torchflower_and_pitcher_plant` | Torchflower & Pitcher Plant |
| `wither_rose` / `golden_dandelion` | Wither Rose & Golden Dandelion |
| `keeps_vanilla_bone_meal` | never touched by the mod |
| `duplicates_with_bone_meal` | the full list of blocks the mod looks at |

## Build

Requires **JDK 25**.

```
./gradlew build          # Windows: gradlew.bat build
```

The jar is written to `build/libs/flower-bonemeal-duplication-4.0.0.jar`. Put it in
`.minecraft/mods` with Fabric Loader 0.19.5+. Mod Menu is optional.

No JDK? Push this folder to a GitHub repository. The included workflow
(`.github/workflows/build.yml`) builds it, and the jar appears under the run's **Artifacts**.
