package com.example.eventapps.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class EventEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String = "",

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "mediaCover")
    var mediaCover: String? = null,

    @ColumnInfo(name = "summary")
    var summary: String? = null,

    @ColumnInfo(name = "imageLogo")
    var imageLogo: String? = null,

    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean = false
)
