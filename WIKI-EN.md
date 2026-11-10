# EWS (Elemental Weakness System) Mod Documentation

## Contents

1. [The 13 elements](#1-the-13-elements)
2. [How damage is calculated](#2-how-damage-is-calculated)
3. [Data systems: which folder does what](#3-data-systems-which-folder-does-what)
4. [`groups` / `entities` / `items` format](#4-groups--entities--items-format)
5. [Basic examples](#5-basic-examples)
6. [`damage_types`: damage with no attacker or a fixed element](#6-damage_types-damage-with-no-attacker-or-a-fixed-element)
7. [Per-damage-type rules (`"damage_types"` inside an entity or item)](#7-per-damage-type-rules-damage_types-inside-an-entity-or-item)
8. [Finding the damage type of an attack (vanilla and other mods)](#8-finding-the-damage-type-of-an-attack-vanilla-and-other-mods)
9. [Full priority order](#9-full-priority-order)
10. [Mod configuration (`ews-common.toml`)](#10-mod-configuration-ews-commontoml)
11. [General tips and common mistakes](#11-general-tips-and-common-mistakes)

---

## 1. The 13 elements

Each element has **two** separate attributes: an **attack** one (what the attacker deals) and a **resistance** one (what the victim mitigates). The resistance id is always the attack id + `_resist`.

| Element     | Attack       | Resistance           |
|-------------|--------------|-----------------------|
| Slash       | `slash`      | `slash_resist`        |
| Strike      | `strike`     | `strike_resist`       |
| Pierce      | `pierce`     | `pierce_resist`       |
| Fire        | `fire`       | `fire_resist`         |
| Ice         | `ice`        | `ice_resist`          |
| Lightning   | `lightning`  | `lightning_resist`    |
| Aqua        | `aqua`       | `aqua_resist`         |
| Holy        | `holy`       | `holy_resist`         |
| Ender       | `ender`      | `ender_resist`        |
| Blood       | `blood`      | `blood_resist`        |
| Evocation   | `evocation`  | `evocation_resist`    |
| Nature      | `nature`     | `nature_resist`       |
| Eldritch    | `eldritch`   | `eldritch_resist`     |

## 2. How damage is calculated

When something hits an entity, the mod recalculates the damage element by element:

```
damage_of_that_element = original_damage × (attack_value_of_the_element / 100)
```

In other words, **an element's attack value is the percentage of the hit that becomes that damage type**. An attacker with `fire = 30` makes 30% of the hit fire damage.

Then that slice of damage is affected by the victim's **resistance** to the same element:

- Resistance between `0` and `80`: reduces that slice linearly (`resistance = 50` → half damage of that element). The maximum is 80, so there is never full immunity.
- Negative resistance (down to `-100`): **amplifies** that element (`resistance = -20` → +20% damage of that element, `-100` → double).

**Limits**: the final value of each attribute (after adding all equipment and modifiers) is clamped to these ranges: **attack `0` to `1000`**, **resistance `-100` to `80`**. Anything past the limit is ignored.

The 13 elements are calculated **independently** (each one against 100% of the original damage) and then **added together**. If the attack values of your 13 elements **add up to less than 100**, the difference is added separately as untyped damage, which **is not affected by any resistance** — so if you give a mob only `fire = 30`, the other 70% of the hit will ignore all of the victim's elemental resistances. Keep this in mind when balancing.

**Where do the attack and resistance values of each hit come from?** From the attacker's and the victim's *profile*. By default each entity has a single profile (section 4), but with **per-damage-type rules** (section 7) the same entity can have a different profile depending on *how* it attacks or *what* hits it. The exact order is in section 9.

## 3. Data systems: which folder does what

Everything goes inside your datapack's `data/ews/`:

| Folder | What it assigns | Format |
|---|---|---|
| `entities` | Elemental profile of a mob (vanilla or from any mod) | `modifiers` + optional `damage_types` (sections 4 and 7) |
| `items` | Elemental attributes an equipped item grants | `modifiers` + optional `damage_types` (sections 4 and 7) |
| `groups` | Same as `entities`/`items`, but for several ids at once | `ids` + `modifiers` + optional `damage_types` |
| `damage_types` | Fixed elemental makeup of a `DamageType` (lava, fall, poison…) when **there is no attacker**, or always if `special` | `damage` + `special` (section 6) |

> **Watch the name**: the `damage_types/` **folder** (section 6) and the `"damage_types"` **field** inside an entity/item file (section 7) are two different things.

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

- **Single file** (`entities/minecraft/zombie.json`, `items/minecraft/diamond_sword.json`): the id comes from the file path itself (`<folder_inside_entities_or_items>/<name>.json` → `<that_folder>:<name>`). No `"ids"` field needed. For a mob from another mod: `entities/<other_mod_id>/<mob_name>.json`.
- **Group file** (`groups/<whatever>.json`): the file name doesn't matter; you must list the ids explicitly in `"ids"`.

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
  ],
  "damage_types": []
}
```

- **`ids`** (only in `groups`, optional/omitted in single files): list of `"namespace:name"` ids it applies to.
- **`modifiers`**: the **default** profile (used for any attack/damage that has no rule of its own). Each modifier has:
  - **`attribute`** *(required)*: the target attribute. A short name (`"fire"`, `"nature_resist"`) assumes EWS's own element; you can also use the full id (`"ews:fire"`), or in theory one from any other mod (`"attributeslib:crit_chance"`).
  - **`operation`** *(optional, default `"addition"`)*: `"addition"`, `"multiply_base"` or `"multiply_total"` (see 4.3).
  - **`value`** *(required)*: a number. Its meaning depends on `operation`.
  - **`slots`** *(optional, only has an effect in `items`, ignored in `entities`)*: list with one or more of `head`, `chest`, `legs`, `feet`, `main`, `off`. If omitted, the modifier applies in whatever slot the item is equipped in. If set, it **only** applies when the item is in that specific slot.
- **`damage_types`** *(optional)*: rules that change the profile for certain damage types. See section 7. If you leave it out, the mod works exactly as always.
- **`weapon_mode`** *(optional, entities only, default `"add"`)*: how the mob's attack combines with the weapon it holds. See 4.5.

You don't need to list all 13 elements — anything not in `modifiers` is simply 0.

### 4.3 The three operations

- **`addition`**: adds a flat value to the attribute. For EWS elements, this is the "% of the hit" from section 2 — `{"attribute": "fire", "operation": "addition", "value": 100}` on a weapon means "100% of what this weapon hits for is fire".
- **`multiply_base`** and **`multiply_total`**: **percentage** bonuses (`value: 10` = +10%). Minecraft computes: `(base + sum of additions) × (1 + sum of multiply_base) × each multiply_total`.
  - **Important**: our 13 elements start at base `0`. A `multiply_base`/`multiply_total` on an attribute that nothing raised above `0` with `addition` **does nothing** (0 × anything = 0). They're meant to boost a value that **already exists** — for example, a helmet with `{"attribute": "pierce", "operation": "multiply_base", "value": 5}` only does something if the weapon you're attacking with already gives you some `pierce`.
  - `multiply_base` vs `multiply_total`: if several items have `multiply_base` on the same attribute, their percentages are **added** together before applying (two `+10%` = `+20%` total). With `multiply_total`, each one is applied **in a chain** on top of the previous result (two `+10%` = `+21%` total). Use `multiply_base` unless you specifically want the compound effect.

### 4.4 Priority: group vs. specific

If an item/entity matches both a `group` and its own file in `items`/`entities`, they are combined: if both define a modifier for the **same `attribute` + `operation`**, the specific one (`items`/`entities`) wins; the rest of both files' modifiers are kept. If two different **group** files target the same id, their modifiers are **added** to the list (they don't overwrite each other). For `damage_types` rules see 7.6. For `weapon_mode`, if both define it the specific one wins.

### 4.5 `weapon_mode`: how a mob combines with the weapon in its hand

By default, if a mob has an attack profile and also holds a weapon that grants elements, **they are added**: a wither skeleton with `blood 100` holding a sword with `slash 100` hits for `blood 100 + slash 100` (200% of the hit). `weapon_mode` changes that. It goes in the entity's file (`entities` or `groups`); it's ignored in `items`.

| `weapon_mode` | Nothing in hand | Weapon in hand |
|---|---|---|
| `"add"` (default) | Mob's profile | Mob's profile + weapon (may exceed 100) |
| `"share"` | Mob's profile | Mob's profile + weapon, and if the total exceeds 100 it's **scaled down proportionally** to 100 |
| `"weapon"` | Mob's profile | **Weapon only**: the mob's attack is ignored |

**`share` example: a wither skeleton that is blood, but with a sword is half blood, half slash.**
`data/ews/entities/minecraft/wither_skeleton.json`:
```json
{
  "modifiers": [ { "attribute": "blood", "value": 100 } ],
  "weapon_mode": "share"
}
```
`data/ews/items/minecraft/stone_sword.json`:
```json
{
  "modifiers": [ { "attribute": "slash", "value": 100 } ]
}
```
No sword: `blood 100`. With the stone sword: `blood 100 + slash 100 = 200` → scaled to `blood 50 + slash 50`. (In vanilla, wither skeletons **always** spawn with a stone sword.)

**`weapon` example: a zombie that deals slash bare-handed, but with a weapon deals only the weapon's elements.**
`data/ews/entities/minecraft/zombie.json`:
```json
{
  "modifiers": [ { "attribute": "slash", "value": 100 } ],
  "weapon_mode": "weapon"
}
```
Empty hand: `slash 100`. With a shovel granting `strike 100`: only `strike 100` (the zombie's `slash` doesn't count).

**Important details:**

- **What counts as a "weapon"**: the **main-hand** item, and only if it grants the mob some attack element via `addition` with a positive value (from its `items`/`groups` file or from that item's rule for that damage type). An item that isn't in the datapack — a torch, or a weapon from another mod you didn't configure — **doesn't count**: the mob keeps its profile. So a zombie holding a torch still deals `slash`.
- **Off hand, armor, potions and other mods** keep adding as always. With `share` they are also part of the split.
- **`share` only scales down**: if the total doesn't exceed 100 nothing changes (e.g. mob `blood 30` + weapon `slash 30` = 60 → stays as is, and the remaining 40% is still untyped damage).
- **`share` with uneven values**: it's proportional to what each side contributes. Mob `blood 100` + weapon `slash 50, fire 50` → `blood 50, slash 25, fire 25`. Mob `blood 100` + weapon `slash 200` → `blood 33.3, slash 66.7`.
- It only affects **attack**; resistances are unchanged.
- A `damage_types` rule can have its own `weapon_mode` (section 7.2), which replaces the entity's only for those damage types. E.g. make the zombie use `weapon` only for `mob_attack`.
- With `keep_bonuses: false` (7.4) the weapon isn't taken into account, so `weapon_mode` has no effect.
- It works for players too (`entities/minecraft/player.json`): for example, fists `strike 100` with `weapon_mode: "weapon"`, so that with a weapon only the weapon counts.

## 5. Basic examples

### Weapon that turns its damage into 100% fire (single file in `items`)
`data/ews/items/minecraft/blaze_rod_sword.json`:
```json
{
  "modifiers": [
    { "attribute": "fire", "operation": "addition", "value": 100 }
  ]
}
```

### Helmet granting +5% pierce damage, but only when worn on the head
```json
{
  "ids": ["mymod:precision_helmet"],
  "modifiers": [
    { "attribute": "pierce", "operation": "multiply_base", "value": 5, "slots": ["head"] }
  ]
}
```

### Entity with resistances/weaknesses (single file in `entities`)
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

### Item group (several hoes deal pierce damage)
`data/ews/groups/hoes_pierce.json`:
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

## 6. `damage_types`: damage with no attacker or a fixed element

Files in the **folder** `data/ews/damage_types/<namespace>/<damage_type>.json`. They define the fixed elemental makeup of a `DamageType` — for example, so lava is always 100% fire no matter who/what caused the damage.

`data/ews/damage_types/minecraft/lava.json`:
```json
{
  "damage": {
    "fire": 100.0
  },
  "special": true
}
```

- **`damage`**: object with `"element": value` pairs (the 13 attack names from section 1, without `_resist`). Anything you leave out is 0.
- **`special`**:
  - `false` or omitted: this profile is used **only when the damage has no living attacker** (lava, falling, poison, fire on the ground…). If there is an attacker, the attacker's profile is used.
  - `true`: this profile is **always** used for that `DamageType`, completely ignoring the attacker (and its rules).

The victim's **resistances** apply the same way in both cases (including its per-damage-type rules from section 7).

If a damage has no attacker **and** no file in this folder, EWS doesn't modify it.

## 7. Per-damage-type rules (`"damage_types"` inside an entity or item)

### 7.1 What it's for

By default an entity has a **single** elemental profile (`modifiers`). But many mobs attack in several ways: a witch throws potions (`indirect_magic` damage), a skeleton shoots arrows (`arrow`), a modded mob might have a bite (`mob_attack`) and a fire breath (a damage type of its own mod).

Rules let you say: *"this entity is 100% aqua by default, **but** when it deals magic damage it's 100% fire"*. The same works for resistances (*"resists magic damage more"*) and for items (*"this staff, when dealing magic damage, is ender"*).

**Everything is optional**: if a file has no `"damage_types"`, or no rule matches the damage, the default profile (`modifiers`) is used as always.

### 7.2 Structure

```json
{
  "modifiers": [
    { "attribute": "aqua", "value": 100 }
  ],
  "damage_types": [
    {
      "types": ["minecraft:magic", "minecraft:indirect_magic"],
      "mode": "replace",
      "keep_bonuses": true,
      "modifiers": [
        { "attribute": "fire", "value": 100 }
      ]
    }
  ]
}
```

Each rule (each object inside `"damage_types"`) has:

| Field | Required | Default | What it does |
|---|---|---|---|
| `types` | yes | — | List of damage types the rule applies to. Either an id (`"minecraft:magic"`, or just `"magic"` which assumes `minecraft:`) or a **tag** with `#` (`"#minecraft:is_fire"`). See section 8 to find ids. |
| `mode` | no | `"replace"` | `"replace"`: the rule **replaces** the default profile. `"add"`: the rule **is added** on top of the default profile. See 7.3. |
| `keep_bonuses` | no | `true` | Only for mob `entities`/`groups`. `true`: equipment, potion effects and other mods' modifiers still stack on top of the rule. `false`: the rule alone decides. See 7.4. Ignored in `items`. |
| `weapon_mode` | no | the entity's | Only for `entities`/`groups`. Replaces the entity's `weapon_mode` (4.5) only for these damage types. A rule can have **only** `weapon_mode` and no `modifiers`: then it doesn't change the profile, only how it combines with the weapon. |
| `modifiers` | no | `[]` | Same format as section 4's `modifiers`. You can mix **attack** and **resistance** attributes in the same rule. Only EWS attributes count (the 13 elements and their `_resist`); anything else is ignored with a warning in the log. |

**Attack and resistance in the same rule:** a rule's attack attributes (`fire`, `slash`…) are used when the entity/item **deals** damage of that type; its resistance attributes (`fire_resist`…) when it **receives** it. A rule with only resistances doesn't touch attack, and vice versa.

### 7.3 `mode`: `replace` vs `add`

A witch with default profile `aqua 100` and a rule for magic damage with `fire 100`:

| `mode` | Witch's magic hit | Non-magic hit |
|---|---|---|
| `"replace"` (default) | `fire 100` | `aqua 100` |
| `"add"` | `aqua 100` + `fire 100` | `aqua 100` |

**How `replace` works depending on the attribute kind:**

- **Attack**: the **whole** attack profile is replaced. Anything the rule doesn't name becomes 0. (This makes sense because attack is a % split of the hit: otherwise the witch would deal `aqua 100 + fire 100`.)
- **Resistances**: **only the ones the rule names** are replaced; the others are kept.

Resistance example: a zombie with `fire_resist 20`, `holy_resist -50`, `aqua_resist 10`, and a rule for magic damage with only `fire_resist 50`. When it takes magic damage it has: `fire_resist 50`, `holy_resist -50`, `aqua_resist 10` (only fire changed; it's still weak to holy).

With `"add"`, the rule's values are added to the default profile instead (`fire_resist 20 + 50 = 70`).

### 7.4 `keep_bonuses`: equipment, potions and other mods

This applies to rules in `entities`/`groups`. Example:

- `entities/minecraft/zombie.json`: default `slash 100`, rule for `mob_attack` with `strike 100`.
- `items/minecraft/diamond_sword.json`: `fire 50`.
- The zombie spawns holding a diamond sword and hits (`mob_attack`):

| `keep_bonuses` | Result |
|---|---|
| `true` (default) | The rule replaces only what the zombie has **as its base**; the sword still adds on top: `strike 100` + `fire 50` |
| `false` | The rule alone decides; the sword, potion effects and other mods' modifiers are ignored: `strike 100` |

With `keep_bonuses: false`, rules of the items it has equipped are also ignored for that category (attack or resistance).

### 7.5 Rules on items

In `items` (or item `groups`) rules change **only what that item contributes**, and only while the entity wearing/holding it deals/takes that damage type. The rest of the equipment is untouched.

- **Weapon / held item**: its attack attributes are used when its holder **deals** damage of that type.
- **Armor**: its resistances are used when its wearer **takes** damage of that type.
- In item rules all three operations (`addition`, `multiply_base`, `multiply_total`) and `slots` work, just like in section 4. `keep_bonuses` is ignored.
- `replace` on items: for attack, **all** attack attributes that item granted are removed and the rule's are used instead; for resistances, only the ones the rule names are replaced.

Example — a staff from another mod whose spells are ender, but whose melee hits are strike:

`data/ews/items/othermod/arcane_staff.json`:
```json
{
  "modifiers": [
    { "attribute": "strike", "value": 100 }
  ],
  "damage_types": [
    {
      "types": ["minecraft:magic", "minecraft:indirect_magic", "othermod:arcane_spell"],
      "modifiers": [ { "attribute": "ender", "value": 100 } ]
    }
  ]
}
```

Example — a chestplate that resists fire only against fire damage (lava, fire, fireballs), using a tag:

`data/ews/items/minecraft/netherite_chestplate.json`:
```json
{
  "modifiers": [
    { "attribute": "slash_resist", "value": 10 }
  ],
  "damage_types": [
    {
      "types": ["#minecraft:is_fire"],
      "mode": "add",
      "modifiers": [ { "attribute": "fire_resist", "value": 30 } ]
    }
  ]
}
```

### 7.6 Which rule wins if several match

For a given hit, **at most one rule** for attack and one for resistance is applied from each entity (and from each equipped item). It is chosen like this:

1. A rule naming the **exact id** of the damage type beats one that only matches through a **tag**.
2. With equal precision, the **specific file** (`entities`/`items`) beats the **group**.
3. If still tied, the one that comes **first** in the list wins.

Only rules that have something of that category are considered: if the `magic` rule only has `fire_resist`, when **attacking** with magic the other rules are still searched (and if none match, the default profile is used).

### 7.7 Complete examples

**Witch: aqua by default, fire with her potions.**
`data/ews/entities/minecraft/witch.json`:
```json
{
  "modifiers": [
    { "attribute": "aqua", "value": 100 },
    { "attribute": "holy_resist", "value": -30 }
  ],
  "damage_types": [
    {
      "types": ["minecraft:indirect_magic", "minecraft:magic"],
      "modifiers": [ { "attribute": "fire", "value": 100 } ]
    }
  ]
}
```

**Skeleton: slash in melee, pierce with arrows (by tag, also covers projectiles from other mods).**
`data/ews/entities/minecraft/skeleton.json`:
```json
{
  "modifiers": [
    { "attribute": "slash", "value": 100 }
  ],
  "damage_types": [
    {
      "types": ["#minecraft:is_projectile"],
      "modifiers": [ { "attribute": "pierce", "value": 100 } ]
    }
  ]
}
```

**A mob from another mod with several attacks.** Say a dragon from `othermod` bites (`minecraft:mob_attack`), breathes fire (`othermod:fire_breath`) and casts lightning (`othermod:lightning_strike`). First find the real ids with section 8.
`data/ews/entities/othermod/dragon.json`:
```json
{
  "modifiers": [
    { "attribute": "slash", "value": 60 },
    { "attribute": "blood", "value": 40 },
    { "attribute": "fire_resist", "value": 50 },
    { "attribute": "ice_resist", "value": -50 }
  ],
  "damage_types": [
    {
      "types": ["othermod:fire_breath"],
      "modifiers": [ { "attribute": "fire", "value": 100 } ]
    },
    {
      "types": ["othermod:lightning_strike"],
      "modifiers": [
        { "attribute": "lightning", "value": 80 },
        { "attribute": "holy", "value": 20 }
      ]
    }
  ]
}
```

**A victim that resists magic (per-damage-type resistances).**
`data/ews/entities/minecraft/evoker.json`:
```json
{
  "modifiers": [
    { "attribute": "evocation", "value": 100 },
    { "attribute": "fire_resist", "value": 10 }
  ],
  "damage_types": [
    {
      "types": ["#minecraft:witch_resistant_to"],
      "modifiers": [
        { "attribute": "fire_resist", "value": 60 },
        { "attribute": "evocation_resist", "value": 60 }
      ]
    }
  ]
}
```
When the evoker takes magic damage, `fire_resist` goes from 10 to 60 and `evocation_resist` to 60; the rest of its resistances stay the same.

**A group with rules.** Rules can also go in `groups`; they apply to every id in `ids`:
`data/ews/groups/undead.json`:
```json
{
  "ids": ["minecraft:zombie", "minecraft:husk", "minecraft:drowned"],
  "modifiers": [
    { "attribute": "holy_resist", "value": -50 }
  ],
  "damage_types": [
    {
      "types": ["#minecraft:is_fire"],
      "modifiers": [ { "attribute": "fire_resist", "value": -30 } ]
    }
  ]
}
```

## 8. Finding the damage type of an attack (vanilla and other mods)

### 8.1 With debug mode (the easiest way, works with any mod)

1. Open `config/ews-common.toml` and set `debug_info = true` (and `debug_damage = true` if you also want the numbers).
2. Join the world and let the mob hit you (or hit it yourself).
3. The chat shows `Damage Type: <id>` (that's the id that goes in `types`), `Attacker: <mob>`, and which profile was used (`Attack profile` / `Resist profile`: `default`, or the rule that was applied).

If the damage has neither an attacker nor a file in the `damage_types` folder, you'll see `No data found for the damage type …` (only with `debug_info`).

### 8.2 Who counts as the "attacker"

The attacker is the entity that **owns** the damage, not the projectile: for an arrow it's the skeleton that shot it; for a thrown potion, the witch. That's why rules go in the file of the mob that shoots/throws. (If the damage comes directly from another living entity, that one is used.)

### 8.3 Most common vanilla damage types for mobs

| Damage type | Used by |
|---|---|
| `minecraft:mob_attack` | Melee hits from almost every mob, the dragon's bite and wing hits, second hit of the guardian's laser |
| `minecraft:player_attack` | Melee hits from players |
| `minecraft:arrow` | Arrows (skeletons, pillagers, players with bow/crossbow) |
| `minecraft:trident` | Thrown tridents (drowned, players) |
| `minecraft:mob_projectile` | Mob projectiles such as llama spit or shulker bullets |
| `minecraft:fireball` | Blaze and ghast fireballs |
| `minecraft:wither_skull` | Wither skulls |
| `minecraft:indirect_magic` | Witch's thrown potion, evoker fangs, **Ender Dragon's breath** (`minecraft:dragon_breath` exists but vanilla doesn't use it), first hit of the guardian's laser |
| `minecraft:magic` | Magic effects (e.g. instant damage, poison) — poison **has no attacker** |
| `minecraft:sonic_boom` | Warden's sonic attack |
| `minecraft:player_explosion` | Explosions caused by an entity: creeper, Wither and its skulls, TNT lit by a player |
| `minecraft:explosion` | Explosions with no responsible entity (e.g. redstone-triggered TNT) |
| `minecraft:sting` | Bee sting |
| `minecraft:thorns` | Thorns, the guardian's spikes when you hit it |

A single attack can deal **more than one** damage type: the guardian's laser hits with `indirect_magic` and right after with `mob_attack`. Each part uses its own rule (or the default profile if it has none).

The repo includes a test datapack covering all these cases in `datapack-prueba/EWS-Prueba`.

**Careful with the witch:** her harming potion hits as `indirect_magic` (with the witch as attacker), but the **poison** she leaves deals `magic` damage **with no attacker**, so that damage can't be attributed to the witch: use the `damage_types` folder (section 6) for poison.

### 8.4 Useful tags

Tags group several damage types, including ones from other mods that get added to the tag:

| Tag | Includes |
|---|---|
| `#minecraft:is_projectile` | Arrows, tridents, fireballs, mob projectiles… |
| `#minecraft:is_fire` | Fire, lava, fireballs… |
| `#minecraft:is_explosion` | Explosions |
| `#minecraft:is_fall` | Falling |
| `#minecraft:is_freezing` | Freezing in powder snow |
| `#minecraft:is_lightning` | Lightning |
| `#minecraft:witch_resistant_to` | `magic`, `indirect_magic`, `sonic_boom`, `thorns` |

Mods may ship their own tags; look for them in the mod's `.jar` under `data/<modid>/tags/damage_type/`. A mod's damage types are in `data/<modid>/damage_type/`.

## 9. Full priority order

For each hit:

**Attack profile** (where each element's % comes from):

1. If the `damage_types` folder has that damage type with `special: true` → that fixed profile is used.
2. If there is a living attacker → its profile:
   1. The entity's rule (`entities`/`groups`) for that damage type, if any (section 7.6). Otherwise its default profile (`modifiers`).
   2. Plus equipment, potion effects and other mods' modifiers (unless `keep_bonuses: false`), where each equipped item with a rule for that damage type contributes its rule's values instead of its normal ones.
   3. `weapon_mode` (4.5; the rule's if it has one, otherwise the entity's): if the main-hand weapon grants elements, `weapon` discards the profile from step 2.1 and `share` scales everything down to 100 if it goes over.
3. If there is no attacker → profile from the `damage_types` folder (non-`special`). If there's none either, EWS leaves the damage alone.

**Victim's resistances**: always its profile (the entity's rule for that damage type or, if none, the default) + its equipment (with each piece's rules).

## 10. Mod configuration (`ews-common.toml`)

File `config/ews-common.toml`:

| Option | Default | What it does |
|---|---|---|
| `detalles` | `false` | In Jade, shows the attribute name next to the number (not just the icon). |
| `debug_damage` | `false` | Shows in chat the calculation of each hit: initial damage, damage per element (only the non-zero ones) and final damage. |
| `debug_info` | `false` | Shows in chat the data of each hit: damage type, attacker/target and which profile/rule was used (including `weapon_mode`, e.g. `minecraft:wither_skeleton [weapon_mode=share, x0.50]`). Both can be enabled at once. |
| `mostrar_reglas_damage_type` | `true` | Shows per-damage-type rules in Jade (`On magic damage:` below the mob's damage/resistances) and in item tooltips. Set it to `false` to hide them (the rules still work). |

In Jade, without holding the key you see the default damage and the **attack** rules; holding the "Show Defenses" key you see resistances/weaknesses and the **resistance** rules.

## 11. General tips and common mistakes

- You can omit `modifiers`/`damage`/`ids`/`damage_types` if you don't need them.
- You don't need to declare all 13 elements in each file — anything missing is 0.
- `slots` and the percentage operations (`multiply_base`/`multiply_total`) **only have an effect on items**; in `entities` (including their rules) they are ignored: mobs only use `addition`.
- **A mob holding a weapon hits twice as hard as expected**: its profile and the weapon's are being added. Use `weapon_mode: "share"` or `"weapon"` (4.5).
- **If a rule's attack values add up to less than 100**, the rest of the hit is untyped damage (section 2), same as with the default profile.
- **The rule isn't applied**: with `debug_info = true`, check that the id in `types` is exactly what the chat shows (including the mod's namespace), and that the rule has attributes of the right category (attack for when the mob hits, `_resist` for when it gets hit).
- **Loading error**: if a JSON is malformed (e.g. `"mode": "sum"` or a rule without `types`), that part is not loaded and the error shows up in `logs/latest.log` (`Error loading <file>: …`). It also warns there if an `attribute` doesn't exist (typo). Always check the log when something has no effect.
- Datapack changes are applied with `/reload` on the server. Jade and item tooltips (client side) receive the data when **joining the world**: after a `/reload`, leave and rejoin to see them updated.
- Mobs that were already spawned keep the default profile they had when they spawned; per-damage-type rules, on the other hand, are read on every hit.
