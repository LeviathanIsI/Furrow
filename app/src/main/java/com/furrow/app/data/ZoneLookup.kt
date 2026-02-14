package com.furrow.app.data

import com.furrow.app.data.local.entity.UserProfile

object ZoneLookup {

    fun deriveProfile(zipCode: String): UserProfile? {
        val prefix = zipCode.take(3).toIntOrNull() ?: return null
        val info = lookupZipPrefix(prefix) ?: return null
        val zoneNumber = zoneNumber(info.zone)
        val group = zoneGroupForZoneNumber(zoneNumber)
        val (lastFrost, firstFrost) = frostDatesForZoneNumber(zoneNumber)
        val climate = climateCategoryForGroup(group)
        return UserProfile(
            zipCode = zipCode,
            hardinessZone = info.zone,
            zoneGroup = group,
            state = info.state,
            climateCategory = climate,
            lastFrostDate = lastFrost,
            firstFrostDate = firstFrost,
        )
    }

    fun isValidZip(zip: String): Boolean {
        if (zip.length != 5 || zip.any { !it.isDigit() }) return false
        val prefix = zip.take(3).toIntOrNull() ?: return false
        return lookupZipPrefix(prefix) != null
    }

    private data class ZipZoneInfo(val zone: String, val state: String)

    private fun zoneNumber(zone: String): Int =
        zone.filter { it.isDigit() }.toIntOrNull() ?: 7

    private fun zoneGroupForZoneNumber(zoneNum: Int): String = when (zoneNum) {
        in 1..3 -> "extreme_cold"
        4 -> "cold"
        in 5..6 -> "moderate"
        in 7..8 -> "warm"
        in 9..10 -> "hot"
        else -> "hot"
    }

    private fun frostDatesForZoneNumber(zoneNum: Int): Pair<String, String> = when (zoneNum) {
        in 1..3 -> "05-15" to "09-15"
        4 -> "05-01" to "10-01"
        5 -> "04-15" to "10-15"
        6 -> "04-01" to "11-01"
        7 -> "03-15" to "11-15"
        8 -> "03-01" to "12-01"
        9 -> "02-01" to "12-15"
        in 10..15 -> "01-01" to "12-31"
        else -> "04-01" to "10-15"
    }

    private fun climateCategoryForGroup(group: String): String = when (group) {
        "extreme_cold" -> "Extreme Cold"
        "cold" -> "Cold"
        "moderate" -> "Moderate"
        "warm" -> "Warm"
        "hot" -> "Hot"
        else -> "Moderate"
    }

    @Suppress("CyclomaticComplexMethod")
    private fun lookupZipPrefix(prefix: Int): ZipZoneInfo? = when (prefix) {

        // ── Puerto Rico / Virgin Islands ──
        in 6..9 -> ZipZoneInfo("11a", "PR")

        // ── Massachusetts ──
        in 10..13 -> ZipZoneInfo("6a", "MA")
        in 14..16 -> ZipZoneInfo("6a", "MA")
        in 17..24 -> ZipZoneInfo("6b", "MA")
        in 25..26 -> ZipZoneInfo("6a", "MA")
        27 -> ZipZoneInfo("7a", "MA")

        // ── Rhode Island ──
        in 28..29 -> ZipZoneInfo("6b", "RI")

        // ── New Hampshire ──
        in 30..34 -> ZipZoneInfo("5b", "NH")
        in 35..38 -> ZipZoneInfo("5a", "NH")

        // ── Maine ──
        39 -> ZipZoneInfo("5b", "ME")
        in 40..41 -> ZipZoneInfo("5b", "ME")
        in 42..49 -> ZipZoneInfo("5a", "ME")

        // ── Vermont ──
        in 50..54 -> ZipZoneInfo("4b", "VT")
        in 55..59 -> ZipZoneInfo("5a", "VT")

        // ── Connecticut ──
        in 60..69 -> ZipZoneInfo("6b", "CT")

        // ── New Jersey ──
        in 70..79 -> ZipZoneInfo("7a", "NJ")
        in 80..89 -> ZipZoneInfo("7a", "NJ")

        // ── New York ──
        in 100..104 -> ZipZoneInfo("7b", "NY")
        in 105..109 -> ZipZoneInfo("6b", "NY")
        in 110..119 -> ZipZoneInfo("7a", "NY")
        in 120..129 -> ZipZoneInfo("5b", "NY")
        in 130..139 -> ZipZoneInfo("5b", "NY")
        in 140..149 -> ZipZoneInfo("6a", "NY")

        // ── Pennsylvania ──
        in 150..162 -> ZipZoneInfo("6b", "PA")
        in 163..169 -> ZipZoneInfo("5b", "PA")
        in 170..179 -> ZipZoneInfo("6b", "PA")
        in 180..189 -> ZipZoneInfo("6b", "PA")
        in 190..196 -> ZipZoneInfo("7a", "PA")

        // ── Delaware ──
        in 197..199 -> ZipZoneInfo("7a", "DE")

        // ── Washington DC ──
        in 200..205 -> ZipZoneInfo("7a", "DC")

        // ── Maryland ──
        in 206..212 -> ZipZoneInfo("7a", "MD")
        in 213..219 -> ZipZoneInfo("7a", "MD")

        // ── Virginia ──
        in 220..232 -> ZipZoneInfo("7a", "VA")
        in 233..241 -> ZipZoneInfo("7b", "VA")
        in 242..246 -> ZipZoneInfo("6b", "VA")

        // ── West Virginia ──
        in 247..253 -> ZipZoneInfo("6b", "WV")
        in 254..268 -> ZipZoneInfo("6a", "WV")

        // ── North Carolina ──
        in 270..274 -> ZipZoneInfo("7b", "NC")
        in 275..279 -> ZipZoneInfo("7b", "NC")
        in 280..289 -> ZipZoneInfo("8a", "NC")

        // ── South Carolina ──
        in 290..294 -> ZipZoneInfo("8a", "SC")
        in 295..299 -> ZipZoneInfo("8b", "SC")

        // ── Georgia ──
        in 300..303 -> ZipZoneInfo("7b", "GA")
        in 304..312 -> ZipZoneInfo("8a", "GA")
        in 313..319 -> ZipZoneInfo("8b", "GA")
        398 -> ZipZoneInfo("8a", "GA")
        399 -> ZipZoneInfo("8a", "GA")

        // ── Florida ──
        in 320..322 -> ZipZoneInfo("9a", "FL")
        in 323..325 -> ZipZoneInfo("9a", "FL")
        in 326..329 -> ZipZoneInfo("9b", "FL")
        in 330..332 -> ZipZoneInfo("10a", "FL")
        in 333..334 -> ZipZoneInfo("10a", "FL")
        in 335..338 -> ZipZoneInfo("9b", "FL")
        339 -> ZipZoneInfo("9a", "FL")
        in 340..342 -> ZipZoneInfo("10a", "FL")
        344 -> ZipZoneInfo("8b", "FL")
        in 346..347 -> ZipZoneInfo("9a", "FL")
        349 -> ZipZoneInfo("9a", "FL")

        // ── Alabama ──
        in 350..352 -> ZipZoneInfo("7b", "AL")
        in 354..359 -> ZipZoneInfo("8a", "AL")
        in 360..368 -> ZipZoneInfo("7b", "AL")

        // ── Tennessee ──
        in 370..374 -> ZipZoneInfo("7a", "TN")
        in 375..379 -> ZipZoneInfo("7b", "TN")
        in 380..385 -> ZipZoneInfo("7a", "TN")

        // ── Mississippi ──
        in 386..397 -> ZipZoneInfo("8a", "MS")

        // ── Kentucky ──
        in 400..409 -> ZipZoneInfo("6b", "KY")
        in 410..418 -> ZipZoneInfo("6b", "KY")
        in 420..427 -> ZipZoneInfo("6b", "KY")

        // ── Ohio ──
        in 430..435 -> ZipZoneInfo("6a", "OH")
        in 436..449 -> ZipZoneInfo("6a", "OH")
        in 450..455 -> ZipZoneInfo("6b", "OH")
        in 456..458 -> ZipZoneInfo("6a", "OH")

        // ── Indiana ──
        in 460..462 -> ZipZoneInfo("6a", "IN")
        in 463..469 -> ZipZoneInfo("5b", "IN")
        in 470..479 -> ZipZoneInfo("6a", "IN")

        // ── Michigan ──
        in 480..489 -> ZipZoneInfo("6a", "MI")
        in 490..496 -> ZipZoneInfo("5b", "MI")
        in 497..499 -> ZipZoneInfo("5a", "MI")

        // ── Iowa ──
        in 500..516 -> ZipZoneInfo("5a", "IA")
        in 520..528 -> ZipZoneInfo("5a", "IA")

        // ── Wisconsin ──
        in 530..534 -> ZipZoneInfo("5a", "WI")
        in 535..539 -> ZipZoneInfo("4b", "WI")
        in 540..549 -> ZipZoneInfo("4a", "WI")

        // ── Minnesota ──
        in 550..556 -> ZipZoneInfo("4b", "MN")
        in 557..559 -> ZipZoneInfo("4a", "MN")
        in 560..564 -> ZipZoneInfo("4a", "MN")
        in 565..567 -> ZipZoneInfo("3b", "MN")

        // ── South Dakota ──
        in 570..572 -> ZipZoneInfo("4b", "SD")
        in 573..577 -> ZipZoneInfo("4a", "SD")

        // ── North Dakota ──
        in 580..584 -> ZipZoneInfo("4a", "ND")
        in 585..588 -> ZipZoneInfo("3b", "ND")

        // ── Montana ──
        in 590..594 -> ZipZoneInfo("4b", "MT")
        in 595..599 -> ZipZoneInfo("4a", "MT")

        // ── Illinois ──
        in 600..606 -> ZipZoneInfo("5b", "IL")
        in 607..612 -> ZipZoneInfo("5b", "IL")
        in 613..619 -> ZipZoneInfo("5b", "IL")
        in 620..629 -> ZipZoneInfo("6a", "IL")

        // ── Missouri ──
        in 630..635 -> ZipZoneInfo("6b", "MO")
        in 636..639 -> ZipZoneInfo("6a", "MO")
        in 640..648 -> ZipZoneInfo("6a", "MO")
        in 649..658 -> ZipZoneInfo("6b", "MO")

        // ── Kansas ──
        in 660..664 -> ZipZoneInfo("6a", "KS")
        in 665..669 -> ZipZoneInfo("6a", "KS")
        in 670..679 -> ZipZoneInfo("6b", "KS")

        // ── Nebraska ──
        in 680..685 -> ZipZoneInfo("5b", "NE")
        in 686..689 -> ZipZoneInfo("5a", "NE")
        in 690..693 -> ZipZoneInfo("5a", "NE")

        // ── Louisiana ──
        in 700..701 -> ZipZoneInfo("9a", "LA")
        in 703..708 -> ZipZoneInfo("8b", "LA")
        in 710..714 -> ZipZoneInfo("8b", "LA")

        // ── Arkansas ──
        in 716..722 -> ZipZoneInfo("7b", "AR")
        in 723..729 -> ZipZoneInfo("7b", "AR")

        // ── Oklahoma ──
        in 730..738 -> ZipZoneInfo("7a", "OK")
        in 739..741 -> ZipZoneInfo("7a", "OK")
        in 743..749 -> ZipZoneInfo("7b", "OK")

        // ── Texas ──
        in 750..759 -> ZipZoneInfo("8a", "TX")
        in 760..769 -> ZipZoneInfo("8a", "TX")
        in 770..778 -> ZipZoneInfo("9a", "TX")
        in 779..789 -> ZipZoneInfo("8b", "TX")
        in 790..794 -> ZipZoneInfo("7b", "TX")
        in 795..799 -> ZipZoneInfo("7b", "TX")

        // ── Colorado ──
        in 800..809 -> ZipZoneInfo("5b", "CO")
        in 810..816 -> ZipZoneInfo("5b", "CO")

        // ── Wyoming ──
        in 820..828 -> ZipZoneInfo("4b", "WY")
        in 829..831 -> ZipZoneInfo("4a", "WY")

        // ── Idaho ──
        in 832..834 -> ZipZoneInfo("6a", "ID")
        in 835..838 -> ZipZoneInfo("5b", "ID")

        // ── Utah ──
        in 840..844 -> ZipZoneInfo("6b", "UT")
        in 845..847 -> ZipZoneInfo("5b", "UT")

        // ── Arizona ──
        in 850..853 -> ZipZoneInfo("9b", "AZ")
        in 855..857 -> ZipZoneInfo("9a", "AZ")
        in 859..860 -> ZipZoneInfo("9a", "AZ")
        in 863..864 -> ZipZoneInfo("6b", "AZ")
        865 -> ZipZoneInfo("7a", "AZ")

        // ── New Mexico ──
        in 870..874 -> ZipZoneInfo("7a", "NM")
        in 875..879 -> ZipZoneInfo("6b", "NM")
        in 880..884 -> ZipZoneInfo("7a", "NM")

        // ── Nevada ──
        in 889..891 -> ZipZoneInfo("9a", "NV")
        893 -> ZipZoneInfo("6b", "NV")
        in 894..898 -> ZipZoneInfo("6b", "NV")

        // ── California ──
        in 900..908 -> ZipZoneInfo("10a", "CA")
        in 909..912 -> ZipZoneInfo("9b", "CA")
        in 913..916 -> ZipZoneInfo("9b", "CA")
        in 917..918 -> ZipZoneInfo("10a", "CA")
        in 919..921 -> ZipZoneInfo("10a", "CA")
        in 922..928 -> ZipZoneInfo("9a", "CA")
        in 930..935 -> ZipZoneInfo("9a", "CA")
        in 936..938 -> ZipZoneInfo("9a", "CA")
        in 939..941 -> ZipZoneInfo("9b", "CA")
        in 942..948 -> ZipZoneInfo("9b", "CA")
        in 949..951 -> ZipZoneInfo("9b", "CA")
        in 952..954 -> ZipZoneInfo("8b", "CA")
        in 955..961 -> ZipZoneInfo("8b", "CA")

        // ── Hawaii ──
        in 967..968 -> ZipZoneInfo("11a", "HI")

        // ── Oregon ──
        in 970..974 -> ZipZoneInfo("8b", "OR")
        in 975..978 -> ZipZoneInfo("7a", "OR")
        979 -> ZipZoneInfo("6b", "OR")

        // ── Washington ──
        in 980..984 -> ZipZoneInfo("8b", "WA")
        in 985..986 -> ZipZoneInfo("8a", "WA")
        in 988..989 -> ZipZoneInfo("6b", "WA")
        in 990..994 -> ZipZoneInfo("6b", "WA")

        // ── Alaska ──
        in 995..999 -> ZipZoneInfo("4a", "AK")

        else -> null
    }
}
