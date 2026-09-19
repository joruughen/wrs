# Documentación del Mod EWS (Elemental Weakness System)

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

- Resistencia entre `0` y `100`: reduce ese porcentaje linealmente (`resistencia = 50` → mitad de daño de ese elemento).
- Resistencia `> 100`: bloquea completamente ese elemento (0 de daño).
- Resistencia negativa: **amplifica** ese elemento (`resistencia = -20` → +20 % de daño de ese elemento).

Los 13 elementos se calculan de forma **independiente** (cada uno contra el 100 % del daño original) y después se **suman**. Si sumás los valores de ataque de tus 13 elementos y **no llegan a 100**, la diferencia se agrega aparte como daño sin tipo, que **no pasa por ninguna resistencia** — así que si a un mob solo le ponés `fire = 30` y nada más, el otro 70 % del golpe va a ignorar todas las resistencias elementales de la víctima. Tenlo en cuenta al balancear.

## 3. Sistemas de datos: cuál usa cada carpeta

Hay dos sistemas de datos distintos, con **formatos diferentes**:

| Carpeta | Qué asigna | Formato |
|---|---|---|
| `groups`, `entities`, `items` | Atributos elementales a ítems/entidades (equipo, stats de mobs) | `modifiers` (ver sección 4) |
| `damage_types` | Composición elemental fija de un `DamageType` de Minecraft (lava, caída, hechizos, etc.), independiente de quién lo cause | `damage` + `special` (ver sección 6) — **este formato no cambió** |

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

- **Archivo individual** (`entities/minecraft/zombie.json`, `items/minecraft/diamond_sword.json`): el id se saca del propio path del archivo (`<carpeta_dentro_de_entities_o_items>/<nombre>.json` → `<esa_carpeta>:<nombre>`). No hace falta el campo `"ids"`.
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
  ]
}
```

- **`ids`** (solo en `groups`, opcional/omitido en archivos individuales): lista de ids `"namespace:nombre"` a los que aplica.
- **`modifiers`**: lista de modificadores. Cada uno tiene:
  - **`attribute`** *(obligatorio)*: el atributo al que apunta. Nombre corto (`"fire"`, `"nature_resist"`) asume el elemento propio de EWS; también podés poner el id completo (`"ews:fire"`), o en teoría el de cualquier otro mod (`"attributeslib:crit_chance"`).
  - **`operation`** *(opcional, default `"addition"`)*: `"addition"`, `"multiply_base"` o `"multiply_total"` (ver 4.3).
  - **`value`** *(obligatorio)*: número. Su significado depende de `operation`.
  - **`slots`** *(opcional, solo tiene efecto en `items`, se ignora en `entities`)*: lista con uno o más de `head`, `chest`, `legs`, `feet`, `main`, `off`. Si no lo ponés, el modificador aplica en cualquier slot donde esté puesto el ítem. Si lo ponés, **solo** aplica cuando el ítem está en ese slot específico.

No hace falta listar los 13 elementos — el que no aparece en `modifiers` simplemente vale 0.

### 4.3 Las tres operaciones

- **`addition`**: suma un valor plano al atributo. Para los elementos de EWS, este es el "% del golpe" del que habla la sección 2 — `{"attribute": "fire", "operation": "addition", "value": 100}` en un arma significa "el 100 % de lo que pegue esa arma es fuego".
- **`multiply_base`** y **`multiply_total`**: bonos **porcentuales** (`value: 10` = +10 %). Minecraft calcula: `(base + suma de additions) × (1 + suma de multiply_base) × cada multiply_total`.
  - **Importante**: nuestros 13 elementos arrancan en base `0`. Un `multiply_base`/`multiply_total` sobre un atributo que nadie puso en `0` con `addition` **no hace nada** (0 × cualquier cosa = 0). Sirven para potenciar un valor que **ya existe** — por ejemplo, un casco con `{"attribute": "pierce", "operation": "multiply_base", "value": 5}` solo hace algo si el arma con la que atacás ya te da algo de `pierce` por su cuenta; si atacás con un arma 100 % `slash`, el casco no afecta nada (ni al `slash`, que es un atributo aparte, ni al `pierce`, que sigue en 0).
  - `multiply_base` vs `multiply_total`: si tenés varios ítems con `multiply_base` sobre el mismo atributo, sus porcentajes se **suman** entre sí antes de aplicarse (dos `+10%` = `+20%` total). Con `multiply_total`, cada uno se aplica **en cadena** sobre el resultado del anterior (dos `+10%` = `+21%` total). Usá `multiply_base` salvo que quieras específicamente el efecto compuesto.

### 4.4 Prioridad: grupo vs. específico

Si un ítem/entidad matchea tanto un `group` como su propio archivo en `items`/`entities`, se combinan: si ambos definen un modificador para el **mismo `attribute` + `operation`**, gana el específico (`items`/`entities`); el resto de los modificadores de ambos se conserva. Si dos archivos de **grupo** distintos apuntan al mismo id, sus modificadores se **suman** en la lista (no se pisan).

## 5. Ejemplos

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

## 6. `damage_types` (sin cambios respecto a antes)

Define la composición elemental fija de un `DamageType` de Minecraft — por ejemplo, para que la lava siempre sea 100 % fuego sin importar quién/qué causó el daño.

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
- **`special`**: si es `true`, este perfil elemental se usa **siempre** para ese `DamageType`, ignorando por completo los atributos del atacante (útil para daño sin atacante con sentido, como caída o lava, o para forzar que un tipo de daño sea siempre el mismo elemento). Si es `false`/lo omitís, solo se usa cuando no hay atacante viviente.

## 7. Tips generales

- Podés omitir `modifiers`/`damage`/`ids` si no hacen falta.
- No hace falta declarar los 13 elementos en cada archivo — lo que no aparece vale 0.
- `slots` y las operaciones porcentuales (`multiply_base`/`multiply_total`) **solo tienen efecto en ítems**; en `entities` se ignoran (los mobs solo usan `addition`, como valor base fijo).
- Si algo no anda, mirá `run/logs/latest.log` — el mod avisa ahí si un `attribute` de tu JSON no se pudo resolver (por typo, por ejemplo).
