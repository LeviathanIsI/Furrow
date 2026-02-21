# Furrow: Full App Scope & Data Architecture

> **No single app integrates everything a homesteader needs.** The market is fragmented across 15+ niche tools — livestock apps ignore gardens, garden planners ignore livestock, and nobody tracks food preservation. Furrow is the first true all-in-one homesteading app.

---

## 1. Animals

### Core Animal Entity (all species)

| Field | Type | Notes |
|-------|------|-------|
| `animal_id` | Long (PK) | Auto-generated |
| `species` | String | chicken, duck, turkey, quail, goat, cattle, pig, sheep, rabbit |
| `breed` | String | Linked to breed reference DB |
| `sex` | String | male/female/unknown |
| `dob` | Long (epoch) | Date of birth/hatch |
| `name` | String | Optional |
| `tag_id` | String | Ear tag, leg band, tattoo, RFID |
| `acquisition_date` | Long | When acquired |
| `source` | String | Hatchery, breeder, self-hatched, auction |
| `cost` | Double | Purchase cost |
| `status` | String | active/sold/deceased/processed |
| `purpose` | String | meat/dairy/eggs/fiber/breeding/pet |
| `photo_url` | String | Local file path |
| `color_markings` | String | Description |
| `registration_number` | String | For registered stock |
| `housing_location` | String | Coop/barn/paddock name |
| `notes` | String | |

### Individual vs. Flock Tracking

- **Individual tracking**: goats, cattle, pigs, sheep, rabbits (always), plus breeding poultry
- **Flock/batch tracking**: chickens, ducks, turkeys, quail — tracked as groups with `flock_id`, `flock_size`, `housing_location`

### Health Records

| Field | Type | Notes |
|-------|------|-------|
| `health_record_id` | Long (PK) | |
| `animal_id` | Long (FK) | |
| `date` | Long | |
| `type` | String | vaccine/deworm/medication/vet_visit/hoof_trim/beak_trim/body_condition |
| `product_name` | String | |
| `dosage` | String | |
| `route` | String | oral/injection/topical/pour-on |
| `withdrawal_period_days` | Int | Days before safe for consumption |
| `withdrawal_end_date` | Long | Auto-calculated |
| `vet_name` | String | |
| `diagnosis` | String | |
| `body_condition_score` | Int | 1-5 (all species) or 1-9 (cattle) |
| `famacha_score` | Int | 1-5, goats and sheep only (anemia/parasite indicator) |
| `cost` | Double | |
| `notes` | String | |

**Species-specific vaccinations:**
- Goats/Sheep: CDT (Clostridium perfringens + tetanus)
- Chickens: Marek's disease
- Rabbits: RHDV2
- Cattle: 7-way clostridial
- Pigs: Erysipelas

**Safety flag:** Sheep cannot tolerate copper above 20 PPM — app should warn if sharing feed data with goats.

### Breeding Records

| Field | Type | Notes |
|-------|------|-------|
| `breeding_id` | Long (PK) | |
| `sire_id` | Long (FK) | |
| `dam_id` | Long (FK) | |
| `breeding_date` | Long | |
| `method` | String | natural/AI |
| `due_date` | Long | Auto-calculated from species gestation |
| `birth_date` | Long | Actual |
| `offspring_count` | Int | |
| `offspring_sexes` | String | e.g. "2M/1F" |
| `birth_weights` | String | |
| `ease_score` | Int | 1-5 |
| `complications` | String | |
| `notes` | String | |

**Gestation/Incubation Periods (auto-populate due dates):**

| Species | Days | Note |
|---------|------|------|
| Chicken | 21 | |
| Duck | 28 | Muscovy: 35 |
| Turkey | 28 | |
| Quail (Coturnix) | 16-18 | |
| Rabbit | 28-33 (avg 31) | Shortest mammal |
| Goat | 145-155 (avg 150) | ~5 months |
| Sheep | 142-152 (avg 147) | ~5 months |
| Pig | 112-120 (avg 114) | "3 months, 3 weeks, 3 days" |
| Cattle | 280-290 (avg 283) | ~9.5 months |

### Production Tracking

**Egg Log** (chickens, ducks, turkeys, quail):
- `date`, `flock_id`, `eggs_collected`, `eggs_broken`, `egg_size`, `egg_color`

**Milk Log** (dairy goats, cattle, sheep):
- `date`, `animal_id`, `milk_am_lbs`, `milk_pm_lbs`, `total_daily`, `butterfat_%`, `somatic_cell_count`

**Wool/Fiber Log** (sheep, Angora rabbits/goats):
- `shearing_date`, `fleece_weight`, `fiber_grade`, `staple_length`

**Weight Tracking** (meat animals):
- `date`, `weight`, `ADG` (average daily gain — auto-calculated)

**Typical yields:**
- Layer hens: 250-300 eggs/yr
- Dairy goat: 0.5-1.5 gal/day
- Beef dress-out: ~60% of live weight → ~36% as cuts
- Pigs: ~72% hanging weight
- Sheep/goat: ~50% hanging weight
- Rabbits: ~60% dressed weight

### Feed Tracking

| Field | Type | Notes |
|-------|------|-------|
| `feed_log_id` | Long (PK) | |
| `animal_id` or `flock_id` | Long (FK) | |
| `date` | Long | |
| `feed_type` | String | |
| `quantity_lbs` | Double | |
| `cost` | Double | |
| `supplier` | String | |

**Daily consumption benchmarks (for budget estimation):**

| Species | Daily feed per animal |
|---------|---------------------|
| Chicken (layer) | 0.25-0.33 lb |
| Duck | 0.25-0.5 lb |
| Turkey (tom) | 0.75-1 lb |
| Quail | ~1 oz (30g) |
| Goat | 3-5 lbs hay + 0.5-1.5 lbs grain |
| Cattle | 25-30 lbs hay (1,000 lb cow) |
| Pig (finishing) | 6-8 lbs |
| Sheep | 3-4.5 lbs hay |
| Rabbit | 4-6 oz pellets + unlimited hay |

### Processing/Butchering

| Field | Type | Notes |
|-------|------|-------|
| `processing_id` | Long (PK) | |
| `animal_id` or `batch_id` | Long (FK) | |
| `date` | Long | |
| `live_weight` | Double | |
| `hanging_weight` | Double | |
| `cut_weight` | Double | Final packaged weight |
| `processor_name` | String | |
| `cost` | Double | |
| `cut_list` | String | JSON or text |
| `aging_days` | Int | |
| `pelt_saved` | Boolean | |
| `tanner` | String | |
| `tanning_cost` | Double | |
| `notes` | String | |

---

## 2. Bees

### Apiary Entity

| Field | Type | Notes |
|-------|------|-------|
| `apiary_id` | Long (PK) | |
| `name` | String | |
| `gps_lat` | Double | |
| `gps_lon` | Double | |
| `elevation` | Int | Feet |
| `forage_notes` | String | |

### Hive Record

| Field | Type | Notes |
|-------|------|-------|
| `hive_id` | Long (PK) | |
| `apiary_id` | Long (FK) | |
| `hive_type` | String | Langstroth/Top Bar/Warré |
| `installation_date` | Long | |
| `source` | String | package/nuc/swarm/split |
| `status` | String | active/dead-out/combined/sold |
| `equipment_config` | String | deeps, mediums, supers, excluder Y/N, screened bottom Y/N |

### Queen Record

| Field | Type | Notes |
|-------|------|-------|
| `queen_id` | Long (PK) | |
| `hive_id` | Long (FK) | |
| `breed` | String | Italian/Carniolan/Buckfast/etc. |
| `source` | String | |
| `introduction_date` | Long | |
| `marked` | Boolean | |
| `mark_color` | String | International year-color: white/yellow/red/green/blue |
| `age_years` | Int | |
| `status` | String | laying/virgin/superseded/missing/dead |
| `parent_queen_id` | Long (FK) | For lineage tracking |

### Inspection Log

| Field | Type | Notes |
|-------|------|-------|
| `inspection_id` | Long (PK) | |
| `hive_id` | Long (FK) | |
| `date` | Long | |
| `time` | String | |
| `weather_temp` | Int | |
| `weather_wind` | String | |
| `weather_cloud` | String | |
| `queen_spotted` | Boolean | |
| `queen_cells` | Boolean | |
| `queen_cell_type` | String | swarm/supersedure |
| `brood_pattern_rating` | Int | 1-5 |
| `frames_of_brood` | Int | |
| `frames_of_honey` | Int | |
| `frames_of_pollen` | Int | |
| `population_estimate` | Int | Frames of bees |
| `temperament_rating` | Int | 1-5 |
| `pest_signs` | String | varroa/SHB/wax moth/ants |
| `disease_signs` | String | AFB/EFB/chalkbrood/nosema/DWV |
| `notes` | String | |
| `photos` | String | Comma-separated paths |

### Varroa Monitoring

| Field | Type | Notes |
|-------|------|-------|
| `test_id` | Long (PK) | |
| `hive_id` | Long (FK) | |
| `date` | Long | |
| `test_method` | String | alcohol_wash/sugar_roll/sticky_board |
| `sample_size` | Int | Standard = 300 bees |
| `mite_count` | Int | |
| `infestation_rate` | Double | Auto-calculated: mite_count / (sample_size/100) |

**Treatment thresholds:** ≤2% safe; >3% treat immediately.

### Treatment Log

| Field | Type | Notes |
|-------|------|-------|
| `treatment_id` | Long (PK) | |
| `hive_id` | Long (FK) | |
| `date_start` | Long | |
| `date_end` | Long | |
| `product` | String | Apivar/Apiguard/oxalic acid/formic acid/HopGuard |
| `dosage` | String | |
| `ambient_temp` | Int | |
| `mite_count_before` | Int | |
| `mite_count_after` | Int | |
| `supers_removed` | Boolean | |

### Feeding Log

- `hive_id`, `date`, `feed_type` (1:1 syrup/2:1 syrup/pollen patty/fondant/dry sugar), `amount`, `feeder_type`

### Harvest Log

- `hive_id`, `date`, `product` (honey/wax/propolis/pollen), `yield_lbs`, `frames_pulled`, `moisture_%` (target <18.6%), `floral_source`

### Splits/Swarms

- `event_type` (split/swarm/combine), `date`, `origin_hive_id`, `destination_hive_id`, `queen_status_post`, `notes`

---

## 3. Garden

### Garden Entity

| Field | Type | Notes |
|-------|------|-------|
| `garden_id` | Long (PK) | |
| `name` | String | |
| `gps_lat` | Double | |
| `gps_lon` | Double | |
| `usda_zone` | String | 1-13 with a/b |
| `last_frost_date` | String | |
| `first_frost_date` | String | |
| `growing_season_days` | Int | Auto-calculated |

### Bed/Plot Record

| Field | Type | Notes |
|-------|------|-------|
| `bed_id` | Long (PK) | |
| `garden_id` | Long (FK) | |
| `name` | String | |
| `length` | Double | Feet |
| `width` | Double | Feet |
| `depth` | Double | For raised beds |
| `type` | String | raised/in-ground/container/hydroponic |
| `soil_type` | String | sandy/loamy/clay |
| `sun_exposure` | String | full (6+ hrs)/partial (4-6)/shade (<4) |
| `irrigation_type` | String | drip/sprinkler/hand/rain-only |
| `orientation` | String | N-S/E-W |

### Crop/Planting Record

| Field | Type | Notes |
|-------|------|-------|
| `crop_id` | Long (PK) | |
| `bed_id` | Long (FK) | |
| `species` | String | |
| `variety` | String | |
| `plant_family` | String | Solanaceae/Brassicaceae/Cucurbitaceae/Fabaceae/Allium/Apiaceae — critical for rotation |
| `seed_source` | String | |
| `days_to_maturity` | Int | |
| `spacing_inches` | Int | |
| `planting_method` | String | indoor_start/direct_sow/transplant |
| `indoor_start_date` | Long | |
| `transplant_date` | Long | |
| `direct_sow_date` | Long | |
| `expected_harvest_date` | Long | Auto-calculated |
| `status` | String | planned/seeded/growing/harvesting/finished |
| `companion_notes` | String | |

### Crop Rotation Log

- `bed_id`, `year`, `season`, `crop_family_planted`
- **Rule:** Don't repeat same family in same bed for 3-4 years

### Pest/Disease Log

| Field | Type | Notes |
|-------|------|-------|
| `entry_id` | Long (PK) | |
| `crop_id` | Long (FK) | |
| `bed_id` | Long (FK) | |
| `date` | Long | |
| `pest_or_disease` | String | |
| `severity` | String | low/medium/high |
| `treatment_applied` | String | |
| `organic_flag` | Boolean | |
| `outcome` | String | |
| `follow_up_date` | Long | |

### Soil Amendment Log

- `bed_id`, `date`, `amendment_type` (compost/manure/fertilizer/lime/sulfur/mulch/bone meal/blood meal/fish emulsion), `npk_values`, `amount`, `method`

### Seed Inventory

| Field | Type | Notes |
|-------|------|-------|
| `seed_id` | Long (PK) | |
| `species` | String | |
| `variety` | String | |
| `supplier` | String | |
| `lot_number` | String | |
| `purchase_date` | Long | |
| `quantity` | String | |
| `germination_rate` | Int | Percentage |
| `expiration_date` | Long | |
| `storage_location` | String | |
| `cost` | Double | |
| `saved_seed` | Boolean | From own harvest |

### Harvest Log

- `crop_id`, `bed_id`, `date`, `yield_lbs` or `yield_count`, `quality_notes`, `destination` (eat_fresh/preserve/sell/give_away/compost)

### Season Extension

- `equipment_type` (row cover/cold frame/greenhouse/low tunnel/high tunnel), `bed_id`, `deploy_date`, `remove_date`, `temp_inside`

---

## 4. Orchard (Perennial Fruit)

Different lifecycle than annual gardens — needs long-term tracking fields.

### Tree/Plant Record

| Field | Type | Notes |
|-------|------|-------|
| `plant_id` | Long (PK) | |
| `location_gps_lat` | Double | |
| `location_gps_lon` | Double | |
| `category` | String | fruit_tree/berry_bush/vine/bramble |
| `species` | String | |
| `variety` | String | |
| `rootstock` | String | e.g. M26 dwarf, MM111 semi-dwarf |
| `pollinator_group` | String | Self-fertile or cross-pollination needed + compatible varieties |
| `source_nursery` | String | |
| `planting_year` | Int | |
| `years_to_bearing` | Int | 2-5 dwarf, 3-7 standard |
| `chill_hours_required` | Int | Low <300, moderate 400-700, high 800-1000+ |
| `zone_hardiness` | String | |
| `status` | String | dormant/vegetative/flowering/fruiting/removed |

### Chill Hours Tracking

- `season`, `accumulated_chill_hours` (hours at 32-45°F), `model_used` (Utah/Dynamic/Hours Below 45°F), `variety_requirement`, `predicted_bloom_quality`
- **Tracking window:** Nov 1 – Feb 28

### Pruning Log

- `plant_id`, `date`, `pruning_type` (dormant/summer/formative/renewal), `method` (thinning/heading/central leader/open vase/espalier), `before_photo`, `after_photo`, `notes`

### Spray Schedule

- `plant_id`, `date`, `product`, `timing` (dormant/delayed dormant/pre-bloom/petal fall/cover spray), `active_ingredient`, `concentration`, `pre_harvest_interval_days`, `organic_approved`, `weather_conditions`

### Bloom/Fruit Tracking

- `plant_id`, `year`, `first_bloom_date`, `full_bloom_date`, `petal_fall_date`, `fruit_set` (light/moderate/heavy), `thinning_date`, `thinning_method`

### Orchard Harvest

- `plant_id`, `date`, `yield_lbs`, `fruit_quality`, `brix` (sugar content), `destination`

### Grafting Log

- `plant_id`, `date`, `scion_source`, `graft_type` (whip-and-tongue/cleft/bark/chip bud/T-bud), `success` (Y/N/pending), `photos`

### Berry-Specific Fields

- `cane_type` (primocane/floricane), `runner_management_date`, `renovation_date`, `trellis_type`, `netting_dates`

---

## 5. Food Preservation (BIGGEST UNSERVED GAP)

**No existing app tracks food preservation inventory. This is Furrow's strongest differentiator.**

### Canning

| Field | Type | Notes |
|-------|------|-------|
| `batch_id` | Long (PK) | |
| `recipe_name` | String | |
| `recipe_source` | String | |
| `usda_approved` | Boolean | **Critical safety field** |
| `method` | String | water_bath/pressure |
| `canner_type` | String | dial_gauge/weighted_gauge |
| `jar_size` | String | half_pint/pint/quart |
| `jar_count` | Int | |
| `processing_time_min` | Int | |
| `pressure_psi` | Int | For pressure canning |
| `user_elevation` | Int | Feet — affects processing |
| `acidity` | String | high_acid/low_acid/acidified |
| `added_acid` | String | lemon juice/citric acid |
| `ingredients_list` | String | |
| `seal_check` | String | pass/fail |
| `seal_check_date` | Long | |
| `date_processed` | Long | |
| `storage_location` | String | |
| `use_by_date` | Long | |
| `photos` | String | |

**Altitude adjustments (required above 1,000 ft):**
- Water bath: add 5-20 min depending on elevation
- Pressure canner (dial gauge): add 1 PSI per 2,000 ft
- Pressure canner (weighted gauge): use 15 PSI for all elevations >1,000 ft

### Dehydrating

- `batch_id`, `product`, `weight_before_lbs`, `weight_after_lbs`, `dehydrator_temp_F`, `drying_time_hrs`, `pre_treatment` (blanched/dipped/sulfured), `storage_method` (vacuum_sealed/mylar/jar_with_O2_absorber), `date`, `use_by_estimate`

### Fermenting

| Field | Type | Notes |
|-------|------|-------|
| `batch_id` | Long (PK) | |
| `product` | String | sauerkraut/kimchi/pickles/hot sauce/kombucha/etc. |
| `ingredients` | String | |
| `salt_pct` | Double | 2% sauerkraut, 3% cucumbers, 3.5-4% kimchi, 5% sturdy veg |
| `method` | String | dry_salt/wet_brine |
| `brine_ratio` | String | |
| `vessel_type` | String | |
| `airlock` | Boolean | |
| `ferment_temp_F` | Int | Ideal: 65-75°F |
| `start_date` | Long | |
| `end_date` | Long | |
| `ph_readings` | String | JSON with dates. Safety target: <4.6 pH |
| `taste_test_notes` | String | |
| `storage_method` | String | |

### Freezing

- `batch_id`, `item`, `quantity_lbs`, `packaging_method` (vacuum_sealed/freezer_bag/container), `blanched` (Y/N), `date_frozen`, `freezer_id`, `quality_duration_months`, `use_by_date`

### Smoking/Curing

- `batch_id`, `meat_type`, `cut`, `weight`, `cure_recipe`, `cure_type` (#1 nitrite/#2 nitrate/none), `method` (dry_rub/wet_brine/injection), `cure_start`, `cure_end`, `smoke_wood`, `smoke_temp_F` (cold <90°F / hot 225-275°F), `internal_temp`, `duration`, `final_weight`

### Pantry Inventory (Master)

| Field | Type | Notes |
|-------|------|-------|
| `item_id` | Long (PK) | |
| `name` | String | |
| `category` | String | canned/dried/frozen/fermented/smoked/purchased |
| `quantity` | Double | |
| `unit` | String | |
| `batch_id` | Long (FK) | Links to preservation record |
| `storage_location` | String | pantry/root cellar/freezer A/B |
| `date_produced` | Long | |
| `expiration_date` | Long | |
| `status` | String | in_stock/consumed/discarded/gifted |
| `source` | String | homegrown/purchased |

**FIFO alerts:** sorted by date, notifications for approaching expiration.

---

## 6. Land Management

### Property

- `property_id`, `name`, `total_acreage`, `parcel_ids`, `zoning` (agricultural/residential/mixed), `county`, `state_province`, `elevation`, `purchase_date`, `assessed_value`, `tax_parcel_number`

### Fencing

- `fence_id`, `type` (electric/woven wire/board/barbed/high-tensile/cattle panel), `length_ft`, `height_ft`, `install_date`, `material_cost`, `labor_cost`, `condition_rating` (1-5), `linked_paddock_ids`
- **Maintenance log:** date, type, cost

### Structures

- `structure_id`, `type` (barn/coop/shed/greenhouse/root cellar/smokehouse), `name`, `dimensions`, `sq_ft`, `build_date`, `cost`, `condition_rating`, `maintenance_schedule`, `last_maintenance`, `next_maintenance`, `photos`

### Pasture Rotation

| Field | Type | Notes |
|-------|------|-------|
| `paddock_id` | Long (PK) | |
| `name` | String | |
| `acreage` | Double | |
| `assigned_animal_group_id` | Long (FK) | |
| `species` | String | |
| `head_count` | Int | |
| `move_in_date` | Long | |
| `move_out_date` | Long | |
| `planned_rest_days` | Int | |
| `actual_rest_days` | Int | Auto-calculated |
| `forage_type` | String | |
| `forage_height_in` | Double | |
| `forage_condition` | Int | 1-5 |
| `stocking_rate` | Double | Animals per acre |

### Soil Testing

- `soil_test_id`, `date`, `location`, `paddock_or_bed_id`, `depth_in`, `lab_name`, `pH`, `nitrogen_ppm`, `phosphorus_ppm`, `potassium_ppm`, `organic_matter_%`, `CEC`, `calcium_ppm`, `magnesium_ppm`, `texture` (sand/silt/clay %), `recommendations`, `report_url`

### Water Sources

- `water_source_id`, `type` (well/pond/stream/spring/rain catchment/cistern), `capacity_gal`, `flow_rate_gpm`, `depth_ft`, `water_test_date`, `pH`, `coliform_count`, `nitrate_ppm`, `hardness`, `TDS`, `pump_type`, `pump_maintenance_date`

### Composting

- `bin_id`, `type` (tumbler/static pile/windrow/vermicompost), `location`, `volume_cu_ft`, `start_date`, `turn_dates`, `temperature_F`, `moisture_%`, `maturity_stage` (active/curing/finished), `output_volume`, `applied_to`
- **Input log:** date, material, volume (carbon/nitrogen tracking)

### Weather Log

- `date`, `high_temp`, `low_temp`, `rainfall_in`, `snowfall_in`, `humidity_%`, `wind_speed`, `growing_degree_days`, `frost_flag`, `notes`

---

## 7. Finances

### Expense Tracking

| Field | Type | Notes |
|-------|------|-------|
| `expense_id` | Long (PK) | |
| `date` | Long | |
| `vendor` | String | |
| `amount` | Double | |
| `currency` | String | USD/CAD |
| `category` | String | See Schedule F categories below |
| `subcategory` | String | |
| `payment_method` | String | |
| `receipt_photo_url` | String | |
| `tax_deductible` | Boolean | |
| `schedule_f_line` | String | US line number |
| `t2042_line` | String | Canadian line number |
| `enterprise_id` | Long (FK) | Links to specific farm activity |
| `notes` | String | |

### Revenue Tracking

| Field | Type | Notes |
|-------|------|-------|
| `revenue_id` | Long (PK) | |
| `date` | Long | |
| `buyer` | String | |
| `product` | String | |
| `product_category` | String | |
| `quantity` | Double | |
| `unit` | String | dozen/lb/jar/bunch/head |
| `unit_price` | Double | |
| `total` | Double | |
| `payment_method` | String | |
| `payment_status` | String | |
| `sales_channel` | String | farm_stand/farmers_market/CSA/online/wholesale/restaurant |
| `market_name` | String | |
| `notes` | String | |

### US Schedule F Expense Categories (IRS lines 10-32)

Car/truck, chemicals, conservation, custom hire, depreciation/Section 179, employee benefits, **feed** (line 16), fertilizers/lime, freight/trucking, gasoline/fuel/oil, insurance, mortgage interest, other interest, labor hired, pension plans, rent-vehicles, rent-land, repairs/maintenance, **seeds/plants** (line 26), storage, supplies, taxes, utilities, **veterinary/breeding/medicine** (line 31), other

### Canadian T2042 Expense Categories

Containers/twine, fertilizers/lime, pesticides, seeds/plants, feed/supplements/bedding, livestock purchased, veterinary/breeding, machinery repairs, gasoline/diesel, building repairs, crop insurance, custom work, electricity, heating, insurance, office expenses, property taxes, rent, salaries, motor vehicle, small tools, CCA (capital cost allowance)

### US vs. Canada Key Differences

- **US hobby farm rule:** Must profit 3 of 5 consecutive years or risk hobby classification (no expense deductions)
- **Canada restricted farm loss:** If farming ≠ chief income source, max deduction capped at $2,500 + 50% of next $30,000 = $17,500; excess carries forward 20 years
- **US SE tax:** 15.3% on net farm profit (12.4% SS + 2.9% Medicare)
- **Canada:** CPP contributions; GST/HST input tax credits

### Cost-Per-Unit Analysis

- `product`, `time_period`, `feed_cost`, `bedding_cost`, `vet_cost`, `seed_cost`, `fertilizer_cost`, `water_cost`, `packaging_cost`, `labor_hours`, `labor_cost`, `equipment_depreciation`, `processing_cost`, `total_units_produced`, `total_cost`, `cost_per_unit`
- **Example:** eggs/dozen = (feed + bedding + vet + coop depreciation + labor) ÷ dozens produced

### Additional Financial Entities

- **Enterprise budgets:** gross revenue, direct costs, allocated overhead, net profit, margin %, return per labor hour
- **Barter/trade:** date, partner, given/received items + FMV (taxable income)
- **Mileage:** date, origin, destination, purpose, miles, rate ($0.70/mi 2025 US), total deduction
- **Grant tracking:** program (EQIP/CSP/SARE/VAPG), agency, application date, status, award amount, cost share %, contract dates, compliance checks
- **Depreciation:** asset, purchase date, cost basis, useful life, method, annual deduction, accumulated depreciation

---

## 8. Competitor Analysis

**No app combines livestock + garden + food preservation + finances + land management.**

| App | Livestock | Garden | Preservation | Finances | Mobile | Price |
|-----|-----------|--------|-------------|----------|--------|-------|
| **Farmbrite** | ✅ | ⚠️ Basic | ❌ | ✅ Full | ❌ Web | $29-119/mo |
| **FarmKeep** | ✅ | ❌ | ❌ | ⚠️ Basic | ✅ | Free-$10/mo |
| **farmOS** | ⚠️ | ⚠️ | ❌ | ❌ | ❌ Self-hosted | Free |
| **LiteFarm** | ❌ | ✅ Crops | ❌ | ⚠️ | ⚠️ Web | Free |
| **AgriWebb** | ✅ Cattle/sheep | ❌ | ❌ | ✅ | ✅ | $30+/mo |
| **Planter** | ❌ | ✅ | ❌ | ❌ | ✅ | Free-$4/mo |
| **GrowVeg** | ❌ | ✅ Best planner | ❌ | ❌ | ❌ Web | $29/yr |
| **HiveTracks** | ❌ Bees only | ❌ | ❌ | ❌ | ✅ | Free-$70/yr |
| **OSU Canning Timer** | ❌ | ❌ | ⚠️ Timer only | ❌ | ✅ | Free |

### #1 Threat: FarmKeep

Mobile-first, $10/mo, 4.8/5 ratings, 1,200+ species/breeds. **Zero garden features, no food preservation.** If they add crops before Furrow launches, they own the space.

### 5 Gaps Furrow Exploits

1. **Food preservation tracking** — completely unserved
2. **Integrated garden + livestock** — nobody does both well at homesteader pricing
3. **Cross-domain connections** — garden → preservation → pantry → financial ROI
4. **Self-sufficiency planning** — "How much do we need to grow for our family?"
5. **Regulatory compliance** — state-specific egg laws, cottage food caps, meat exemptions

### Recommended Price Point

$5-15/month — undercuts Farmbrite ($59+/mo), outscopes FarmKeep.

---

## 9. Regulations & Compliance

### US Egg Selling

Key variables by state: licensing thresholds (often 100-500 hens), grading requirements, labeling (producer name/address, "ungraded" label, date, safe handling), refrigeration (35-45°F), washing rules, sale venue restrictions.

- **FDA Egg Safety Rule** kicks in above 3,000 hens
- Most permissive: food freedom states (WY, ND, UT, ME, MT)
- **App fields:** state, flock_size, license_number, license_expiration, sales_volume_running_total, sale_venue_type, label_template

### US Cottage Food Laws

All 50 states have programs. Key variables:
- **Revenue caps:** range from none (AK, AZ, AR) to $250,000 (FL); common $10K-$50K
- **Allowed products:** all allow baked goods/jams/candy; some allow pickles, ferments, acidified
- **Sales venues:** most restrict to direct-to-consumer
- **Label disclaimer:** "Made in a home kitchen not inspected by [state dept]"
- **5 food freedom states:** WY, ND, UT, ME, MT — allow almost any homemade food
- **App fields:** state, permit_number, annual_revenue_running_total, revenue_cap, allowed_products_list, label_templates, food_safety_course_date

### US Meat Processing (Poultry Exemptions)

| Exemption | Limit/yr | Can sell to | Key requirement |
|-----------|----------|-----------|-----------------|
| Personal use | Unlimited | Nobody | Own birds, own premises |
| 1,000-bird | ≤1,000 | Direct consumer | Own birds, own premises |
| 20,000-bird | ≤20,000 | Consumer, restaurant, retail | Own birds, labeled |
| Small enterprise | ≤20,000 | Same + can buy birds | Full labeling |

**Red meat has NO small-producer exemption** — requires USDA or state inspection for any commercial sale.

### USDA Organic Certification

- 36-month transition for land
- $5,000 threshold: operations >$5K/yr must be certified
- Average cost: ~$2,800/yr (USDA reimburses up to 75%, max $750/yr via OCCSP)
- Records retained 5 years

### Canadian Regulations

- **Supply management:** dairy, chicken, turkey, eggs require quota for commercial scale. Quota-exempt thresholds vary by province (e.g. BC: 99 hens/200 broilers, ON: 99 hens/300 broilers)
- **Ungraded eggs:** can sell at farm gate/farmers markets in most provinces
- **Meat:** provincial inspection for in-province; CFIA for interprovincial
- **Organic:** Canada Organic Regime (CFIA); 6 provinces have own organic regs; 36-month transition

### Compliance Module Fields

- `license_permit_registry`: type, number, authority, issue date, expiration, renewal reminder
- `inspection_log`: date, inspector, agency, outcome, follow-up items
- `sales_tracker`: running totals against annual caps with alerts
- `label_manager`: per-product templates with required state/province fields
- `document_storage`: certificates, permits, inspection reports, food safety course completions

---

## 10. Market Size

### North America

- **US:** 1.9M farms (2022 USDA Census); 85% are small family farms = ~1.6M
- **Canada:** ~189,000 farms
- **Total:** ~2.1M farms

### The Broader Homesteading Funnel

- **11M US households** own backyard chickens
- **39M US households** grow vegetables
- **115K-125K US beekeepers** (94.5% are hobbyists with <50 hives)
- **18.3M new gardeners** entered since 2021; 75% plan to continue

### TAM/SAM/SOM

| Tier | Definition | Size | Revenue potential |
|------|-----------|------|-------------------|
| TAM | All NA food-producing households | 40-50M | $2-5B at $50-100/yr |
| SAM | Active producers who sell/need records | 2-5M | $120-600M at $60-120/yr |
| SOM | Realistic years 1-3 capture | 5K-50K paying | $360K-$4.8M at ~$8/mo |

---

## Module Enable/Disable Architecture

Users choose their modules in Settings. The app stores all data regardless — modules just toggle UI visibility.

### Onboarding Multi-Select

"What do you manage?"
- [ ] Garden
- [ ] Poultry (chickens, ducks, turkeys, quail)
- [ ] Bees
- [ ] Other Livestock (goats, cattle, pigs, sheep, rabbits)
- [ ] Orchard / Fruit Trees
- [ ] Food Preservation
- [ ] Land & Property

### Settings Toggle

Each module has an enable/disable switch. Disabled modules:
- Hidden from bottom nav
- Hidden from home screen cards
- Data preserved in database (not deleted)
- Re-enabling restores all data

### Bottom Nav Behavior

- 1-3 modules: show all in bottom nav
- 4+ modules: show Home + top 3 most-used, with "More" overflow
- Home screen always shows cards for all enabled modules

---

## Cross-Domain Links (Furrow's Killer Feature)

Every entity connects through a shared `enterprise_id`:

```
Garden Harvest → Preservation Batch → Pantry Inventory → Revenue (if sold)
     ↓                                      ↓
  Feed Cost ← Animal ← Production Log → Revenue (eggs/milk/meat)
     ↓
  Financial Summary → Cost per unit → "Is this worth it?"
```

This is what no competitor does. Every egg collected, jar canned, and dollar spent rolls up into per-activity profitability analysis.