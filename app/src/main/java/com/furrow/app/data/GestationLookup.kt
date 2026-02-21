package com.furrow.app.data

object GestationLookup {

    data class GestationInfo(
        val species: String,
        val rangeDays: IntRange,
        val averageDays: Int,
    )

    private val data = listOf(
        GestationInfo("chicken", 19..23, 21),
        GestationInfo("duck", 26..30, 28),
        GestationInfo("turkey", 26..30, 28),
        GestationInfo("goose", 28..32, 30),
        GestationInfo("quail", 15..19, 17),
        GestationInfo("goat", 145..155, 150),
        GestationInfo("sheep", 142..152, 147),
        GestationInfo("pig", 110..118, 114),
        GestationInfo("cow", 275..290, 283),
        GestationInfo("rabbit", 28..34, 31),
        GestationInfo("horse", 330..350, 340),
        GestationInfo("alpaca", 335..355, 345),
        GestationInfo("llama", 340..360, 350),
    )

    private val bySpecies = data.associateBy { it.species }

    fun forSpecies(species: String): GestationInfo? = bySpecies[species.lowercase()]

    fun allSpecies(): List<GestationInfo> = data
}
