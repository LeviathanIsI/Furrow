package com.furrow.app.data

object NACatalogJson {
    val RAW: String = """
{
  "schema_version": "1.0",
  "generated_on": "2026-02-20",
  "region_scope": "North America (general; not all species suitable everywhere)",
  "notes": [
    "Catalog is a practical superset: common + commercial + rare/heritage + experimental options.",
    "Plants focus on cultivated crops/herbs/fruits/nuts/grains/cover crops + common homestead ornamentals.",
    "Bees include managed honey bee strains and commonly managed native pollinators used in North America."
  ],
  "chickens": [
    {
      "id": "chicken:australorp",
      "common_name": "Australorp",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs",
        "dual-purpose"
      ],
      "tags": [
        "common",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:barred-plymouth-rock",
      "common_name": "Barred Plymouth Rock",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs",
        "dual-purpose"
      ],
      "tags": [
        "common",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:buff-orpington",
      "common_name": "Buff Orpington",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs",
        "dual-purpose"
      ],
      "tags": [
        "common",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:rhode-island-red",
      "common_name": "Rhode Island Red",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs",
        "dual-purpose"
      ],
      "tags": [
        "common",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:leghorn",
      "common_name": "Leghorn",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "common",
        "commercial"
      ],
      "notes": ""
    },
    {
      "id": "chicken:sussex",
      "common_name": "Sussex",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs",
        "dual-purpose"
      ],
      "tags": [
        "common",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:wyandotte",
      "common_name": "Wyandotte",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs",
        "dual-purpose"
      ],
      "tags": [
        "common",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:new-hampshire",
      "common_name": "New Hampshire",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs",
        "dual-purpose"
      ],
      "tags": [
        "common",
        "heritage"
      ],
      "notes": ""
    },
    {
      "id": "chicken:jersey-giant",
      "common_name": "Jersey Giant",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "meat",
        "dual-purpose"
      ],
      "tags": [
        "common",
        "heritage"
      ],
      "notes": ""
    },
    {
      "id": "chicken:cornish-cross",
      "common_name": "Cornish Cross",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "meat"
      ],
      "tags": [
        "common",
        "commercial"
      ],
      "notes": ""
    },
    {
      "id": "chicken:cornish",
      "common_name": "Cornish",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "meat"
      ],
      "tags": [
        "heritage"
      ],
      "notes": ""
    },
    {
      "id": "chicken:cochin",
      "common_name": "Cochin",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "ornamental",
        "broody"
      ],
      "tags": [
        "common",
        "heritage"
      ],
      "notes": ""
    },
    {
      "id": "chicken:silkie",
      "common_name": "Silkie",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "ornamental",
        "broody"
      ],
      "tags": [
        "common",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:easter-egger-type",
      "common_name": "Easter Egger (type)",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "common",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:ameraucana",
      "common_name": "Ameraucana",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "heritage",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:araucana",
      "common_name": "Araucana",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:marans",
      "common_name": "Marans",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "common",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:welsummer",
      "common_name": "Welsummer",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "common",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:brahma",
      "common_name": "Brahma",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "meat",
        "dual-purpose"
      ],
      "tags": [
        "heritage",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:faverolles",
      "common_name": "Faverolles",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs",
        "dual-purpose"
      ],
      "tags": [
        "heritage",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:hamburg",
      "common_name": "Hamburg",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:ancona",
      "common_name": "Ancona",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:andalusian",
      "common_name": "Andalusian",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:dorking",
      "common_name": "Dorking",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "meat",
        "heritage"
      ],
      "tags": [
        "rare",
        "heritage"
      ],
      "notes": ""
    },
    {
      "id": "chicken:langshan",
      "common_name": "Langshan",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "meat",
        "dual-purpose"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:minorca",
      "common_name": "Minorca",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:polish",
      "common_name": "Polish",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "ornamental"
      ],
      "tags": [
        "heritage",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:sebright-bantam",
      "common_name": "Sebright (bantam)",
      "category": "chicken_breed",
      "size_class": "bantam",
      "primary_purpose": [
        "ornamental"
      ],
      "tags": [
        "bantam",
        "heritage"
      ],
      "notes": ""
    },
    {
      "id": "chicken:old-english-game",
      "common_name": "Old English Game",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "ornamental"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:modern-game",
      "common_name": "Modern Game",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "ornamental"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:sumatra",
      "common_name": "Sumatra",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "ornamental"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:sultan",
      "common_name": "Sultan",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "ornamental"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:houdan",
      "common_name": "Houdan",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "dual-purpose"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:campine",
      "common_name": "Campine",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:redcap",
      "common_name": "Redcap",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:cr-vecoeur",
      "common_name": "Crèvecoeur",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "meat",
        "heritage"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:la-fl-che",
      "common_name": "La Flèche",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "meat",
        "heritage"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:belgian-bearded-d-uccle-bantam",
      "common_name": "Belgian Bearded d'Uccle (bantam)",
      "category": "chicken_breed",
      "size_class": "bantam",
      "primary_purpose": [
        "ornamental"
      ],
      "tags": [
        "bantam",
        "heritage"
      ],
      "notes": ""
    },
    {
      "id": "chicken:booted-bantam",
      "common_name": "Booted Bantam",
      "category": "chicken_breed",
      "size_class": "bantam",
      "primary_purpose": [
        "ornamental"
      ],
      "tags": [
        "bantam",
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:phoenix",
      "common_name": "Phoenix",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "ornamental"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:yokohama",
      "common_name": "Yokohama",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "ornamental"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:lakenvelder",
      "common_name": "Lakenvelder",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:spitzhauben",
      "common_name": "Spitzhauben",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:dominique",
      "common_name": "Dominique",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs",
        "dual-purpose"
      ],
      "tags": [
        "heritage",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:delaware",
      "common_name": "Delaware",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "meat",
        "dual-purpose"
      ],
      "tags": [
        "heritage",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:buckeye",
      "common_name": "Buckeye",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "dual-purpose"
      ],
      "tags": [
        "heritage",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:chantecler",
      "common_name": "Chantecler",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "dual-purpose"
      ],
      "tags": [
        "heritage",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "chicken:java",
      "common_name": "Java",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "dual-purpose"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:holland",
      "common_name": "Holland",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "dual-purpose"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    },
    {
      "id": "chicken:rhode-island-white",
      "common_name": "Rhode Island White",
      "category": "chicken_breed",
      "size_class": "standard",
      "primary_purpose": [
        "eggs",
        "dual-purpose"
      ],
      "tags": [
        "heritage",
        "rare"
      ],
      "notes": ""
    }
  ],
  "bees": [
    {
      "id": "bee:italian-honey-bee",
      "common_name": "Italian honey bee",
      "scientific_name": "Apis mellifera ligustica",
      "category": "honey_bee_subspecies",
      "tags": [
        "common",
        "commercial",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "bee:carniolan-honey-bee",
      "common_name": "Carniolan honey bee",
      "scientific_name": "Apis mellifera carnica",
      "category": "honey_bee_subspecies",
      "tags": [
        "common",
        "commercial",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "bee:russian-honey-bee",
      "common_name": "Russian honey bee",
      "scientific_name": "Apis mellifera (selected lines)",
      "category": "honey_bee_strain",
      "tags": [
        "common",
        "commercial",
        "mite-tolerant"
      ],
      "notes": ""
    },
    {
      "id": "bee:buckfast-bee",
      "common_name": "Buckfast bee",
      "scientific_name": "Apis mellifera (hybrid strain)",
      "category": "honey_bee_strain",
      "tags": [
        "common",
        "commercial"
      ],
      "notes": ""
    },
    {
      "id": "bee:caucasian-honey-bee",
      "common_name": "Caucasian honey bee",
      "scientific_name": "Apis mellifera caucasica",
      "category": "honey_bee_subspecies",
      "tags": [
        "rare",
        "experimental"
      ],
      "notes": ""
    },
    {
      "id": "bee:german-european-dark-bee",
      "common_name": "German/European dark bee",
      "scientific_name": "Apis mellifera mellifera",
      "category": "honey_bee_subspecies",
      "tags": [
        "rare",
        "experimental"
      ],
      "notes": ""
    },
    {
      "id": "bee:cordovan-italian-color-strain",
      "common_name": "Cordovan (Italian color strain)",
      "scientific_name": "Apis mellifera ligustica (color strain)",
      "category": "honey_bee_strain",
      "tags": [
        "rare",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "bee:minnesota-hygienic-selected-line",
      "common_name": "Minnesota Hygienic (selected line)",
      "scientific_name": "Apis mellifera (selected line)",
      "category": "honey_bee_strain",
      "tags": [
        "commercial",
        "mite-tolerant"
      ],
      "notes": ""
    },
    {
      "id": "bee:vsh-varroa-sensitive-hygiene-lines",
      "common_name": "VSH (Varroa Sensitive Hygiene) lines",
      "scientific_name": "Apis mellifera (selected line)",
      "category": "honey_bee_strain",
      "tags": [
        "commercial",
        "mite-tolerant"
      ],
      "notes": ""
    },
    {
      "id": "bee:blue-orchard-mason-bee",
      "common_name": "Blue orchard mason bee",
      "scientific_name": "Osmia lignaria",
      "category": "managed_native_pollinator",
      "tags": [
        "common",
        "orchards",
        "homestead"
      ],
      "notes": ""
    },
    {
      "id": "bee:alfalfa-leafcutter-bee",
      "common_name": "Alfalfa leafcutter bee",
      "scientific_name": "Megachile rotundata",
      "category": "managed_native_pollinator",
      "tags": [
        "commercial"
      ],
      "notes": ""
    },
    {
      "id": "bee:common-eastern-bumble-bee",
      "common_name": "Common eastern bumble bee",
      "scientific_name": "Bombus impatiens",
      "category": "managed_native_pollinator",
      "tags": [
        "commercial",
        "greenhouse"
      ],
      "notes": ""
    },
    {
      "id": "bee:western-bumble-bee",
      "common_name": "Western bumble bee",
      "scientific_name": "Bombus occidentalis",
      "category": "managed_native_pollinator",
      "tags": [
        "rare",
        "regional"
      ],
      "notes": ""
    }
  ],
  "plants": [
    {
      "id": "plant:tomato",
      "common_name": "Tomato",
      "scientific_name": "Solanum lycopersicum",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:pepper-sweet-bell",
      "common_name": "Pepper (sweet/bell)",
      "scientific_name": "Capsicum annuum",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:pepper-hot-chili",
      "common_name": "Pepper (hot/chili)",
      "scientific_name": "Capsicum annuum",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:eggplant",
      "common_name": "Eggplant",
      "scientific_name": "Solanum melongena",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:potato",
      "common_name": "Potato",
      "scientific_name": "Solanum tuberosum",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:sweet-potato",
      "common_name": "Sweet potato",
      "scientific_name": "Ipomoea batatas",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:carrot",
      "common_name": "Carrot",
      "scientific_name": "Daucus carota subsp. sativus",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:beet",
      "common_name": "Beet",
      "scientific_name": "Beta vulgaris",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:radish",
      "common_name": "Radish",
      "scientific_name": "Raphanus sativus",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:turnip",
      "common_name": "Turnip",
      "scientific_name": "Brassica rapa subsp. rapa",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:rutabaga",
      "common_name": "Rutabaga",
      "scientific_name": "Brassica napus var. napobrassica",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:onion",
      "common_name": "Onion",
      "scientific_name": "Allium cepa",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:garlic",
      "common_name": "Garlic",
      "scientific_name": "Allium sativum",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:leek",
      "common_name": "Leek",
      "scientific_name": "Allium ampeloprasum var. porrum",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:shallot",
      "common_name": "Shallot",
      "scientific_name": "Allium cepa (Aggregatum Group)",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:scallion-green-onion",
      "common_name": "Scallion/Green onion",
      "scientific_name": "Allium fistulosum",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:lettuce",
      "common_name": "Lettuce",
      "scientific_name": "Lactuca sativa",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:spinach",
      "common_name": "Spinach",
      "scientific_name": "Spinacia oleracea",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:kale",
      "common_name": "Kale",
      "scientific_name": "Brassica oleracea var. sabellica",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:cabbage",
      "common_name": "Cabbage",
      "scientific_name": "Brassica oleracea var. capitata",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:broccoli",
      "common_name": "Broccoli",
      "scientific_name": "Brassica oleracea var. italica",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:cauliflower",
      "common_name": "Cauliflower",
      "scientific_name": "Brassica oleracea var. botrytis",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:brussels-sprouts",
      "common_name": "Brussels sprouts",
      "scientific_name": "Brassica oleracea var. gemmifera",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:collards",
      "common_name": "Collards",
      "scientific_name": "Brassica oleracea var. viridis",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:swiss-chard",
      "common_name": "Swiss chard",
      "scientific_name": "Beta vulgaris (Cicla Group)",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:arugula",
      "common_name": "Arugula",
      "scientific_name": "Eruca vesicaria subsp. sativa",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:mustard-greens",
      "common_name": "Mustard greens",
      "scientific_name": "Brassica juncea",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:bok-choy",
      "common_name": "Bok choy",
      "scientific_name": "Brassica rapa subsp. chinensis",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:pea",
      "common_name": "Pea",
      "scientific_name": "Pisum sativum",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:green-bean",
      "common_name": "Green bean",
      "scientific_name": "Phaseolus vulgaris",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:lima-bean",
      "common_name": "Lima bean",
      "scientific_name": "Phaseolus lunatus",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:cucumber",
      "common_name": "Cucumber",
      "scientific_name": "Cucumis sativus",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:zucchini",
      "common_name": "Zucchini",
      "scientific_name": "Cucurbita pepo",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:winter-squash-butternut",
      "common_name": "Winter squash (butternut)",
      "scientific_name": "Cucurbita moschata",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:pumpkin",
      "common_name": "Pumpkin",
      "scientific_name": "Cucurbita pepo",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:watermelon",
      "common_name": "Watermelon",
      "scientific_name": "Citrullus lanatus",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:corn-sweet",
      "common_name": "Corn (sweet)",
      "scientific_name": "Zea mays var. saccharata",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:okra",
      "common_name": "Okra",
      "scientific_name": "Abelmoschus esculentus",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:asparagus",
      "common_name": "Asparagus",
      "scientific_name": "Asparagus officinalis",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "perennial"
      ],
      "notes": ""
    },
    {
      "id": "plant:rhubarb",
      "common_name": "Rhubarb",
      "scientific_name": "Rheum × hybridum",
      "plant_type": "vegetable",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "perennial"
      ],
      "notes": ""
    },
    {
      "id": "plant:basil",
      "common_name": "Basil",
      "scientific_name": "Ocimum basilicum",
      "plant_type": "herb",
      "uses": [
        "food",
        "medicinal"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:parsley",
      "common_name": "Parsley",
      "scientific_name": "Petroselinum crispum",
      "plant_type": "herb",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:cilantro-coriander",
      "common_name": "Cilantro/Coriander",
      "scientific_name": "Coriandrum sativum",
      "plant_type": "herb",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:dill",
      "common_name": "Dill",
      "scientific_name": "Anethum graveolens",
      "plant_type": "herb",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:mint",
      "common_name": "Mint",
      "scientific_name": "Mentha spp.",
      "plant_type": "herb",
      "uses": [
        "food",
        "medicinal"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:rosemary",
      "common_name": "Rosemary",
      "scientific_name": "Salvia rosmarinus",
      "plant_type": "herb",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:thyme",
      "common_name": "Thyme",
      "scientific_name": "Thymus vulgaris",
      "plant_type": "herb",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:oregano",
      "common_name": "Oregano",
      "scientific_name": "Origanum vulgare",
      "plant_type": "herb",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:sage",
      "common_name": "Sage",
      "scientific_name": "Salvia officinalis",
      "plant_type": "herb",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:lavender",
      "common_name": "Lavender",
      "scientific_name": "Lavandula angustifolia",
      "plant_type": "herb",
      "uses": [
        "fragrance",
        "pollinator"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:apple",
      "common_name": "Apple",
      "scientific_name": "Malus domestica",
      "plant_type": "fruit_tree",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "orchard"
      ],
      "notes": ""
    },
    {
      "id": "plant:pear",
      "common_name": "Pear",
      "scientific_name": "Pyrus communis",
      "plant_type": "fruit_tree",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "orchard"
      ],
      "notes": ""
    },
    {
      "id": "plant:peach",
      "common_name": "Peach",
      "scientific_name": "Prunus persica",
      "plant_type": "fruit_tree",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "orchard"
      ],
      "notes": ""
    },
    {
      "id": "plant:plum",
      "common_name": "Plum",
      "scientific_name": "Prunus domestica",
      "plant_type": "fruit_tree",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "orchard"
      ],
      "notes": ""
    },
    {
      "id": "plant:cherry-sweet",
      "common_name": "Cherry (sweet)",
      "scientific_name": "Prunus avium",
      "plant_type": "fruit_tree",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "orchard"
      ],
      "notes": ""
    },
    {
      "id": "plant:grape",
      "common_name": "Grape",
      "scientific_name": "Vitis spp.",
      "plant_type": "vine",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "orchard"
      ],
      "notes": ""
    },
    {
      "id": "plant:strawberry",
      "common_name": "Strawberry",
      "scientific_name": "Fragaria × ananassa",
      "plant_type": "berry",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:blueberry",
      "common_name": "Blueberry",
      "scientific_name": "Vaccinium corymbosum",
      "plant_type": "berry",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "orchard"
      ],
      "notes": ""
    },
    {
      "id": "plant:raspberry",
      "common_name": "Raspberry",
      "scientific_name": "Rubus idaeus",
      "plant_type": "berry",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:blackberry",
      "common_name": "Blackberry",
      "scientific_name": "Rubus fruticosus agg.",
      "plant_type": "berry",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:wheat",
      "common_name": "Wheat",
      "scientific_name": "Triticum aestivum",
      "plant_type": "grain",
      "uses": [
        "food",
        "feed"
      ],
      "tags": [
        "common",
        "field"
      ],
      "notes": ""
    },
    {
      "id": "plant:oats",
      "common_name": "Oats",
      "scientific_name": "Avena sativa",
      "plant_type": "grain",
      "uses": [
        "food",
        "feed"
      ],
      "tags": [
        "common",
        "field"
      ],
      "notes": ""
    },
    {
      "id": "plant:barley",
      "common_name": "Barley",
      "scientific_name": "Hordeum vulgare",
      "plant_type": "grain",
      "uses": [
        "food",
        "feed"
      ],
      "tags": [
        "common",
        "field"
      ],
      "notes": ""
    },
    {
      "id": "plant:rye",
      "common_name": "Rye",
      "scientific_name": "Secale cereale",
      "plant_type": "grain",
      "uses": [
        "food",
        "cover_crop"
      ],
      "tags": [
        "common",
        "field"
      ],
      "notes": ""
    },
    {
      "id": "plant:buckwheat",
      "common_name": "Buckwheat",
      "scientific_name": "Fagopyrum esculentum",
      "plant_type": "grain",
      "uses": [
        "food",
        "cover_crop",
        "pollinator"
      ],
      "tags": [
        "common",
        "field"
      ],
      "notes": ""
    },
    {
      "id": "plant:clover-white",
      "common_name": "Clover (white)",
      "scientific_name": "Trifolium repens",
      "plant_type": "cover_crop",
      "uses": [
        "cover_crop",
        "pollinator",
        "forage"
      ],
      "tags": [
        "common",
        "field"
      ],
      "notes": ""
    },
    {
      "id": "plant:clover-red",
      "common_name": "Clover (red)",
      "scientific_name": "Trifolium pratense",
      "plant_type": "cover_crop",
      "uses": [
        "cover_crop",
        "pollinator",
        "forage"
      ],
      "tags": [
        "common",
        "field"
      ],
      "notes": ""
    },
    {
      "id": "plant:sunflower",
      "common_name": "Sunflower",
      "scientific_name": "Helianthus annuus",
      "plant_type": "flower",
      "uses": [
        "pollinator",
        "food"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:marigold",
      "common_name": "Marigold",
      "scientific_name": "Tagetes spp.",
      "plant_type": "flower",
      "uses": [
        "companion",
        "pollinator"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:nasturtium",
      "common_name": "Nasturtium",
      "scientific_name": "Tropaeolum majus",
      "plant_type": "flower",
      "uses": [
        "food",
        "companion",
        "pollinator"
      ],
      "tags": [
        "common",
        "garden"
      ],
      "notes": ""
    },
    {
      "id": "plant:hazelnut",
      "common_name": "Hazelnut",
      "scientific_name": "Corylus spp.",
      "plant_type": "nut_tree",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "orchard"
      ],
      "notes": ""
    },
    {
      "id": "plant:pecan",
      "common_name": "Pecan",
      "scientific_name": "Carya illinoinensis",
      "plant_type": "nut_tree",
      "uses": [
        "food"
      ],
      "tags": [
        "common",
        "orchard"
      ],
      "notes": ""
    }
  ]
}
""".trimIndent()
}
