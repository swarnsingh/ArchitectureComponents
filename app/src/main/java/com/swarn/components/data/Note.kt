package com.swarn.components.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author Swarn Singh.
 */
@Entity(tableName = "note_table")
data class Note(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        @ColumnInfo(name = "title")
        var title: String? = null,

        @ColumnInfo(name = "description")
        var description: String? = null,

        @ColumnInfo(name = "priority")
        var priority: Int = 0
) {
    constructor(title: String, description: String, priority: Int) : this() {
        this.title = title
        this.description = description
        this.priority = priority
    }
}