# EWS (Elemental Weakness System) Mod Documentation

## 1. The 13 elements

Each element has **two** separate attributes: one for **attack** (what the attacker deals) and one for **resistance** (what the victim mitigates). The resistance id is always the attack id + `_resist`.

| Element      | Attack       | Resistance             |
|--------------|--------------|--------------------------|
| Slash        | `slash`      | `slash_resist`           |
| Strike       | `strike`     | `strike_resist`          |
| Pierce       | `pierce`     | `pierce_resist`          |
| Fire         | `fire`       | `fire_resist`            |
| Ice          | `ice`        | `ice_resist`             |
| Lightning    | `lightning`  | `lightning_resist`       |
| Aqua         | `aqua`       | `aqua_resist`            |
| Holy         | `holy`       | `holy_resist`            |
| Ender        | `ender`      | `ender_resist`           |
| Blood        | `blood`      | `blood_resist`           |
| Evocation    | `evocation`  | `evocation_resist`       |
| Nature       | `nature`     | `nature_resist`          |
| Eldritch     | `eldritch`   | `eldritch_resist`        |

## 2. How the damage calculation works

Whenever something hits an entity, the mod recalculates damage element by element:

```
damage_of_that_element = original_damage × (element_attack_value / 100)
```

In other words, **an element's attack value is the percentage of the hit that becomes that damage type**. An attacker with `fire = 30` means 30% of the hit becomes fire damage.

That chunk of damage is then affected by the victim's **resistance** to that same element:

- Resistance between `0` and `100`: reduces that percentage linearly (`resistance = 50` → half the damage of that element).
- Resistance `> 100`: fully blocks that element (0 damage from it).
- Negative resistance: **amplifies** that element (`resistance = -20` → +20% damage from that element).

All 13 elements are calculated **independently** (each against 100% of the original damage) and then **summed**. If the sum of your 13 attack values doesn't reach 100, the difference is added separately as untyped damage, which **bypasses all resistances** — so if a mob only has `fire = 30` and nothing else, the remaining 70% of the hit will ignore every elemental resistance the victim has. Keep this in mind when balancing.

## 3. Data systems: which folder does what

There are two separate data systems, with **different formats**:

| Folder | What it assigns | Format |
|---|---|---|
| `groups`, `entities`, `items` | Elemental attributes to items/entities (gear, mob stats) | `modifiers` (see section 4) |
| `damage_types` | Fixed elemental composition of a vanilla `DamageType` (lava, fall, spells, etc.), independent of who caused it | `damage` + `special` (see section 6) — **this format hasn't changed** |

## 4. `groups` / `entities` / `items` format

### 4.1 File location

```
<your_datapack>/
├── data/
│   └── ews/
│       ├── groups/
│       │   └── <any_name>.json
│       ├── entities/
│       │   └── <modid>/
│       │       └── <entity_name>.json
│       └── items/
│           └── <modid>/
│               └── <item_name>.json
└── pack.mcmeta
```

Two ways to say who a file applies to:

- **Individual file** (`entities/minecraft/zombie.json`, `items/minecraft/diamond_sword.json`): the id is derived from the file's own path (`<folder_under_entities_or_items>/<name>.json` → `<that_folder>:<name>`). No `"ids"` field needed.
- **Group file** (`groups/<anything>.json`): the filename doesn't matter, you must list the ids explicitly in `"ids"`.

### 4.2 JSON structure

```json
{
  "ids": ["minecraft:zombie", "minecraft:husk"],
  "modifiers": [
    {
      "attribute": "fire",
      "operation": "addition",
      "value": 100
    }
  ]
}
```

- **`ids`** (only in `groups`, omitted for individual files): list of `"namespace:name"` ids this applies to.
- **`modifiers`**: list of modifiers. Each one has:
  - **`attribute`** *(required)*: the attribute it targets. A short name (`"fire"`, `"nature_resist"`) assumes EWS's own element; you can also give a full id (`"ews:fire"`), or in principle any other mod's (`"attributeslib:crit_chance"`).
  - **`operation`** *(optional, default `"addition"`)*: `"addition"`, `"multiply_base"`, or `"multiply_total"` (see 4.3).
  - **`value`** *(required)*: a number. Its meaning depends on `operation`.
  - **`slots`** *(optional, only has an effect on `items`, ignored on `entities`)*: a list with one or more of `head`, `chest`, `legs`, `feet`, `main`, `off`. If omitted, the modifier applies in whatever slot the item is worn/held in. If set, it **only** applies when the item is in that specific slot.

You don't need to list all 13 elements — anything not present in `modifiers` is simply 0.

### 4.3 The three operations

- **`addition`**: adds a flat value to the attribute. For EWS's own elements, this is the "% of the hit" described in section 2 — `{"attribute": "fire", "operation": "addition", "value": 100}` on a weapon means "100% of what this weapon hits for is fire."
- **`multiply_base`** and **`multiply_total`**: **percentage** bonuses (`value: 10` = +10%). Minecraft computes: `(base + sum of additions) × (1 + sum of multiply_base) × each multiply_total`.
  - **Important**: our 13 elements start at a base of `0`. A `multiply_base`/`multiply_total` on an attribute nobody set with `addition` **does nothing** (0 × anything = 0). These are meant to boost a value that **already exists** — e.g. a helmet with `{"attribute": "pierce", "operation": "multiply_base", "value": 5}` only does something if the weapon you're attacking with already grants some `pierce` on its own; if you attack with a 100% `slash` weapon, the helmet affects nothing (not `slash`, which is a separate attribute, and not `pierce`, which stays at 0).
  - `multiply_base` vs `multiply_total`: with several items giving `multiply_base` on the same attribute, their percentages **add up** before being applied (two `+10%` = `+20%` total). With `multiply_total`, each one is applied **in sequence** on top of the previous result (two `+10%` = `+21%` total). Use `multiply_base` unless you specifically want the compounding effect.

### 4.4 Priority: group vs. specific

If an item/entity matches both a `group` and its own dedicated file in `items`/`entities`, they're merged: if both define a modifier for the **same `attribute` + `operation`**, the specific one (`items`/`entities`) wins; every other modifier from both is kept. If two different **group** files both target the same id, their modifiers are **combined** in the list (not overridden).

## 5. Examples

### A weapon whose damage becomes 100% fire (individual file in `items`)
`data/ews/items/minecraft/blaze_rod_sword.json`:
```json
{
  "modifiers": [
    { "attribute": "fire", "operation": "addition", "value": 100 }
  ]
}
```

### A helmet giving +5% pierce damage, only while worn on the head
```json
{
  "ids": ["mymod:precision_helmet"],
  "modifiers": [
    { "attribute": "pierce", "operation": "multiply_base", "value": 5, "slots": ["head"] }
  ]
}
```

### An entity with resistances/weaknesses (individual file in `entities`)
`data/ews/entities/minecraft/zombie.json`:
```json
{
  "modifiers": [
    { "attribute": "slash", "operation": "addition", "value": 100 },

    { "attribute": "strike_resist", "operation": "addition", "value": -20 },
    { "attribute": "fire_resist", "operation": "addition", "value": -30 },
    { "attribute": "holy_resist", "operation": "addition", "value": -50 },
    { "attribute": "blood_resist", "operation": "addition", "value": 20 }
  ]
}
```

### A group of items (several hoes deal pierce damage)
`data/ews/groups/pierce_hoes.json`:
```json
{
  "ids": [
    "minecraft:wooden_hoe",
    "minecraft:stone_hoe",
    "minecraft:iron_hoe",
    "minecraft:diamond_hoe",
    "minecraft:netherite_hoe"
  ],
  "modifiers": [
    { "attribute": "pierce", "operation": "addition", "value": 100 }
  ]
}
```

## 6. `damage_types` (unchanged from before)

Defines the fixed elemental composition of a vanilla `DamageType` — for example, making lava always be 100% fire regardless of who/what caused the damage.

`data/ews/damage_types/minecraft/lava.json`:
```json
{
  "damage": {
    "fire": 100.0
  },
  "special": true
}
```

- **`damage`**: an object of `"element": value` pairs (the 13 attack names from section 1, without `_resist`). Anything not set is 0.
- **`special`**: if `true`, this elemental profile is **always** used for that `DamageType`, completely ignoring the attacker's own attributes (useful for damage without a meaningful attacker, like fall or lava, or to force a damage type to always be a given element). If `false`/omitted, it's only used when there's no living attacker.

## 7. General tips

- You can omit `modifiers`/`damage`/`ids` when not needed.
- You don't need to declare all 13 elements in every file — anything missing is 0.
- `slots` and the percentage operations (`multiply_base`/`multiply_total`) **only have an effect on items**; they're ignored on `entities` (mobs only use `addition`, as a fixed base value).
- If something doesn't work, check `run/logs/latest.log` — the mod logs a warning there if an `attribute` in your JSON couldn't be resolved (e.g. a typo).
