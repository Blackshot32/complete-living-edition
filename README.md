# Mo' Creatures: Complete Living Edition (1.21.1 NeoForge)

![Mo' Creatures: Complete Living Edition](aura_banner.png)

[![Minecraft 1.21.1](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen.svg)](https://minecraft.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.1+-orange.svg)](https://neoforged.net/)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

**Mo' Creatures: Complete Living Edition** brings the legendary classic mod to **Minecraft 1.21.1 (NeoForge)** with all ~60 original creatures, enhanced by modern living AI, a realistic predator-prey food chain, natural territorial rivalries, and robust biome generation.

---

## ✨ Key Features

### 🦁 Living Food Chain & Ecosystem (Naturalist Cooldown)
* **Digestive Cooldown (2 Minutes):** Apex predators (Big Cats, Bears, Crocodiles, Foxes, Komodos, Sharks) hunt natural prey and enter a satiated state for 2,400 ticks (2 minutes), preventing immediate massacres of local fauna.
* **Pet Protection:** Predators strictly ignore tamed animals and companions (`isTame()`).
* **Prey Survival Instincts:** Deer, Boars, Bunnies, and Ducks detect approaching carnivores and flee in panic (`AvoidEntityGoal`).

### ⚔️ Territorial Rivalries & Behavioral Overhaul
* **Werewolves (Village Sieges):** Roam harmlessly as humans during the day, but transform under the moonlight to actively assault settlements and duel **Iron Golems**.
* **Ogres (Siege Warfare):** Green, Fire, and Cave Ogres actively target villages and defensive fortifications, retaining their classic block-breaking capability (`OgreBlockGriefing = true`).
* **Ents vs. Fire Ogres (Ancient Rivalry):** Forest guardian Ents will charge and attack Fire Ogres on sight to protect nature from destruction.
* **Panicked Rats:** Flee at high speed from domestic Cats and Big Cats.
* **Greedy Filch Lizards:** Irresistibly lured by gold ingots, raw gold, nuggets, and diamonds held by players.
* **Manticores & Wraiths:** Roaming aerial patrol flights and eerie gliding navigation.

### 🌲 Robust Biome Generation (NeoForge 1.21.1)
* **Cross-Namespace Compatibility:** Full bidirectional support for `#c:is_*` and `#minecraft:is_*` convention tags.
* **Smart Fallback:** Native compatibility with worldgen mods like **Terralith** and **Biomes O' Plenty** via intelligent path classification (plains, forest, taiga, savanna, jungle, swamp, desert, snowy, mountain, ocean, river, badlands).

### 🧩 Extensible AI Compatibility
* **Datapack-driven prey lists:** Pack makers can append an entity to `#mocreatures:predator_prey` for large predators or `#mocreatures:fox_prey` for foxes, without adding a code dependency between mods.
* **Safe target selection:** Tamed animals are excluded from autonomous hunting; third-party creatures can participate even when they do not inherit vanilla's `Animal` class.

### 🐎 Mythological Horse Transmutation
* Tamed horses can be fed Mo' Creatures Magical Essences to transform them:
  * **Heart of Fire** $\rightarrow$ **Nightmare** (fire trails, lava immunity)
  * **Heart of Darkness** $\rightarrow$ **Bat Horse** (wings, controlled flight)
  * **Heart of Undead** $\rightarrow$ **Undead Horse** (skeletal/zombie steed)
  * **Heart of Light** $\rightarrow$ **Unicorn** (horn charge attack)

---

## 📦 Requirements & Installation

1. Install **Minecraft 1.21.1** with **NeoForge 21.1.1+**.
2. Download `mocreatures-1.21.1-1.0.9-complete-edition.jar` from the [Releases](https://github.com/Blackshot32/complete-living-edition/releases) section.
3. Place the `.jar` inside your `.minecraft/mods` folder.
4. Launch and enjoy!

---

## ⚙️ Configuration

Mo' Creatures configuration is located at `config/MoCreatures/MoCSettings.cfg`:
* `OgreBlockGriefing`: Set to `false` if you want Ogres to deal combat damage without destroying blocks/machinery.
* `EcosystemFilterMode`: Set to `NEVER_PRUNE` by default in this Complete Edition so all creatures spawn naturally.

---

## 👥 Credits & Acknowledgments

* **Original Author & Creator:** DrZhark
* **Original Models & Textures:** BlockDaddy, DrZhark
* **Original Code & Systems:** Bloodshot, multision, TheidenHD, ACGaming, IcarussOne, xJon
* **NeoForge 1.21.1 Port:** meynoik (Aura Edition)
* **Complete Living Edition & AI Overhaul:** Golden & Antigravity
* **Cadena Trófica / Food Chain Inspiration:** Starfish Studios (Naturalist team, MIT License)
