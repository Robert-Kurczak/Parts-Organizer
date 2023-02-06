package pl.edu.uwr.partsorganizer.model

import android.content.Context
import androidx.room.Room

object DataProvider{
    @Volatile
    private var INSTANCE: PartsDatabase? = null

    fun getDatabase(context: Context): PartsDatabase {
        return INSTANCE ?: synchronized(this){

            val instance = Room.databaseBuilder(
                context.applicationContext,
                PartsDatabase::class.java,
                "PartsDatabase"
            )
                .allowMainThreadQueries()
                .createFromAsset("database/PartsDatabaseExample.db")
                .build().also { INSTANCE = it }

            instance
        }
    }
}