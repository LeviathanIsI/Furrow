# Furrow App — Comprehensive Reference Data Research
## Location-Aware Intelligence System: All US Regions

---

# TABLE OF CONTENTS

1. [USDA Hardiness Zones & Frost Dates](#1-usda-hardiness-zones--frost-dates)
2. [Garden Module: Plant Database](#2-garden-module-plant-database)
3. [Garden Module: Planting Windows by Zone Group](#3-garden-module-planting-windows-by-zone-group)
4. [Garden Module: Companion Planting](#4-garden-module-companion-planting)
5. [Garden Module: Container/Grow Bag Suitability](#5-garden-module-containergrow-bag-suitability)
6. [Poultry Module: Chicken Breed Database](#6-poultry-module-chicken-breed-database)
7. [Poultry Module: Meat Bird Reference](#7-poultry-module-meat-bird-reference)
8. [Bee Module: Bee Race Database](#8-bee-module-bee-race-database)
9. [Bee Module: Regional Beekeeping Calendars](#9-bee-module-regional-beekeeping-calendars)
10. [Bee Module: Regional Nectar Flow Windows](#10-bee-module-regional-nectar-flow-windows)

---

# 1. USDA Hardiness Zones & Frost Dates

## Zone Definitions (2023 Map, 1991-2020 data)

| Zone | Avg Annual Min Temp (°F) | Typical US Regions |
|------|--------------------------|-------------------|
| 3a   | -40 to -35 | Northern MN, ND, MT, interior AK |
| 3b   | -35 to -30 | Northern WI, VT, NH, ME |
| 4a   | -30 to -25 | MN (central), WI, MI (UP), upstate NY |
| 4b   | -25 to -20 | Minneapolis, Milwaukee, southern VT/NH |
| 5a   | -20 to -15 | Southern MN, IA, northern IL, CO front range |
| 5b   | -15 to -10 | Chicago, Denver, southern New England, Appalachian highlands |
| 6a   | -10 to -5 | St. Louis, Cincinnati, NJ, southern PA |
| 6b   | -5 to 0 | Nashville, mid-Atlantic coast, Pacific NW inland |
| 7a   | 0 to 5 | Memphis, Norfolk VA, Seattle, Portland OR |
| 7b   | 5 to 10 | Atlanta, Dallas (north), Coastal PNW |
| 8a   | 10 to 15 | Austin, coastal Carolinas, Portland OR |
| 8b   | 15 to 20 | North FL panhandle, Gulf coast, Sacramento |
| 9a   | 20 to 25 | Central FL, Houston, Phoenix, coastal CA |
| 9b   | 25 to 30 | South FL, Tampa, LA coast, San Diego |
| 10a  | 30 to 35 | Miami, Key West, southernmost TX, coastal SoCal |
| 10b  | 35 to 40 | South FL Keys, HI lowlands |
| 11a  | 40 to 45 | Key West tip, HI |
| 11b+ | 45+ | Hawaii, Puerto Rico, US Virgin Islands |

## Average Frost Dates by Zone Group

| Zone Group | Last Spring Frost | First Fall Frost | Frost-Free Days |
|------------|-------------------|------------------|-----------------|
| 3 (Cold)   | May 15 - June 1   | Sep 1 - Sep 15   | 90-120 |
| 4 (Cold)   | May 1 - May 15    | Sep 15 - Oct 1   | 120-150 |
| 5 (Moderate)| Apr 15 - May 15  | Oct 1 - Oct 15   | 150-180 |
| 6 (Moderate)| Apr 1 - Apr 15   | Oct 15 - Nov 1   | 180-200 |
| 7 (Warm)   | Mar 15 - Apr 1    | Nov 1 - Nov 15   | 200-230 |
| 8 (Warm)   | Mar 1 - Mar 15    | Nov 15 - Dec 1   | 240-270 |
| 9 (Hot)    | Jan 15 - Feb 15   | Dec 1 - Dec 15   | 270-330 |
| 10+ (Tropical)| Frost-free     | Frost-free        | 365 |

**App implementation**: Store zone and derive frost dates from lookup table. Use zone group (Cold 3-4, Moderate 5-6, Warm 7-8, Hot 9-10, Tropical 11+) for recommendation logic.

---

# 2. Garden Module: Plant Database

## Complete Vegetable Reference (40+ plants)

Format: Plant | Season Type | Days to Maturity | Min Zone | Max Zone | Sun | Water | Container Suitable | Min Container Size

### Warm-Season Vegetables (plant AFTER last frost)

| Plant | Season | DTM | Min Zone | Max Zone | Sun | Water | Container? | Min Size |
|-------|--------|-----|----------|----------|-----|-------|------------|----------|
| Tomato (determinate) | Warm | 60-80 | 3 | 11 | Full | Regular | Yes | 5 gal |
| Tomato (indeterminate) | Warm | 70-90 | 4 | 11 | Full | Regular | Yes | 10 gal |
| Pepper (sweet) | Warm | 65-80 | 4 | 11 | Full | Regular | Yes | 5 gal |
| Pepper (hot) | Warm | 70-90 | 4 | 11 | Full | Moderate | Yes | 5 gal |
| Cucumber | Warm | 55-65 | 3 | 11 | Full | Regular | Yes | 10 gal |
| Zucchini/Summer Squash | Warm | 40-55 | 3 | 11 | Full | Regular | Yes | 10 gal |
| Winter Squash | Warm | 85-100 | 3 | 10 | Full | Regular | Marginal | 15 gal |
| Pumpkin | Warm | 100-120 | 3 | 10 | Full | Regular | No | N/A |
| Watermelon | Warm | 80-90 | 4 | 11 | Full | Regular | Marginal | 15 gal |
| Cantaloupe/Melon | Warm | 70-80 | 4 | 10 | Full | Regular | Marginal | 10 gal |
| Eggplant | Warm | 75-90 | 5 | 11 | Full | Regular | Yes | 5 gal |
| Okra | Warm | 50-65 | 5 | 11 | Full | Low | Yes | 10 gal |
| Sweet Potato | Warm | 90-150 | 5 | 11 | Full | Moderate | Marginal | 15 gal |
| Corn (sweet) | Warm | 60-90 | 3 | 10 | Full | Regular | Marginal | 15 gal (3 plants min) |
| Bush Beans | Warm | 50-60 | 3 | 11 | Full | Regular | Yes | 5 gal |
| Pole Beans | Warm | 60-70 | 3 | 11 | Full | Regular | Yes | 7 gal |
| Lima Beans | Warm | 75-85 | 5 | 11 | Full | Regular | Yes | 5 gal |
| Southern Peas (Cowpeas) | Warm | 60-70 | 7 | 11 | Full | Low | Yes | 5 gal |

### Cool-Season Vegetables (plant BEFORE last frost or in fall)

| Plant | Season | DTM | Min Zone | Max Zone | Sun | Water | Container? | Min Size |
|-------|--------|-----|----------|----------|-----|-------|------------|----------|
| Lettuce (leaf) | Cool | 40-60 | 3 | 11 | Partial-Full | Regular | Yes | 3 gal |
| Lettuce (head) | Cool | 60-85 | 3 | 10 | Partial-Full | Regular | Yes | 5 gal |
| Spinach | Cool | 40-50 | 3 | 10 | Partial-Full | Regular | Yes | 3 gal |
| Kale | Cool | 55-75 | 3 | 11 | Full | Regular | Yes | 5 gal |
| Swiss Chard | Cool | 55-65 | 3 | 11 | Partial-Full | Regular | Yes | 5 gal |
| Collard Greens | Cool | 60-80 | 5 | 11 | Full | Regular | Yes | 5 gal |
| Mustard Greens | Cool | 40-50 | 3 | 11 | Full | Regular | Yes | 3 gal |
| Broccoli | Cool | 60-80 | 3 | 10 | Full | Regular | Yes | 5 gal |
| Cauliflower | Cool | 55-80 | 3 | 9 | Full | Regular | Yes | 5 gal |
| Cabbage | Cool | 65-80 | 3 | 10 | Full | Regular | Yes | 5 gal |
| Brussels Sprouts | Cool | 90-110 | 3 | 8 | Full | Regular | Marginal | 7 gal |
| Peas (snap/snow) | Cool | 58-72 | 3 | 9 | Full | Regular | Yes | 5 gal |
| Radish | Cool | 25-30 | 3 | 11 | Full-Partial | Regular | Yes | 2 gal |
| Carrot | Cool | 70-80 | 3 | 10 | Full | Regular | Yes | 7 gal (deep) |
| Beet | Cool | 55-65 | 3 | 10 | Full | Regular | Yes | 5 gal |
| Turnip | Cool | 40-60 | 3 | 10 | Full | Regular | Yes | 5 gal |
| Onion (bulb) | Cool | 90-120 | 3 | 10 | Full | Moderate | Yes | 5 gal |
| Garlic | Cool | 90-120 (fall plant) | 3 | 10 | Full | Moderate | Yes | 5 gal |
| Potato | Cool | 70-120 | 3 | 10 | Full | Regular | Yes | 10 gal |
| Kohlrabi | Cool | 50-70 | 3 | 9 | Full | Regular | Yes | 3 gal |

### Herbs

| Plant | Season | DTM | Min Zone | Max Zone | Sun | Water | Container? | Min Size |
|-------|--------|-----|----------|----------|-----|-------|------------|----------|
| Basil | Warm | 50-75 | 4 | 11 | Full | Regular | Yes | 1 gal |
| Cilantro | Cool | 45-70 | 3 | 10 | Partial | Regular | Yes | 1 gal |
| Dill | Cool | 40-60 | 3 | 10 | Full | Moderate | Yes | 3 gal |
| Parsley | Cool | 70-90 | 3 | 11 | Partial-Full | Regular | Yes | 1 gal |
| Oregano | Warm (perennial) | 80-90 | 4 | 10 | Full | Low | Yes | 1 gal |
| Thyme | Warm (perennial) | 85-95 | 4 | 10 | Full | Low | Yes | 1 gal |
| Rosemary | Warm (perennial) | 80-100 | 7 | 11 | Full | Low | Yes | 2 gal |
| Sage | Warm (perennial) | 75-85 | 4 | 10 | Full | Low | Yes | 2 gal |
| Mint | Cool (perennial) | 60-90 | 3 | 11 | Partial | Regular | Yes | 2 gal |
| Chives | Cool (perennial) | 60-90 | 3 | 10 | Full-Partial | Moderate | Yes | 1 gal |

---

# 3. Garden Module: Planting Windows by Zone Group

**System**: Each plant has planting windows defined relative to zone group. Windows are expressed as month ranges for OUTDOOR planting (transplant or direct sow).

## Spring Planting Windows (outdoor, primary season)

| Plant | Zone 3-4 | Zone 5-6 | Zone 7-8 | Zone 9-10 | Zone 11+ |
|-------|----------|----------|----------|-----------|----------|
| Tomato | Late May-Jun | May-Jun | Apr-May | Feb-Apr | Year-round |
| Pepper | Late May-Jun | May-Jun | Apr-May | Feb-Apr | Year-round |
| Cucumber | Jun | May-Jun | Apr-May | Mar-Apr | Year-round |
| Squash (summer) | Jun | May-Jun | Apr-May | Mar-Apr | Year-round |
| Squash (winter) | Jun | May-Jun | Apr-May | Feb-Mar | Oct-Feb |
| Beans (bush) | Jun | May-Jun | Apr-May | Mar-Apr, Aug-Sep | Year-round |
| Corn | Jun | May-Jun | Apr-May | Mar | N/A (not ideal) |
| Eggplant | Jun | Late May-Jun | Apr-May | Feb-Apr | Year-round |
| Okra | N/A | Jun | May-Jun | Mar-May | Year-round |
| Sweet Potato | N/A | Jun | May-Jun | Mar-May | Year-round |
| Watermelon | Jun (short season var.) | May-Jun | Apr-May | Mar-Apr | Year-round |
| Lettuce | May-Jun, Aug | Apr-May, Aug-Sep | Mar-Apr, Sep-Oct | Oct-Mar | Oct-Mar |
| Spinach | Apr-May, Aug | Mar-Apr, Sep | Feb-Mar, Oct-Nov | Oct-Feb | Nov-Feb |
| Kale | Apr-May, Aug | Mar-May, Aug-Sep | Feb-Mar, Sep-Oct | Oct-Mar | Nov-Mar |
| Broccoli | May (transplant) | Apr-May, Aug | Feb-Mar, Sep-Oct | Oct-Jan | Nov-Feb |
| Peas | Apr-May | Mar-Apr | Feb-Mar, Oct-Nov | Oct-Feb | Nov-Feb |
| Carrots | May-Jun | Apr-May, Aug | Feb-Mar, Sep-Oct | Oct-Feb | Nov-Mar |
| Beets | May-Jun | Apr-May, Aug-Sep | Feb-Mar, Sep-Oct | Oct-Feb | Nov-Mar |
| Radish | May-Jun, Aug | Apr-May, Aug-Sep | Feb-Apr, Sep-Nov | Oct-Mar | Year-round |
| Potato | May | Apr-May | Feb-Mar | Jan-Feb | Oct-Jan |
| Onion | May (sets) | Apr (sets/transplant) | Feb-Mar | Oct-Dec | Oct-Dec |
| Garlic | Sep-Oct (fall) | Oct-Nov (fall) | Oct-Nov (fall) | Nov-Dec (fall) | Nov-Jan |
| Basil | Jun | Late May-Jun | Apr-May | Mar-Apr | Year-round |
| Cilantro | May-Jun | Apr-May, Sep | Feb-Mar, Oct-Nov | Oct-Feb | Nov-Mar |

## Fall Planting Windows

| Zone Group | Fall Planting Start | Crops |
|------------|-------------------|-------|
| 3-4 | Jul-Aug | Fast-maturing cool crops (lettuce, radish, kale, spinach) |
| 5-6 | Aug-Sep | Most cool-season crops, start brassicas indoors Jul-Aug |
| 7-8 | Sep-Oct | Full cool-season garden (brassicas, greens, root crops) |
| 9-10 | Sep-Nov | Primary garden season begins. Warm crops Sep-Oct, Cool crops Oct-Dec |
| 11+ | Sep-Nov | Year-round production, avoid peak summer heat for cool crops |

**Florida-specific note (Zone 9-10)**: Fall (Sep-Mar) is the PRIMARY vegetable growing season. Cool crops Oct-Feb, warm crops Feb-Apr. Summer (Jun-Aug) is the dead zone — only okra, sweet potatoes, southern peas survive the heat.

---

# 4. Garden Module: Companion Planting

## Companion Matrix (evidence-based where possible)

| Plant | Good Companions | Bad Companions | Why |
|-------|----------------|----------------|-----|
| Tomato | Basil, marigold, carrot, parsley, borage | Brassicas, fennel, walnut | Basil repels pests; marigold reduces whitefly |
| Pepper | Basil, carrot, onion, spinach, tomato | Fennel, kohlrabi | Similar needs, basil pest repellent |
| Cucumber | Beans, corn, peas, radish, sunflower | Potato, aromatic herbs | Beans fix nitrogen; sunflower provides structure |
| Squash/Zucchini | Beans, corn, peas, radish, marigold | Potato | Three Sisters method (corn/beans/squash) |
| Beans | Corn, squash, cucumber, carrot, beet | Onion, garlic, chives, fennel | Beans fix nitrogen; alliums inhibit bean growth |
| Corn | Beans, squash, cucumber, peas | Tomato | Three Sisters; share pest pressure with tomato |
| Lettuce | Carrot, radish, strawberry, chive, mint | Celery | Tall neighbors provide shade in heat |
| Kale/Broccoli/Cabbage | Onion, garlic, beet, celery, sage, rosemary | Strawberry, tomato, pole bean | Sage/rosemary repel cabbage moth |
| Carrot | Tomato, onion, leek, rosemary, sage | Dill, parsnip | Rosemary/sage repel carrot fly |
| Onion/Garlic | Carrot, beet, lettuce, tomato, cabbage | Beans, peas | Alliums repel many pests; inhibit legume growth |
| Peas | Carrot, turnip, radish, cucumber, corn | Onion, garlic | Peas fix nitrogen |
| Potato | Beans, corn, cabbage, horseradish | Tomato, squash, cucumber | Same family (nightshade) shares diseases |
| Beet | Onion, cabbage family, lettuce, garlic | Pole bean, mustard | Different root depths, don't compete |
| Radish | Lettuce, peas, beans, cucumber, spinach | Hyssop | Fast growth between slow crops |
| Basil | Tomato, pepper | Rue, sage | Improves tomato flavor, repels pests |
| Marigold | Nearly everything (border plant) | None significant | Repels nematodes, whiteflies, aphids |

---

# 5. Garden Module: Container/Grow Bag Suitability

## 10-Gallon Grow Bag Capacity (Josh's primary container)

| Plant | Plants per 10-gal Bag | Notes |
|-------|----------------------|-------|
| Tomato (indeterminate) | 1 | Needs stake/cage support |
| Tomato (determinate) | 1-2 | Bush type, less support needed |
| Pepper (any) | 2-3 | Space evenly |
| Cucumber | 2-3 | Provide trellis |
| Zucchini | 1 | Large leaves need space |
| Eggplant | 2-3 | |
| Bush Beans | 8-10 | Dense planting OK |
| Lettuce (leaf) | 8-12 | Great for successive cuts |
| Kale | 3-4 | |
| Swiss Chard | 5 | |
| Spinach | 8-12 | |
| Broccoli | 2-3 | |
| Carrots | 20-24 | Use deep bag, thin to 2" apart |
| Beets | 20-24 | Thin to 2" apart |
| Radish | 24+ | Fast crop, succession plant |
| Potato | 2-3 | Mound soil as they grow |
| Herbs (basil, etc.) | 4-6 | Mix herbs in one bag |
| Onion | 12-15 | Space 3" apart |
| Garlic | 12-15 | Space 3" apart |

---

# 6. Poultry Module: Chicken Breed Database

## Complete Breed Reference (25 breeds)

**Climate Categories**: 
- **Hot** = Thrives in sustained 90°F+, large comb for heat dissipation, light feathering
- **Cold** = Thrives in sub-zero, small/pea/rose comb (frostbite resistant), dense feathering
- **Both** = Tolerates wide range but excels in neither extreme
- **Mixed** = Survives both but struggles in true extremes

| Breed | Eggs/Year | Egg Color | Egg Size | Weight (hen) | Purpose | Temperament | Heat Tol | Cold Tol | Broody | Comb Type |
|-------|-----------|-----------|----------|--------------|---------|-------------|----------|----------|--------|-----------|
| Leghorn (White) | 280-320 | White | Large-XL | 4.5 lb | Layer | Active, flighty | Excellent | Poor (lg comb) | Rarely | Single (lg) |
| Rhode Island Red | 250-300 | Brown | Large | 6.5 lb | Dual | Hardy, friendly | Good | Good | Sometimes | Single |
| Australorp | 250-280 | Brown | Medium-Lg | 6.5 lb | Dual | Calm, docile | Good | Very Good | Sometimes | Single |
| Plymouth Rock (Barred) | 200-280 | Brown | Large | 7 lb | Dual | Friendly, calm | Good | Good | Sometimes | Single |
| Sussex (Speckled) | 250-300 | Cream-Brown | Medium-Lg | 7 lb | Dual | Curious, gentle | Fair | Good | Sometimes | Single |
| Wyandotte | 180-220 | Brown | Large | 6.5 lb | Dual | Calm, vocal | Fair | Very Good | Sometimes | Rose |
| Orpington (Buff) | 180-200 | Brown | Large | 8 lb | Dual | Very gentle | Poor | Very Good | Often | Single |
| Brahma | 150-200 | Brown | Medium-Lg | 9 lb | Dual/Meat | Gentle, calm | Poor | Excellent | Often | Pea |
| Cochin | 120-160 | Brown | Small-Med | 8.5 lb | Ornamental | Very docile | Poor | Very Good | Very Often | Single (sm) |
| New Hampshire Red | 200-250 | Brown | Large | 6.5 lb | Dual | Competitive | Good | Good | Sometimes | Single |
| Easter Egger | 200-250 | Blue/Green/Pink | Medium-Lg | 5-6 lb | Layer | Variable | Good | Good | Rarely | Pea/Various |
| Ameraucana | 180-200 | Blue | Medium-XL | 5.5 lb | Layer | Calm | Good | Very Good | Sometimes | Pea |
| Delaware | 200-280 | Brown | Lg-Jumbo | 6.5 lb | Dual | Calm, docile | Good | Good | Sometimes | Single |
| Dominique | 230-275 | Brown | Medium | 5 lb | Dual | Calm | Fair | Very Good | Often | Rose |
| Buckeye | 180-220 | Brown | Medium | 6.5 lb | Dual | Calm, curious | Fair | Excellent | Sometimes | Pea |
| Chantecler | 200-220 | Brown | Small-Lg | 6.5 lb | Dual | Calm | Fair | Excellent | Often | Cushion |
| Naked Neck (Turken) | 200-250 | Brown | Large | 5 lb | Dual | Curious, adaptable | Excellent | Good (surprisingly) | Sometimes | Single |
| Egyptian Fayoumi | 150-200 | White/Cream | Small | 3.5 lb | Layer | Wild, flighty | Excellent | Poor | Rarely | Single (lg) |
| Welsummer | 160-200 | Dark Brown | Medium-Lg | 6 lb | Dual | Docile, friendly | Fair | Good | Sometimes | Single |
| Marans | 150-200 | Very Dark Brown | Medium-Lg | 7 lb | Dual | Gentle, quiet | Fair | Good | Sometimes | Single |
| Barnevelder | 180-220 | Dark Brown | Large | 6.5 lb | Dual | Calm | Fair | Fair | Variable | Single |
| Ancona | 220-300 | White | Medium-XL | 4.5 lb | Layer | Flighty, active | Good | Good (not comb) | Rarely | Single |
| Cornish Cross | N/A (meat) | N/A | N/A | 8 lb (6-8 wks) | Meat Only | Sedentary | Poor | Poor | N/A | Single |
| Freedom Ranger | N/A (meat) | N/A | N/A | 5-6 lb (9-11 wks) | Meat Only | Active, forage | Good | Fair | N/A | Single |
| Silkie | 100-120 | Cream/Tinted | Small | 2-3 lb | Ornamental | Very gentle | Fair | Poor | Very Often | Walnut |

## Climate Recommendation Matrix

| Climate Category | Zone Range | Best Breeds | Acceptable | Avoid |
|-----------------|-----------|-------------|------------|-------|
| **Extreme Cold** (zone 3-4) | -40 to -20°F winters | Chantecler, Buckeye, Dominique, Wyandotte, Ameraucana | Brahma, Orpington, Plymouth Rock, RIR, Australorp | Leghorn (lg comb frostbite), Fayoumi, Naked Neck, Silkie |
| **Cold** (zone 5-6) | -20 to -5°F winters | Wyandotte, Plymouth Rock, RIR, Australorp, Dominique, Buckeye | Most dual-purpose breeds | Fayoumi, Naked Neck |
| **Moderate** (zone 7) | 0 to 10°F winters | Any dual-purpose breed excels here | All breeds acceptable | None specifically |
| **Warm** (zone 8) | Mild winters, hot summers | RIR, Australorp, Plymouth Rock, Easter Egger, Delaware | Most breeds survive | Brahma, Cochin (too hot) |
| **Hot & Humid** (zone 9-10) | Sustained 90°F+, humid | Leghorn, Naked Neck, Fayoumi, Easter Egger | RIR, Australorp, Plymouth Rock | Brahma, Orpington, Cochin, Silkie |
| **Hot & Arid** (zone 9-10 desert) | Sustained 100°F+, dry | Leghorn, Naked Neck, Fayoumi, Ancona | RIR, Easter Egger | All heavy-feathered breeds |

---

# 7. Poultry Module: Meat Bird Reference

| Breed | Ready in | Live Weight | Dress Weight | Climate Tolerance | Notes |
|-------|----------|-------------|--------------|-------------------|-------|
| Cornish Cross | 6-8 weeks | 8-10 lb | 5-7 lb | Poor (any extreme) | Industry standard, fastest growth, needs careful management |
| Freedom Ranger | 9-11 weeks | 5-7 lb | 4-5 lb | Good | Better forager, more flavor, hardier |
| Red Ranger | 10-12 weeks | 6-8 lb | 4-6 lb | Good | Similar to Freedom Ranger |
| Dual-Purpose (e.g. Plymouth Rock cockerels) | 16-20 weeks | 7-8 lb | 4-5 lb | Varies by breed | Slower, leaner, more flavorful |

**Processing**: Atlanta Poultry Processing (full-service), or home processing. USDA exemption: up to 1,000 birds/year for personal use without inspection. Check state laws for sale limits.

---

# 8. Bee Module: Bee Race Database

## Complete Race Comparison (7 races)

| Race | Temperament | Honey Production | Cold Hardy | Heat Tolerant | Mite Resistance | Swarming | Overwintering | Spring Buildup | Robbing | Propolis |
|------|-------------|-----------------|------------|---------------|-----------------|----------|---------------|----------------|---------|----------|
| **Italian** (Apis mellifera ligustica) | Very gentle | High | Fair | Good | Low | Low | Fair (large cluster, uses more stores) | Moderate | Prone to robbing others | Low |
| **Carniolan** (A.m. carnica) | Very gentle | High | Excellent | Poor-Fair | Moderate | HIGH | Excellent (small cluster, conserves stores) | Rapid | Low | Low |
| **Russian** (A.m. hybrid) | Moderate-Defensive | Moderate-High | Excellent | Good | High (VSH trait) | High | Excellent | Slow | Low | Moderate |
| **Buckfast** (hybrid) | Gentle | High | Good | Good | Good (tracheal mite esp.) | Low | Good | Moderate-Fast | Low | Low |
| **Saskatraz** (hybrid) | Moderate | High | Good-Excellent | Good | High (varroa + tracheal) | Moderate | Good | Moderate | Low | High |
| **Caucasian** (A.m. caucasica) | Gentle | Moderate | Good | Fair | Low | Low | Good | Slow | Low | Very High |
| **German/Dark** (A.m. mellifera) | Aggressive | Moderate | Excellent | Poor | Low | Moderate | Excellent | Slow | N/A | Moderate |

## Climate Recommendation Matrix

| Region/Climate | Best Choice | Second Choice | Notes |
|----------------|-------------|---------------|-------|
| **Northeast** (zones 5-7, cold winters) | Carniolan | Russian, Buckfast | Fast spring buildup critical for short season; excellent overwinter |
| **Upper Midwest** (zones 3-5, extreme cold) | Carniolan, Russian | Saskatraz | Overwintering ability paramount; need 80-100 lbs stores |
| **Southeast** (zones 7-9, hot & humid) | Italian | Saskatraz, Buckfast | Heat tolerance + high production; mite pressure year-round |
| **Florida** (zones 9-10, subtropical) | Italian | Saskatraz | Year-round brood cycle; SHB pressure high; Africanized risk in south |
| **Pacific Northwest** (zones 7-8, cool & wet) | Carniolan | Buckfast | Forages in cool/wet weather; cool/wet winters |
| **Mountain West** (zones 4-6, dry, variable) | Italian | Buckfast, Saskatraz | Hot dry summers + cold winters; versatility needed |
| **Southwest/Desert** (zones 8-10, hot & arid) | Italian | Saskatraz | Extreme heat management critical; limited nectar flows |
| **Great Plains** (zones 4-6, continental) | Italian | Carniolan | Cold winters but hot summers; wind exposure |
| **Mid-Atlantic** (zones 6-7, moderate) | Italian or Carniolan | Any | Most forgiving climate; any race works |
| **Texas/Gulf** (zones 8-9, hot & humid) | Italian | Saskatraz | Africanized risk in south TX; mite pressure constant |

**First-year recommendation for ALL regions**: Italian (most forgiving, gentlest, widely available, well-documented management practices).

---

# 9. Bee Module: Regional Beekeeping Calendars

## Three-Region Calendar (Cold / Moderate / Warm)

### Cold Regions (Zones 3-5: Northern US, Upper Midwest, Northern New England)

| Month | Hive Activity | Beekeeper Tasks |
|-------|--------------|-----------------|
| Jan | Clustered, minimal activity, cleansing flights on warm days | Don't open. Heft for weight. Emergency feed (sugar board/fondant) if light. |
| Feb | Queen may start laying again late month. Stores dwindling. | Most colonies that starve die now. Check weight. Feed pollen patties if needed. |
| Mar | Brood rearing increasing. First pollen (pussy willow, maple). | Feed 1:1 syrup if stores low. Do NOT open until 50°F+. |
| Apr | Rapid buildup begins. Dandelions, fruit trees blooming. | First full inspection when temps allow. Check queen, brood pattern, disease. Varroa test. |
| May | Swarm season begins. Population booming. Nectar flow starts. | Inspect every 7-10 days for swarm cells. Add supers. Make splits if desired. |
| Jun | Peak nectar flow (clover, basswood). Maximum population. | Add supers as needed. Monitor for swarming. Peak honey production. |
| Jul | Nectar flow may taper. Goldenrod flow late month in some areas. | Harvest spring honey. Varroa test. Continue swarm management. |
| Aug | Varroa population peaks. Winter bees being raised. Population declining. | CRITICAL: Treat for varroa NOW. This is the most important treatment window. |
| Sep | Dearth possible. Robbing risk. Queen reduces laying. | Final varroa treatment. Remove empty supers. Feed 2:1 syrup for winter stores. |
| Oct | Cluster forming. Drones expelled. Final foraging on asters/goldenrod. | Install mouse guards. Ensure 80-100 lbs stores. Wrap hives in cold climates. |
| Nov | Fully clustered. No inspections. | Ensure top ventilation. Windbreak if exposed. |
| Dec | Deep cluster. No activity except rare warm-day flights. | Leave alone. Plan for next year. Order equipment. |

### Moderate Regions (Zones 6-7: Mid-Atlantic, Central US, Pacific NW)

| Month | Hive Activity | Beekeeper Tasks |
|-------|--------------|-----------------|
| Jan | Clustered but may fly on warm days. | Check stores. Emergency feed if needed. Order bees/equipment for spring. |
| Feb | Brood rearing starting. First pollen (maple, crocus). | Pollen patties if needed. Quick check on warm days (50°F+). |
| Mar | Rapid buildup. Dandelions, fruit trees. First major pollen flow. | Full inspection. Reverse brood boxes if needed. Feed if light. Varroa test. |
| Apr | Swarm season! Strong colonies may be ready to split. | Inspect weekly for swarm cells. Add first supers. Make nucs/splits. |
| May | Peak buildup. Major nectar flow begins (locust, tulip poplar, clover). | Supers on. This is your honey month (many regions). Monitor crowding. |
| Jun | Nectar flow continues or peaks. Honey production high. | Add supers. May harvest spring honey mid-month in some areas. |
| Jul | Nectar flow ending in most areas. Summer dearth possible. | Harvest honey. Begin varroa treatment plan. Provide water. |
| Aug | Varroa critical. Winter bees being raised. | TREAT FOR VARROA. Requeen if needed. Feed if stores low. |
| Sep | Fall nectar flow (goldenrod, aster) in some areas. | Complete varroa treatment. Assess stores. Begin fall feeding (2:1 syrup). |
| Oct | Colony contracting. Drones expelled. | Mouse guards. Ensure 60-80 lbs stores. Reduce entrances. |
| Nov | Clustered. Minimal activity. | Ensure ventilation. Leave alone. |
| Dec | Dormant. | Plan next year. |

### Warm Regions (Zones 8-10: Southeast, Gulf Coast, Florida, SoCal, Desert SW)

| Month | Hive Activity | Beekeeper Tasks |
|-------|--------------|-----------------|
| Jan | Brood rearing active. Pollen coming in (FL: maple, tupelo starting). | Inspect. Feed if needed. Some regions: build up for spring flow. |
| Feb | Strong buildup. Early nectar flow starting in deep South. | Add supers in FL/Gulf Coast. Swarm season begins in South. |
| Mar | PRIMARY NECTAR FLOW in many southern areas. Citrus, gallberry, wildflower. | Supers on. Weekly swarm inspections. Peak honey production month in FL. |
| Apr | Nectar flow continues (north FL) or ending (south FL). | Continue harvest. Monitor for SHB (Small Hive Beetle). Varroa test. |
| May | Flow ending in most southern areas. | Harvest. Begin varroa treatment. Prepare for summer dearth. |
| Jun | Summer dearth in many areas. Heat management critical. | Provide shade, water, ventilation. Feed if needed. SHB management. |
| Jul | Dearth continues. Extreme heat. Bees may beard heavily. | Keep water available. Minimal inspections during extreme heat (inspect early AM). |
| Aug | Dearth. Some areas: fall buildup starting. | Late summer varroa treatment. Start feeding for fall. |
| Sep | Fall buildup begins. Secondary nectar flows (goldenrod, aster). | Inspect. Feed. Requeen if needed. |
| Oct | Fall flow continues. Good buildup month. | Add supers for fall honey if strong flow. |
| Nov | Activity slowing. Some brood rearing continues year-round in zones 9-10. | Light harvest if surplus. Ensure stores (40-60 lbs; less than northern). |
| Dec | Reduced activity. Cluster is loose or absent in zone 9-10. | Light inspection. Oxalic acid treatment (broodless period in some areas). |

---

# 10. Bee Module: Regional Nectar Flow Windows

## Major Nectar Flows by Region

| Region | Primary Flow | Secondary Flow | Summer Dearth | Key Plants |
|--------|-------------|----------------|---------------|------------|
| **Northeast** (NY, NE, PA) | Late May - Jul | Aug-Sep (goldenrod) | Late Jul - Aug (sometimes) | Clover, basswood, tulip poplar, goldenrod, aster |
| **Upper Midwest** (MN, WI, MI) | Jun - Jul | Aug (goldenrod) | Late Jul - Aug | Clover, basswood, alfalfa, goldenrod |
| **Mid-Atlantic** (VA, MD, DC) | Apr - Jun | Sep (goldenrod) | Jul - Aug | Tulip poplar, black locust, clover, goldenrod |
| **Southeast** (GA, SC, NC) | Mar - May | Sep-Oct (goldenrod) | Jun - Aug | Tulip poplar, privet, sourwood, goldenrod |
| **Florida** (North) | Mar - May | Oct-Nov (wildflower) | Jun - Sep | Citrus, gallberry, palmetto, wildflower |
| **Florida** (Central/South) | Feb - Apr | Oct-Nov | May - Sep | Orange blossom, Brazilian pepper, saw palmetto |
| **Gulf Coast** (TX, LA, MS, AL) | Mar - May | Sep-Oct | Jun - Aug | Tallow, clover, wildflower, goldenrod |
| **Pacific NW** (WA, OR) | Jun - Aug | None significant | Sep - Feb (long winter) | Blackberry, clover, fireweed, maple |
| **Mountain West** (CO, UT, MT) | Jun - Jul | Late Aug (rabbitbrush) | Aug | Clover, sweet clover, alfalfa, wildflower |
| **Desert SW** (AZ, NM) | Mar - May (varies) | Sep-Oct (after monsoon) | Jun - Aug (extreme) | Mesquite, catclaw acacia, palo verde, wildflower |
| **California** (Central Valley) | Feb - May | None | Jun - Nov | Almond, citrus, star thistle, eucalyptus |
| **Great Plains** (KS, NE, OK) | May - Jul | Sep (goldenrod) | Aug | Clover, alfalfa, sunflower, goldenrod |
| **Texas** (Central) | Mar - Jun | Sep-Oct | Jul - Aug | Horsemint, mesquite, huajilla, goldenrod |

## Winter Stores Requirements by Region

| Region | Minimum Winter Stores | Notes |
|--------|----------------------|-------|
| Zones 3-4 (extreme cold) | 80-100 lbs | Cluster cannot break; long winters; need massive reserves |
| Zones 5-6 (cold) | 60-80 lbs | Standard northern requirement |
| Zone 7 (moderate) | 50-70 lbs | Shorter winter, some warm foraging days |
| Zone 8 (warm) | 40-60 lbs | Short dormant period; some forage year-round |
| Zone 9-10 (hot) | 30-50 lbs | Little to no dormant period; feeding through summer dearth more important |
| Zone 11+ (tropical) | 20-30 lbs | Year-round foraging; main threat is summer dearth, not winter |

---

# APPENDIX: Data Model Notes for App Implementation

## Zone Group Simplification

For the app's recommendation engine, collapse 13 zones into 5 climate categories:

```
COLD     = zones 3-4  (extreme winters, short growing season)
MODERATE = zones 5-6  (cold winters, medium growing season)  
WARM     = zones 7-8  (mild winters, long growing season)
HOT      = zones 9-10 (minimal frost, year-round potential)
TROPICAL = zones 11+  (frost-free)
```

All recommendation badges use this:
- 🟢 "Recommended for your zone" = Plant/breed is in its ideal climate category
- 🟡 "May need extra care" = Plant/breed is one category outside ideal
- 🔴 "Not recommended" = Plant/breed is two+ categories outside ideal

## ZIP to Zone Lookup

Simplified approach for v1: Use a prefix-based lookup table mapping first 3 digits of ZIP to approximate zone. Not perfect but sufficient for recommendation purposes. Can refine later with full ZIP-to-zone database (USDA provides CSV).

## Planting Window Calculation

For each plant + user zone:
1. Look up zone group (COLD/MODERATE/WARM/HOT/TROPICAL)
2. Get planting month ranges from reference table
3. For "What to plant now" feature: filter plants where current month falls within any planting window
4. For countdowns: calculate days until next planting window opens

## First-Year Bee Colony Note

For ALL new hives regardless of region: suppress honey harvest expectations in year 1. Display: "First-year colony — focus on building strength. Honey harvest unlikely until year 2."
