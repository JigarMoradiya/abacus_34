package com.jigar.me.data.model.dbtable.inapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.android.billingclient.api.Purchase

@Entity(tableName = "tableInAppPurchase")
data class InAppPurchaseDetails(
    @PrimaryKey val orderId: String,
    val sku: String,
    val developerPayload: String?,
    val purchaseToken: String?,
    val purchaseTime: Long?,
    val purchaseState: Int?,
    val acknowledged: Boolean?,
    val signature: String?,
    val originalJson: String?,
    var isAutoRenewing : Boolean = false
)
