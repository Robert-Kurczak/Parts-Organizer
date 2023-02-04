package pl.edu.uwr.partsorganizer.model

import androidx.room.*
import java.io.File

//---Tables---
@Entity(tableName = "Drawer")
data class Drawer(
    @PrimaryKey(autoGenerate = true) val drawerID: Int,
    @ColumnInfo val Row: Int,
    @ColumnInfo val Column: Int
)

@Entity(tableName = "Types")
data class Types(
    @PrimaryKey(autoGenerate = true) val typeID: Int,
    @ColumnInfo val Name: String
)

@Entity(tableName = "Parts")
data class PartsItem(
    @PrimaryKey(autoGenerate = true) val partID: Int,
    @ColumnInfo val Name: String,
    @ColumnInfo val Description: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val Image: ByteArray,
    @ColumnInfo val amount: Int,

    @ColumnInfo val PartTypeID: Int,
    @ColumnInfo val PartDrawerID: Int,
)

data class Parts(
    @Embedded val Part: PartsItem,
    @Relation(
        parentColumn = "PartTypeID",
        entityColumn = "typeID"
    )
    val Type: Types,

    @Relation(
        parentColumn = "PartTypeID",
        entityColumn = "drawerID"
    )
    val Drawer: Drawer
)
//------

//---DAOs---
@Dao
interface DrawerDao{
    @Insert
    suspend fun insertDrawer(drawer: Drawer)

    @Query("SELECT * FROM Drawer")
    fun selectAll(): List<Drawer>
}

@Dao
interface TypesDao{
    @Insert
    suspend fun insertType(Type: Types)
}

@Dao
interface PartsDao{
    @Transaction
    @Query("SELECT * FROM Parts")
    fun selectAll(): List<Parts>

    @Transaction
    @Query("SELECT * FROM Parts WHERE PartDrawerID = :drawerID")
    fun selectInDrawer(drawerID: Int): List<Parts>

    @Insert
    suspend fun insertPart(part: PartsItem)
}
//------

@Database(
    entities = [PartsItem::class, Types::class, Drawer::class],
    version = 1
)
abstract class PartsDatabase : RoomDatabase(){
    abstract fun DrawerDao(): DrawerDao
    abstract fun TypesDao(): TypesDao
    abstract fun PartsDao(): PartsDao
}
