# Clash of Claws

A competitive real-time multiplayer battle royale game built with LibGDX. Control your cat, eat kibbles to grow, and outlast your opponents. Last cat standing wins.

---

## Gameplay

Players control a cat that grows longer as it eats kibbles scattered across the map. Lure opponents into running their head into your body to eliminate them, collect powerups for advantages, and use your dash ability strategically. The game lasts 3 minutes — if opponents remain when time runs out, the player with the highest score wins.

### Match End Conditions
| Condition | Outcome |
|-----------|---------|
| All opponents eliminated | Last surviving cat wins immediately |
| 3-minute timer expires | Player with the highest score wins |

### Collision Rules
| Situation | Outcome |
|-----------|---------|
| Head hits opponent's body | **Attacker (head) is eliminated** — body cat wins |
| Head-to-head (higher score) | Lower score cat is eliminated |
| Head-to-head (equal score) | Both cats are eliminated |
| Shielded cat takes a fatal hit | Shield absorbs the hit (consumed); cat survives |

### Powerups
| Powerup | Effect | Duration |
|---------|--------|----------|
| Speed Boost | 1.5x movement speed | 5 sec |
| Shield | Absorbs one fatal collision | 8 sec |
| Kibble Magnet | Pulls nearby kibbles toward you | 6 sec |

---

## Characters

| | Fluffy | Shadow | Swift |
|--|--------|--------|-------|
| **Max Speed** | 4.0 | 5.0 | 6.0 |
| **Dash Multiplier** | 1.4x | 1.5x | 1.4x |
| **Starting Size** | 2 | 3 | 1 |
| **Growth Rate** | 5 | 2 | 3 |
| **Stamina Drain** | 20/s | 35/s | 25/s |
| **Stamina Recharge** | 15/s | 10/s | 12/s |

---

## Game Modes

- **Quick Match** — Auto-match with up to 6 players
- **Private Room** — Create or join a room using a 6-character room code

Minimum 2 players required to start.

---

## Controls

| Input | Action |
|-------|--------|
| Joystick (touchpad) | Move cat |
| DASH button / Spacebar | Activate dash |
| WASD / Arrow keys | Move (desktop) |

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Game Engine | LibGDX 1.14.0 |
| Physics | Box2D |
| Entity System | Ashley 1.7.4 |
| Networking | KryoNet 2.22.0-RC1 |
| Backend | Custom Java Server (Azure Hosted). |
| Build | Gradle |
| Desktop Runtime | LWJGL3 |

The game server runs on Azure and handles matchmaking, powerup spawning, kibble distribution, and game state synchronisation over TCP/UDP.

---

## Project Structure

```
final_project/
├── core/                     # Shared game logic
│   └── src/main/java/.../
│       ├── states/           # UI screens (Login, Lobby, Play, Results, etc.)
│       ├── ecs/
│       │   ├── components/   # ECS components (Cat, Powerup, Stamina, etc.)
│       │   └── systems/      # ECS systems (Movement, Dash, Powerup, etc.)
│       ├── network/          # Client/server networking & message definitions
│       ├── audio/            # Audio manager
│       └── config/           # Cat stats & game constants
├── lwjgl3/                   # Desktop backend
├── android/                  # Android backend
└── assets/                   # Sprites, sounds, fonts, UI skin
```

---

## Building & Running

**Run on desktop:**
```bash
./gradlew lwjgl3:run
```

**Build JAR:**
```bash
./gradlew lwjgl3:jar
```

**Platform-specific builds:**
```bash
./gradlew lwjgl3:jarMac      # macOS
./gradlew lwjgl3:jarLinux    # Linux
./gradlew lwjgl3:jarWin      # Windows
```

**Requirements:** Java 17+, Gradle 9.2.1+

---

## Team

TDT4240 Group 4
