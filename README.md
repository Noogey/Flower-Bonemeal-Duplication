# Flower Bone Meal Duplication (Fabric, Minecraft 26.2)

Using bone meal on **any flower** makes it drop one copy of itself. This is the vanilla
tall-flower behaviour ("applying bone meal to a double flower causes the flower to drop a copy
of itself"), extended to every flower on the Minecraft Wiki's Flower page:

- **Small flowers (17):** allium, azure bluet, blue orchid, cornflower, dandelion, open/closed
  eyeblossom, golden dandelion, lily of the valley, oxeye daisy, poppy, torchflower,
  4 tulips, wither rose
- **Tall flowers (5):** lilac, peony, rose bush, sunflower, **and pitcher plant**
- **Other flowers (9):** cherry leaves, chorus flower, flowering azalea, flowering azalea
  leaves, mangrove propagule, pink petals, spore blossom, wildflowers, cactus flower

Works by hand and from dispensers. Each use costs one bone meal and drops one item; the
flower stays in place. Fabric API is **not** required.

## Settings

| Setting | Default | What it does |
|---|---|---|
| Mod Enabled | Yes | **No** switches the whole mod off, and bone meal works exactly like vanilla |
| Flowering Azalea Drops Copy | Yes | **No** = vanilla (grows an azalea tree) |
| Mangrove Propagule Drops Copy | No | **No** = vanilla (grows the propagule a stage) |
| Pink Petals & Wildflowers Drop Copy | Yes | **No** = vanilla (adds a petal / spreads) |

**With [Mod Menu](https://modrinth.com/mod/modmenu)** (optional): go to Mods → Flower Bone Meal
Duplication → Configure. Each setting has its name on the left and a Yes/No button on the right.
Click to flip it, and hover for a description. While Mod Enabled is No, the other three are greyed
out. Changes apply immediately.

**Without Mod Menu** (or on a dedicated server), edit `.minecraft/config/flowerbonemeal.json`
(created on first launch), then restart:

```json
{
  "modEnabled": true,
  "floweringAzaleaDropsCopy": true,
  "mangrovePropaguleDropsCopy": false,
  "pinkPetalsAndWildflowersDropCopy": true
}
```

On a multiplayer server, the **server's** config file decides what happens.

### Modded flowers

Each toggle also covers modded flowers that behave the same way, detected automatically from
the vanilla code they're built on:

- **Like mangrove propagule** (grows in stages): built on `MangrovePropaguleBlock`
- **Like pink petals / wildflowers** (adds petals): built on `FlowerBedBlock`
- **Like flowering azalea** (grows into a tree): built on `AzaleaBlock` or `SaplingBlock`

A modded flower that does something similar with its own code can't be detected
automatically. Add it to the matching tag with a datapack:

- `data/flowerbonemeal/tags/block/behaves_like_flowering_azalea.json`
- `data/flowerbonemeal/tags/block/behaves_like_mangrove_propagule.json`
- `data/flowerbonemeal/tags/block/behaves_like_pink_petals.json`

Modded flowers in the vanilla `#minecraft:flowers` tag duplicate automatically. Anything
in neither group simply drops a copy.

## Build

Requires **JDK 25**.

```
./gradlew build          # Windows: gradlew.bat build
```

The jar is written to `build/libs/flower-bonemeal-duplication-3.1.0.jar` (ignore any `-sources`
jar). Put it in `.minecraft/mods` with Fabric Loader 0.19.5+. Mod Menu is optional.

No JDK? Push this folder to a GitHub repository. The included workflow
(`.github/workflows/build.yml`) builds it, and the jar appears under the run's **Artifacts**.

## Other tags

- `duplicates_with_bone_meal.json`: which blocks count as flowers. It includes
  `#minecraft:flowers` plus each wiki flower by name.
- `keeps_vanilla_bone_meal.json`: always use vanilla behaviour for these, regardless of the
  toggles. It's empty by default.
