# Documentación del Mod EWS (Elemental Weakness System)

## Índice

1. [Los 13 elementos](#1-los-13-elementos)
2. [Cómo funciona el cálculo de daño](#2-cómo-funciona-el-cálculo-de-daño)
3. [Sistemas de datos: cuál usa cada carpeta](#3-sistemas-de-datos-cuál-usa-cada-carpeta)
4. [Formato de `groups` / `entities` / `items`](#4-formato-de-groups--entities--items)
5. [Ejemplos básicos](#5-ejemplos-básicos)
6. [`damage_types`: daño sin atacante o con elemento fijo](#6-damage_types-daño-sin-atacante-o-con-elemento-fijo)
7. [Reglas por damage type (`"damage_types"` dentro de una entidad o ítem)](#7-reglas-por-damage-type-damage_types-dentro-de-una-entidad-o-ítem)
8. [Cómo encontrar el damage type de un ataque (vanilla y otros mods)](#8-cómo-encontrar-el-damage-type-de-un-ataque-vanilla-y-otros-mods)
9. [Orden de prioridad completo](#9-orden-de-prioridad-completo)
10. [Configuración del mod (`ews-common.toml`)](#10-configuración-del-mod-ews-commontoml)
11. [Tips generales y errores comunes](#11-tips-generales-y-errores-comunes)

---

## 1. Los 13 elementos

Cada elemento tiene **dos** atributos separados: uno de **ataque** (lo que el atacante inflige) y uno de **resistencia** (lo que la víctima mitiga). El id de resistencia es siempre el del ataque + `_resist`.

| Elemento    | Ataque       | Resistencia          |
|-------------|--------------|-----------------------|
| Corte       | `slash`      | `slash_resist`        |
| Impacto     | `strike`     | `strike_resist`       |
| Perforante  | `pierce`     | `pierce_resist`       |
| Fuego       | `fire`       | `fire_resist`         |
| Hielo       | `ice`        | `ice_resist`          |
| Eléctrico   | `lightning`  | `lightning_resist`    |
| Agua        | `aqua`       | `aqua_resist`         |
| Sagrado     | `holy`       | `holy_resist`         |
| Ender       | `ender`      | `ender_resist`        |
| Sangre      | `blood`      | `blood_resist`        |
| Evocación   | `evocation`  | `evocation_resist`    |
| Naturaleza  | `nature`     | `nature_resist`       |
| Primigenio  | `eldritch`   | `eldritch_resist`     |

## 2. Cómo funciona el cálculo de daño

Cuando algo golpea a una entidad, el mod recalcula el daño elemento por elemento:

```
daño_de_ese_elemento = daño_original × (valor_de_ataque_del_elemento / 100)
```

Es decir, **el valor de ataque de un elemento es el porcentaje del golpe que se convierte en ese tipo de daño**. Un atacante con `fire = 30` hace que el 30 % del golpe sea daño de fuego.

Después, ese trozo de daño se ve afectado por la **resistencia** de la víctima a ese mismo elemento:

- Resistencia entre `0` y `80`: reduce ese porcentaje linealmente (`resistencia = 50` → mitad de daño de ese elemento). El máximo es 80, así que nunca hay inmunidad total.
- Resistencia negativa (hasta `-100`): **amplifica** ese elemento (`resistencia = -20` → +20 % de daño de ese elemento, `-100` → el doble).

**Límites**: el valor final de cada atributo (después de sumar todo el equipo y los modificadores) se recorta a estos rangos: **ataque `0` a `1000`**, **resistencia `-100` a `80`**. Lo que se pase del límite no cuenta.

Los 13 elementos se calculan de forma **independiente** (cada uno contra el 100 % del daño original) y después se **suman**. Si sumás los valores de ataque de tus 13 elementos y **no llegan a 100**, la diferencia se agrega aparte como daño sin tipo, que **no pasa por ninguna resistencia** — así que si a un mob solo le ponés `fire = 30` y nada más, el otro 70 % del golpe va a ignorar todas las resistencias elementales de la víctima. Tenelo en cuenta al balancear.

**¿De dónde salen los valores de ataque y resistencia de cada golpe?** Del *perfil* del atacante y de la víctima. Por defecto es uno solo por entidad (sección 4), pero con las **reglas por damage type** (sección 7) una misma entidad puede tener un perfil distinto según *cómo* ataca o *qué* la golpea. El orden exacto está en la sección 9.

## 3. Sistemas de datos: cuál usa cada carpeta

Todo va dentro de `data/ews/` de tu datapack:

| Carpeta | Qué asigna | Formato |
|---|---|---|
| `entities` | Perfil elemental de un mob (vanilla o de cualquier mod) | `modifiers` + `damage_types` opcional (secciones 4 y 7) |
| `items` | Atributos elementales que da un ítem equipado | `modifiers` + `damage_types` opcional (secciones 4 y 7) |
| `groups` | Lo mismo que `entities`/`items`, pero para varios ids a la vez | `ids` + `modifiers` + `damage_types` opcional |
| `damage_types` | Composición elemental fija de un `DamageType` (lava, caída, veneno…) cuando **no hay atacante**, o siempre si es `special` | `damage` + `special` (sección 6) |

> **Ojo con el nombre**: la **carpeta** `damage_types/` (sección 6) y el **campo** `"damage_types"` dentro de un archivo de entidad/ítem (sección 7) son dos cosas distintas.

## 4. Formato de `groups` / `entities` / `items`

### 4.1 Ubicación de los archivos

```
<tu_datapack>/
├── data/
│   └── ews/
│       ├── groups/
│       │   └── <cualquier_nombre>.json
│       ├── entities/
│       │   └── <modid>/
│       │       └── <entity_nombre>.json
│       └── items/
│           └── <modid>/
│               └── <item_nombre>.json
└── pack.mcmeta
```

Dos formas de nombrar a quién le aplica un archivo:

- **Archivo individual** (`entities/minecraft/zombie.json`, `items/minecraft/diamond_sword.json`): el id se saca del propio path del archivo (`<carpeta_dentro_de_entities_o_items>/<nombre>.json` → `<esa_carpeta>:<nombre>`). No hace falta el campo `"ids"`. Para un mob de otro mod: `entities/<modid_del_otro_mod>/<nombre_del_mob>.json`.
- **Archivo de grupo** (`groups/<lo_que_quieras>.json`): el nombre del archivo no importa, tenés que listar explícitamente los ids en `"ids"`.

### 4.2 Estructura del JSON

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

- **`ids`** (solo en `groups`, opcional/omitido en archivos individuales): lista de ids `"namespace:nombre"` a los que aplica.
- **`modifiers`**: el perfil **por defecto** (se usa para cualquier ataque/daño que no tenga una regla propia). Cada modificador tiene:
  - **`attribute`** *(obligatorio)*: el atributo al que apunta. Nombre corto (`"fire"`, `"nature_resist"`) asume el elemento propio de EWS; también podés poner el id completo (`"ews:fire"`), o en teoría el de cualquier otro mod (`"attributeslib:crit_chance"`).
  - **`operation`** *(opcional, default `"addition"`)*: `"addition"`, `"multiply_base"` o `"multiply_total"` (ver 4.3).
  - **`value`** *(obligatorio)*: número. Su significado depende de `operation`.
  - **`slots`** *(opcional, solo tiene efecto en `items`, se ignora en `entities`)*: lista con uno o más de `head`, `chest`, `legs`, `feet`, `main`, `off`. Si no lo ponés, el modificador aplica en cualquier slot donde esté puesto el ítem. Si lo ponés, **solo** aplica cuando el ítem está en ese slot específico.
- **`damage_types`** *(opcional)*: reglas que cambian el perfil para ciertos damage types. Ver sección 7. Si no lo ponés, el mod funciona exactamente igual que siempre.
- **`weapon_mode`** *(opcional, solo entidades, default `"add"`)*: cómo se combina el ataque del mob con el arma que tiene en la mano. Ver 4.5.

No hace falta listar los 13 elementos — el que no aparece en `modifiers` simplemente vale 0.

### 4.3 Las tres operaciones

- **`addition`**: suma un valor plano al atributo. Para los elementos de EWS, este es el "% del golpe" del que habla la sección 2 — `{"attribute": "fire", "operation": "addition", "value": 100}` en un arma significa "el 100 % de lo que pegue esa arma es fuego".
- **`multiply_base`** y **`multiply_total`**: bonos **porcentuales** (`value: 10` = +10 %). Minecraft calcula: `(base + suma de additions) × (1 + suma de multiply_base) × cada multiply_total`.
  - **Importante**: nuestros 13 elementos arrancan en base `0`. Un `multiply_base`/`multiply_total` sobre un atributo que nadie puso en `0` con `addition` **no hace nada** (0 × cualquier cosa = 0). Sirven para potenciar un valor que **ya existe** — por ejemplo, un casco con `{"attribute": "pierce", "operation": "multiply_base", "value": 5}` solo hace algo si el arma con la que atacás ya te da algo de `pierce` por su cuenta.
  - `multiply_base` vs `multiply_total`: si tenés varios ítems con `multiply_base` sobre el mismo atributo, sus porcentajes se **suman** entre sí antes de aplicarse (dos `+10%` = `+20%` total). Con `multiply_total`, cada uno se aplica **en cadena** sobre el resultado del anterior (dos `+10%` = `+21%` total). Usá `multiply_base` salvo que quieras específicamente el efecto compuesto.

### 4.4 Prioridad: grupo vs. específico

Si un ítem/entidad matchea tanto un `group` como su propio archivo en `items`/`entities`, se combinan: si ambos definen un modificador para el **mismo `attribute` + `operation`**, gana el específico (`items`/`entities`); el resto de los modificadores de ambos se conserva. Si dos archivos de **grupo** distintos apuntan al mismo id, sus modificadores se **suman** en la lista (no se pisan). Para las reglas de `damage_types` ver 7.6. Para `weapon_mode`, si ambos lo definen gana el específico.

### 4.5 `weapon_mode`: cómo se combina el mob con el arma de la mano

Por defecto, si un mob tiene un perfil de ataque y además empuña un arma que da elementos, **se suman**: un wither skeleton con `blood 100` y una espada con `slash 100` pega `blood 100 + slash 100` (el 200 % del golpe). `weapon_mode` cambia eso. Va en el archivo de la entidad (`entities` o `groups`); en `items` se ignora.

| `weapon_mode` | Sin arma en la mano | Con arma en la mano |
|---|---|---|
| `"add"` (default) | Perfil del mob | Perfil del mob + arma (puede pasar de 100) |
| `"share"` | Perfil del mob | Perfil del mob + arma, y si el total pasa de 100 se **reparte proporcionalmente** hasta 100 |
| `"weapon"` | Perfil del mob | **Solo el arma**: el ataque del mob se ignora |

**Ejemplo `share`: wither skeleton que es sangre, pero con espada mitad sangre y mitad corte.**
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
Sin espada: `blood 100`. Con la espada de piedra: `blood 100 + slash 100 = 200` → se escala a `blood 50 + slash 50`. (En vanilla el wither skeleton **siempre** aparece con espada de piedra.)

**Ejemplo `weapon`: zombie que a mano limpia hace corte, pero con un arma hace solo lo del arma.**
`data/ews/entities/minecraft/zombie.json`:
```json
{
  "modifiers": [ { "attribute": "slash", "value": 100 } ],
  "weapon_mode": "weapon"
}
```
Sin nada en la mano: `slash 100`. Con una pala que da `strike 100`: solo `strike 100` (el `slash` del zombie no cuenta).

**Detalles importantes:**

- **Qué cuenta como "arma"**: el ítem de la **mano principal**, y solo si le da al mob algún elemento de ataque con `addition` y valor positivo (por su archivo de `items`/`groups` o por la regla de ese ítem para ese damage type). Un ítem que no está en el datapack —una antorcha, o un arma de otro mod que no configuraste— **no cuenta**: el mob sigue con su perfil. Así un zombie con una antorcha sigue haciendo `slash`.
- **Mano secundaria, armadura, pociones y otros mods** siguen sumando como siempre. En `share` también entran en el reparto.
- **`share` solo achica**: si el total no pasa de 100 no se toca nada (ej. mob `blood 30` + arma `slash 30` = 60 → queda así, y el 40 % restante sigue siendo daño sin tipo).
- **`share` con valores desiguales**: es proporcional a lo que aporta cada uno. Mob `blood 100` + arma `slash 50, fire 50` → `blood 50, slash 25, fire 25`. Mob `blood 100` + arma `slash 200` → `blood 33.3, slash 66.7`.
- Solo afecta al **ataque**; las resistencias no cambian.
- Una regla de `damage_types` puede tener su propio `weapon_mode` (sección 7.2), que reemplaza al de la entidad solo para esos damage types. Ej.: que el zombie use `weapon` solo para `mob_attack`.
- Con `keep_bonuses: false` (7.4) el arma no se tiene en cuenta, así que `weapon_mode` no tiene efecto.
- También sirve para jugadores (`entities/minecraft/player.json`): por ejemplo, puños `strike 100` con `weapon_mode: "weapon"`, para que con un arma cuente solo el arma.

## 5. Ejemplos básicos

### Arma que convierte su daño en 100 % fuego (archivo individual en `items`)
`data/ews/items/minecraft/blaze_rod_sword.json`:
```json
{
  "modifiers": [
    { "attribute": "fire", "operation": "addition", "value": 100 }
  ]
}
```

### Casco que da +5 % de daño perforante, pero solo puesto en la cabeza
```json
{
  "ids": ["mimod:casco_de_precision"],
  "modifiers": [
    { "attribute": "pierce", "operation": "multiply_base", "value": 5, "slots": ["head"] }
  ]
}
```

### Entidad con resistencias/debilidades (archivo individual en `entities`)
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

### Grupo de ítems (varias azadas dan daño perforante)
`data/ews/groups/azadas_pierce.json`:
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

## 6. `damage_types`: daño sin atacante o con elemento fijo

Archivos en la **carpeta** `data/ews/damage_types/<namespace>/<damage_type>.json`. Definen la composición elemental fija de un `DamageType` — por ejemplo, para que la lava siempre sea 100 % fuego sin importar quién/qué causó el daño.

`data/ews/damage_types/minecraft/lava.json`:
```json
{
  "damage": {
    "fire": 100.0
  },
  "special": true
}
```

- **`damage`**: objeto con pares `"elemento": valor` (los 13 nombres de ataque de la sección 1, sin `_resist`). Los que no pongas valen 0.
- **`special`**:
  - `false` u omitido: este perfil se usa **solo cuando el daño no tiene atacante viviente** (lava, caída, veneno, fuego en el suelo…). Si hay atacante, manda el perfil del atacante.
  - `true`: este perfil se usa **siempre** para ese `DamageType`, ignorando por completo al atacante (y sus reglas).

Las **resistencias** de la víctima se aplican igual en ambos casos (incluidas sus reglas por damage type de la sección 7).

Si un daño no tiene atacante **y** no tiene archivo en esta carpeta, EWS no lo modifica.

## 7. Reglas por damage type (`"damage_types"` dentro de una entidad o ítem)

### 7.1 Para qué sirve

Por defecto una entidad tiene **un solo** perfil elemental (`modifiers`). Pero muchos mobs atacan de varias formas: una bruja tira pociones (daño `indirect_magic`), un esqueleto pega con flechas (`arrow`), un mob de otro mod puede tener un mordisco (`mob_attack`) y un aliento de fuego (un damage type propio de ese mod).

Las reglas te permiten decir: *"esta entidad por defecto es 100 % agua, **pero** cuando hace daño mágico es 100 % fuego"*. Lo mismo sirve para resistencias (*"resiste más el daño mágico"*) y para ítems (*"este báculo, cuando hace daño mágico, es ender"*).

**Todo es opcional**: si un archivo no tiene `"damage_types"`, o ninguna regla coincide con el daño, se usa el perfil por defecto (`modifiers`) como siempre.

### 7.2 Estructura

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

Cada regla (cada objeto dentro de `"damage_types"`) tiene:

| Campo | Obligatorio | Default | Qué hace |
|---|---|---|---|
| `types` | sí | — | Lista de damage types a los que aplica la regla. Puede ser un id (`"minecraft:magic"`, o solo `"magic"` que asume `minecraft:`) o un **tag** con `#` (`"#minecraft:is_fire"`). Ver sección 8 para encontrar ids. |
| `mode` | no | `"replace"` | `"replace"`: la regla **reemplaza** al perfil por defecto. `"add"`: la regla **se suma** al perfil por defecto. Ver 7.3. |
| `keep_bonuses` | no | `true` | Solo para `entities`/`groups` de mobs. `true`: el equipo, pociones y modificadores de otros mods siguen sumando encima de la regla. `false`: la regla manda sola. Ver 7.4. En `items` se ignora. |
| `weapon_mode` | no | el de la entidad | Solo para `entities`/`groups`. Reemplaza el `weapon_mode` de la entidad (4.5) solo para estos damage types. Una regla puede tener **solo** `weapon_mode` sin `modifiers`: entonces no cambia el perfil, solo cómo se combina con el arma. |
| `modifiers` | no | `[]` | Mismo formato que el `modifiers` de la sección 4. Podés mezclar atributos de **ataque** y de **resistencia** en la misma regla. Solo cuentan los atributos de EWS (los 13 elementos y sus `_resist`); cualquier otro se ignora y se avisa en el log. |

**Ataque y resistencia en la misma regla:** los atributos de ataque (`fire`, `slash`…) de una regla se usan cuando la entidad/ítem **hace** daño de ese tipo; los de resistencia (`fire_resist`…) cuando lo **recibe**. Una regla que solo tiene resistencias no toca el ataque, y viceversa.

### 7.3 `mode`: `replace` vs `add`

Bruja con perfil por defecto `aqua 100` y una regla para daño mágico con `fire 100`:

| `mode` | Golpe mágico de la bruja | Golpe no mágico |
|---|---|---|
| `"replace"` (default) | `fire 100` | `aqua 100` |
| `"add"` | `aqua 100` + `fire 100` | `aqua 100` |

**Cómo reemplaza `replace` según el tipo de atributo:**

- **Ataque**: se reemplaza **todo** el perfil de ataque. Lo que la regla no nombra queda en 0. (Tiene sentido porque el ataque es un reparto en % del golpe: si no, la bruja haría `aqua 100 + fire 100`.)
- **Resistencias**: se reemplazan **solo las que la regla nombra**; las demás se mantienen.

Ejemplo de resistencias: un zombie con `fire_resist 20`, `holy_resist -50`, `aqua_resist 10`, y una regla para daño mágico con solo `fire_resist 50`. Cuando recibe daño mágico queda: `fire_resist 50`, `holy_resist -50`, `aqua_resist 10` (solo cambió el fuego; sigue siendo débil a holy).

Con `"add"`, en cambio, los valores de la regla se suman a los del perfil por defecto (`fire_resist 20 + 50 = 70`).

### 7.4 `keep_bonuses`: equipo, pociones y otros mods

Esto aplica a reglas en `entities`/`groups`. Ejemplo:

- `entities/minecraft/zombie.json`: por defecto `slash 100`, regla para `mob_attack` con `strike 100`.
- `items/minecraft/diamond_sword.json`: `fire 50`.
- El zombie spawnea con espada de diamante y pega (`mob_attack`):

| `keep_bonuses` | Resultado |
|---|---|
| `true` (default) | La regla reemplaza solo lo que el zombie trae **de base**; la espada sigue sumando: `strike 100` + `fire 50` |
| `false` | La regla manda sola; se ignoran espada, pociones y modificadores de otros mods: `strike 100` |

Con `keep_bonuses: false` también se ignoran las reglas de los ítems que tenga equipados para esa categoría (ataque o resistencia).

### 7.5 Reglas en ítems

En `items` (o `groups` de ítems) las reglas cambian **solo lo que aporta ese ítem**, y solo mientras la entidad que lo tiene equipado hace/recibe ese damage type. El resto del equipo no se toca.

- **Arma / ítem en la mano**: sus atributos de ataque se usan cuando quien lo lleva **hace** daño de ese tipo.
- **Armadura**: sus resistencias se usan cuando quien la lleva **recibe** daño de ese tipo.
- En reglas de ítems funcionan las tres operaciones (`addition`, `multiply_base`, `multiply_total`) y `slots`, igual que en la sección 4. `keep_bonuses` se ignora.
- `replace` en ítems: para ataque, se quitan **todos** los atributos de ataque que daba ese ítem y se ponen los de la regla; para resistencias, solo se reemplazan las que la regla nombra.

Ejemplo — un báculo de otro mod cuyos hechizos son ender, pero que a melee pega como impacto:

`data/ews/items/otromod/baculo_arcano.json`:
```json
{
  "modifiers": [
    { "attribute": "strike", "value": 100 }
  ],
  "damage_types": [
    {
      "types": ["minecraft:magic", "minecraft:indirect_magic", "otromod:arcane_spell"],
      "modifiers": [ { "attribute": "ender", "value": 100 } ]
    }
  ]
}
```

Ejemplo — pechera que resiste el fuego solo contra daño de fuego (lava, fuego, bolas de fuego), usando un tag:

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

### 7.6 Qué regla gana si hay varias

Para un mismo daño, de cada entidad (y de cada ítem equipado) se aplica **como máximo una regla** de ataque y una de resistencia. Se elige así:

1. Una regla que nombra el **id exacto** del damage type gana sobre una que solo coincide por **tag**.
2. A igual precisión, gana la del **archivo específico** (`entities`/`items`) sobre la del **grupo**.
3. Si aún empatan, gana la que está **primero** en la lista.

Para buscar, solo se consideran las reglas que tienen algo de esa categoría: si la regla de `magic` solo tiene `fire_resist`, al **atacar** con magia se sigue buscando entre las demás reglas (y, si no hay ninguna, se usa el perfil por defecto).

### 7.7 Ejemplos completos

**Bruja: agua por defecto, fuego con sus pociones.**
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

**Esqueleto: corte a melee, perforante con flechas (por tag, sirve también para proyectiles de otros mods).**
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

**Mob de otro mod con varios ataques.** Supongamos un dragón de `otromod` que muerde (`minecraft:mob_attack`), escupe fuego (`otromod:fire_breath`) y lanza rayos (`otromod:lightning_strike`). Primero averiguá los ids reales con la sección 8.
`data/ews/entities/otromod/dragon.json`:
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
      "types": ["otromod:fire_breath"],
      "modifiers": [ { "attribute": "fire", "value": 100 } ]
    },
    {
      "types": ["otromod:lightning_strike"],
      "modifiers": [
        { "attribute": "lightning", "value": 80 },
        { "attribute": "holy", "value": 20 }
      ]
    }
  ]
}
```

**Víctima que resiste la magia (resistencias por damage type).**
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
Cuando el evocador recibe daño mágico, `fire_resist` pasa de 10 a 60 y `evocation_resist` a 60; el resto de sus resistencias queda igual.

**Grupo con reglas.** Las reglas también se pueden poner en `groups`; aplican a todos los `ids`:
`data/ews/groups/no_muertos.json`:
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

## 8. Cómo encontrar el damage type de un ataque (vanilla y otros mods)

### 8.1 Con el modo debug (la forma más fácil, sirve para cualquier mod)

1. Abrí `config/ews-common.toml` y poné `debug_info = true` (y `debug_damage = true` si además querés ver los números).
2. Entrá al mundo y dejá que el mob te pegue (o pegale vos).
3. En el chat aparece `Tipo de Daño: <id>` (ese es el id que va en `types`), `Atacante: <mob>`, y qué perfil se usó (`Perfil de ataque` / `Perfil de resistencia`: `default`, o la regla que se aplicó).

Si el daño no tiene ni atacante ni archivo en la carpeta `damage_types`, verás `No se encontraron datos para el tipo de daño …` (solo con `debug_info`).

### 8.2 Quién cuenta como "atacante"

El atacante es la entidad **dueña** del daño, no el proyectil: para una flecha es el esqueleto que la disparó; para una poción lanzada, la bruja. Por eso las reglas van en el archivo del mob que dispara/lanza. (Si el daño viene directamente de otra entidad viviente, se usa esa.)

### 8.3 Damage types vanilla más comunes en mobs

| Damage type | Lo usan |
|---|---|
| `minecraft:mob_attack` | Golpes cuerpo a cuerpo de casi todos los mobs, mordisco y aletazos del dragón, segundo golpe del láser del guardián |
| `minecraft:player_attack` | Golpes cuerpo a cuerpo de jugadores |
| `minecraft:arrow` | Flechas (esqueletos, pillagers, jugadores con arco/ballesta) |
| `minecraft:trident` | Tridente lanzado (ahogados, jugadores) |
| `minecraft:mob_projectile` | Proyectiles de mobs como la escupida de la llama o la bala del shulker |
| `minecraft:fireball` | Bolas de fuego de blaze y ghast |
| `minecraft:wither_skull` | Calaveras del Wither |
| `minecraft:indirect_magic` | Poción lanzada por la bruja, colmillos del evocador, **aliento del dragón del End** (`minecraft:dragon_breath` existe pero vanilla no lo usa), primer golpe del láser del guardián |
| `minecraft:magic` | Efectos mágicos (ej. daño instantáneo, veneno) — el veneno **no tiene atacante** |
| `minecraft:sonic_boom` | Ataque sónico del Warden |
| `minecraft:player_explosion` | Explosiones causadas por una entidad: creeper, Wither y sus calaveras, TNT encendida por un jugador |
| `minecraft:explosion` | Explosiones sin entidad responsable (ej. TNT activada con redstone) |
| `minecraft:sting` | Picadura de abeja |
| `minecraft:thorns` | Espinas, pinchos del guardián al pegarle |

Un mismo ataque puede hacer **más de un** damage type: el láser del guardián pega `indirect_magic` y enseguida `mob_attack`. Cada parte usa su propia regla (o el perfil por defecto si no tiene).

El repo incluye un datapack de prueba con todos estos casos en `datapack-prueba/EWS-Prueba`.

**Cuidado con la bruja:** su poción de daño pega como `indirect_magic` (con la bruja como atacante), pero el **veneno** que deja hace `magic` **sin atacante**, así que ese daño no se le puede atribuir a la bruja: para el veneno usá la carpeta `damage_types` (sección 6).

### 8.4 Tags útiles

Los tags agrupan varios damage types, incluidos los de otros mods que se agreguen al tag:

| Tag | Incluye |
|---|---|
| `#minecraft:is_projectile` | Flechas, tridentes, bolas de fuego, proyectiles de mobs… |
| `#minecraft:is_fire` | Fuego, lava, bolas de fuego… |
| `#minecraft:is_explosion` | Explosiones |
| `#minecraft:is_fall` | Caídas |
| `#minecraft:is_freezing` | Congelamiento en nieve polvo |
| `#minecraft:is_lightning` | Rayos |
| `#minecraft:witch_resistant_to` | `magic`, `indirect_magic`, `sonic_boom`, `thorns` |

Los mods pueden traer sus propios tags; buscalos en el `.jar` del mod en `data/<modid>/tags/damage_type/`. Los damage types de un mod están en `data/<modid>/damage_type/`.

## 9. Orden de prioridad completo

Para cada golpe:

**Perfil de ataque** (de dónde salen los % de cada elemento):

1. Si la carpeta `damage_types` tiene ese damage type con `special: true` → se usa ese perfil fijo.
2. Si hay atacante viviente → su perfil:
   1. Regla de la entidad (`entities`/`groups`) para ese damage type, si hay (sección 7.6). Si no, su perfil por defecto (`modifiers`).
   2. Más el equipo, pociones y modificadores de otros mods (salvo `keep_bonuses: false`), donde cada ítem equipado con una regla para ese damage type aporta los valores de su regla en vez de los normales.
   3. `weapon_mode` (4.5; el de la regla si lo tiene, si no el de la entidad): si el arma de la mano principal aporta elementos, con `weapon` se descarta el perfil del paso 2.1 y con `share` se reparte todo hasta 100 si se pasa.
3. Si no hay atacante → perfil de la carpeta `damage_types` (no `special`). Si tampoco hay, EWS no toca el daño.

**Resistencias de la víctima**: siempre su perfil (regla de la entidad para ese damage type o, si no, el default) + su equipo (con las reglas de cada pieza).

## 10. Configuración del mod (`ews-common.toml`)

Archivo `config/ews-common.toml`:

| Opción | Default | Qué hace |
|---|---|---|
| `detalles` | `false` | En Jade muestra el nombre del atributo junto al número (no solo el ícono). |
| `debug_damage` | `false` | Muestra en el chat el cálculo de cada golpe: daño inicial, daño de cada elemento (solo los que no son 0) y daño final. |
| `debug_info` | `false` | Muestra en el chat los datos de cada golpe: damage type, atacante/objetivo y qué perfil/regla se usó (incluido `weapon_mode`, ej. `minecraft:wither_skeleton [weapon_mode=share, x0.50]`). Se pueden activar las dos a la vez. |
| `mostrar_reglas_damage_type` | `true` | Muestra las reglas por damage type en Jade (`Con daño magic:` debajo de los daños/resistencias del mob) y en el tooltip de los ítems. Ponelo en `false` para ocultarlas (las reglas siguen funcionando). |

En Jade, sin apretar la tecla se ven los daños por defecto y las reglas de **ataque**; apretando la tecla de "Mostrar Defensas" se ven resistencias/debilidades y las reglas de **resistencia**.

## 11. Tips generales y errores comunes

- Podés omitir `modifiers`/`damage`/`ids`/`damage_types` si no hacen falta.
- No hace falta declarar los 13 elementos en cada archivo — lo que no aparece vale 0.
- `slots` y las operaciones porcentuales (`multiply_base`/`multiply_total`) **solo tienen efecto en ítems**; en `entities` (incluidas sus reglas) se ignoran: los mobs solo usan `addition`.
- **Un mob con arma pega el doble de lo esperado**: su perfil y el del arma se están sumando. Usá `weapon_mode: "share"` o `"weapon"` (4.5).
- **Si la suma de ataque de una regla no llega a 100**, el resto del golpe es daño sin tipo (sección 2), igual que en el perfil por defecto.
- **La regla no se aplica**: revisá con `debug_info = true` que el id en `types` sea exactamente el que muestra el chat (incluido el namespace del mod), y que la regla tenga atributos de la categoría correcta (ataque para cuando el mob pega, `_resist` para cuando recibe).
- **Error al cargar**: si un JSON está mal escrito (ej. `"mode": "sumar"` o una regla sin `types`), esa parte no se carga y el error aparece en `logs/latest.log` (`Error loading <archivo>: …`). También avisa ahí si un `attribute` no existe (typo). Revisá el log siempre que algo no haga efecto.
- Los cambios en el datapack se aplican con `/reload` en el servidor. Jade y los tooltips de ítems (lado cliente) reciben los datos al **entrar al mundo**: después de un `/reload`, salí y volvé a entrar para verlos actualizados.
- Los mobs que ya estaban spawneados conservan el perfil por defecto que tenían al aparecer; las reglas por damage type, en cambio, se leen en cada golpe.
