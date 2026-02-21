package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "barter_trades")
data class BarterTrade(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val partner: String? = null,
    @ColumnInfo(name = "given_items") val givenItems: String? = null,
    @ColumnInfo(name = "received_items") val receivedItems: String? = null,
    @ColumnInfo(name = "fair_market_value") val fairMarketValue: Double? = null,
    val notes: String? = null,
)
