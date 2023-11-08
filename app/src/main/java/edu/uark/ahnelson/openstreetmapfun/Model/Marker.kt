package edu.uark.ahnelson.openstreetmapfun.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marker_table")
data class Marker(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "image_path") val imagePath: String?,
    @ColumnInfo(name = "description") val description: String?,
    val timestamp: Long
)
