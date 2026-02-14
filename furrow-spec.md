# Furrow — App Specification

## Overview
Furrow is a personal Android homesteading tracker. Three modules: Bees, Poultry, Garden. Local-only storage (Room/SQLite). Built with Kotlin + Jetpack Compose. Designed to expand with additional modules later.

---

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Navigation:** Compose Navigation (single-activity)
- **Database:** Room (SQLite)
- **Architecture:** MVVM (ViewModel + Repository + Room DAOs)
- **DI:** Hilt
- **Target SDK:** 36 (Baklava)
- **Min SDK:** 24

---

## Project Structure
```
com.furrow.app/
├── data/
│   ├── local/
│   │   ├── FurrowDatabase.kt          # Room database
│   │   ├── dao/
│   │   │   ├── HiveDao.kt
│   │   │   ├── InspectionDao.kt
│   │   │   ├── ChickenDao.kt
│   │   │   ├── EggLogDao.kt
│   │   │   ├── GardenBedDao.kt
│   │   │   └── PlantingDao.kt
│   │   └── entity/
│   │       ├── Hive.kt
│   │       ├── Inspection.kt
│   │       ├── Treatment.kt
│   │       ├── Chicken.kt
│   │       ├── EggLog.kt
│   │       ├── FeedLog.kt
│   │       ├── GardenBed.kt
│   │       ├── Planting.kt
│   │       └── HarvestLog.kt
│   └── repository/
│       ├── BeeRepository.kt
│       ├── PoultryRepository.kt
│       └── GardenRepository.kt
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── navigation/
│   │   └── FurrowNavGraph.kt
│   ├── home/
│   │   └── HomeScreen.kt              # Dashboard
│   ├── bees/
│   │   ├── HiveListScreen.kt
│   │   ├── HiveDetailScreen.kt
│   │   ├── InspectionFormScreen.kt
│   │   └── BeeViewModel.kt
│   ├── poultry/
│   │   ├── FlockScreen.kt
│   │   ├── EggLogScreen.kt
│   │   ├── ChickenDetailScreen.kt
│   │   └── PoultryViewModel.kt
│   └── garden/
│       ├── GardenScreen.kt
│       ├── BedDetailScreen.kt
│       ├── PlantingFormScreen.kt
│       └── GardenViewModel.kt
├── di/
│   └── AppModule.kt                   # Hilt module
└── MainActivity.kt
```

---

## Navigation
- Bottom navigation bar with 4 tabs: **Home**, **Bees**, **Poultry**, **Garden**
- Home tab = dashboard with summary cards from each module
- Each module tab has its own nav stack

---

## Data Models

### Bees Module

**Hive**
| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | Auto-generated |
| name | String | e.g. "Hive 1" |
| installDate | Long | Epoch millis |
| queenStatus | String | "present", "absent", "unknown" |
| source | String | "package", "nuc", "swarm", "split" |
| notes | String? | |
| isActive | Boolean | Default true |

**Inspection**
| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| hiveId | Long (FK) | |
| date | Long | Epoch millis |
| temperament | String? | "calm", "nervous", "aggressive" |
| queenSeen | Boolean | |
| queenCells | Boolean | |
| eggsLarvae | Boolean | |
| broodPattern | String? | "solid", "spotty", "none" |
| honeyStores | String? | "heavy", "moderate", "light", "empty" |
| pollenStores | String? | "heavy", "moderate", "light", "empty" |
| pestsSigns | String? | Free text — varroa, hive beetles, wax moths, etc. |
| diseasesSigns | String? | Free text |
| frameCount | Int? | Frames of bees |
| addedSupers | Int? | Supers added this inspection |
| removedSupers | Int? | |
| feeding | String? | What was fed (syrup ratio, patties, etc.) |
| notes | String? | |
| weatherTemp | Int? | °F |
| weatherCondition | String? | "sunny", "cloudy", "rainy", "overcast" |

**Treatment**
| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| hiveId | Long (FK) | |
| date | Long | |
| type | String | "oxalic acid", "apivar", "formic pro", "apiguard", "thymol", "other" |
| method | String? | "dribble", "vaporize", "strip", "pad" |
| dose | String? | |
| endDate | Long? | For multi-day treatments |
| notes | String? | |

### Poultry Module

**Chicken**
| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| name | String? | Optional — not everyone names them |
| breed | String | e.g. "Leghorn" |
| dateAcquired | Long | |
| dateOfBirth | Long? | If known |
| status | String | "active", "deceased", "rehomed", "processed" |
| notes | String? | |

**EggLog**
| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| date | Long | |
| count | Int | Number of eggs collected |
| notes | String? | Abnormalities, soft shells, etc. |

**FeedLog**
| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| date | Long | |
| feedType | String | "layer pellets", "scratch", "oyster shell", "treats", "other" |
| amountLbs | Double? | |
| notes | String? | |

### Garden Module

**GardenBed**
| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| name | String | e.g. "Tomato Bag 1", "Raised Bed A" |
| type | String | "grow bag", "raised bed", "in-ground", "container" |
| sizGallons | Int? | For grow bags |
| lengthFt | Double? | For beds |
| widthFt | Double? | |
| soilType | String? | |
| sunExposure | String? | "full sun", "partial sun", "partial shade", "full shade" |
| notes | String? | |
| isActive | Boolean | Default true |

**Planting**
| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| bedId | Long (FK) | |
| plantName | String | e.g. "Roma Tomato" |
| variety | String? | |
| datePlanted | Long | |
| dateTransplanted | Long? | |
| source | String | "seed", "transplant", "cutting" |
| status | String | "growing", "producing", "finished", "failed" |
| notes | String? | |

**HarvestLog**
| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| plantingId | Long (FK) | |
| date | Long | |
| amountOz | Double? | Weight |
| count | Int? | For countable items (peppers, tomatoes) |
| notes | String? | |

---

## Screen Details

### Home / Dashboard
- Summary card per module showing key stats:
  - **Bees:** Number of active hives, days since last inspection, upcoming treatment end dates
  - **Poultry:** Flock count, eggs collected today/this week, weekly average
  - **Garden:** Active plantings, recent harvests
- Quick-action buttons: "Log Eggs", "New Inspection", "Log Harvest"

### Bees — Hive List
- Card per hive showing name, queen status, last inspection date
- FAB to add new hive
- Tap card → Hive Detail

### Bees — Hive Detail
- Hive info header (name, install date, queen status, source)
- Tabs or sections: Inspections (list, newest first), Treatments (list)
- FAB to add inspection or treatment

### Bees — Inspection Form
- Date picker (default today)
- Toggle/checkbox fields for queen seen, queen cells, eggs/larvae
- Dropdown selectors for temperament, brood pattern, honey/pollen stores
- Numeric inputs for frame count, supers
- Free text for pests, diseases, feeding, notes
- Optional weather fields

### Poultry — Flock Screen
- Egg log list (daily entries, newest first) with running weekly/monthly totals
- Section showing flock roster (list of chickens with breed, status)
- FAB to log eggs (primary action) or add chicken

### Poultry — Egg Log Entry
- Date picker (default today)
- Number input for count
- Notes field

### Garden — Garden Screen
- List of beds/containers with active planting count
- FAB to add bed
- Tap → Bed Detail

### Garden — Bed Detail
- Bed info (type, size, sun exposure)
- List of plantings in this bed
- Harvest log for this bed's plantings
- FAB to add planting or log harvest

---

## Theme Direction
- Earthy, natural palette — greens, browns, warm tones
- Material 3 dynamic color support
- Dark mode support
- Clean, functional UI — this is a tool, not a lifestyle app

---

## Claude Code Build Sequence

Build in this order. Each step should be a standalone prompt to Claude Code.

### Step 1: Project Foundation
> Set up the project architecture for Furrow. Add Hilt for dependency injection, Room for local database, and Compose Navigation. Create the base package structure under com.furrow.app with data/local/dao, data/local/entity, data/repository, ui/theme, ui/navigation, ui/home, ui/bees, ui/poultry, ui/garden, and di packages. Set up the Room database class (FurrowDatabase) with no entities yet. Set up the Hilt AppModule providing the database and DAOs. Set up the NavGraph with bottom navigation (Home, Bees, Poultry, Garden tabs) and placeholder screens for each. Use Material 3 with an earthy color theme (dark greens, warm browns). Ensure it compiles and runs showing the bottom nav with placeholder content on each tab.

### Step 2: Bees Module — Data Layer
> Add the Bees data layer. Create Room entities for Hive, Inspection, and Treatment with the following schemas: [paste Hive, Inspection, Treatment tables from this spec]. Create HiveDao and InspectionDao with standard CRUD operations plus: getActiveHives(), getInspectionsForHive(hiveId), getTreatmentsForHive(hiveId), getLatestInspection(hiveId). Create BeeRepository wrapping these DAOs. Register everything in the Room database and Hilt module. Ensure it compiles.

### Step 3: Bees Module — UI
> Build the Bees UI screens. Create BeeViewModel using Hilt that exposes StateFlow for hive list, selected hive detail, inspections, and treatments. Build HiveListScreen showing a card per active hive (name, queen status, days since last inspection) with a FAB to add a new hive. Build HiveDetailScreen showing hive info header, tabbed sections for inspections and treatments, with FAB to add either. Build InspectionFormScreen with date picker, toggles for queen seen/queen cells/eggs larvae, dropdowns for temperament/brood pattern/stores, numeric inputs for frames/supers, and free text fields. Build a similar TreatmentFormScreen. Wire navigation between these screens. Use Material 3 components throughout.

### Step 4: Poultry Module — Data Layer
> Add the Poultry data layer. Create Room entities for Chicken, EggLog, and FeedLog with these schemas: [paste tables]. Create ChickenDao and EggLogDao with CRUD plus: getActiveChickens(), getEggLogsForDateRange(start, end), getEggCountForDate(date), getWeeklyEggAverage(). Create PoultryRepository. Register in database and Hilt.

### Step 5: Poultry Module — UI
> Build the Poultry UI. Create PoultryViewModel exposing flock list, egg logs, and summary stats (today's count, weekly total, weekly average). Build FlockScreen with two sections: egg log (daily entries newest first with running totals) and flock roster (chickens with breed/status). Build quick egg logging with date picker, count input, notes. Build ChickenDetailScreen for viewing/editing individual bird info. Wire navigation.

### Step 6: Garden Module — Data Layer
> Add the Garden data layer. Create Room entities for GardenBed, Planting, and HarvestLog with these schemas: [paste tables]. Create GardenBedDao and PlantingDao with CRUD plus: getActiveBeds(), getPlantingsForBed(bedId), getHarvestsForPlanting(plantingId), getRecentHarvests(limit). Create GardenRepository. Register in database and Hilt.

### Step 7: Garden Module — UI
> Build the Garden UI. Create GardenViewModel exposing beds, plantings, harvests, and summary stats. Build GardenScreen showing bed cards with active planting count, FAB to add bed. Build BedDetailScreen showing bed info, planting list, harvest log, FABs to add planting or log harvest. Build PlantingFormScreen and HarvestLogScreen. Wire navigation.

### Step 8: Dashboard
> Build the Home dashboard screen. Create a HomeViewModel that pulls summary data from all three repositories: active hive count and days since last inspection, flock size and today's egg count plus weekly average, active planting count and recent harvests. Display as summary cards with quick-action buttons that navigate to the relevant log entry screens (Log Eggs → egg entry, New Inspection → inspection form, Log Harvest → harvest entry).
