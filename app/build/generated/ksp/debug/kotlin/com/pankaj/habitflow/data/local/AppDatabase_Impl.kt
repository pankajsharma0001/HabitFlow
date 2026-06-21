package com.pankaj.habitflow.`data`.local

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.room.RoomOpenHelper
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.pankaj.habitflow.`data`.local.dao.HabitDao
import com.pankaj.habitflow.`data`.local.dao.HabitDao_Impl
import com.pankaj.habitflow.`data`.local.dao.HabitRecordDao
import com.pankaj.habitflow.`data`.local.dao.HabitRecordDao_Impl
import java.lang.Class
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import javax.`annotation`.processing.Generated
import kotlin.Any
import kotlin.Boolean
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.Set

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class AppDatabase_Impl : AppDatabase() {
  private val _habitDao: Lazy<HabitDao> = lazy {
    HabitDao_Impl(this)
  }


  private val _habitRecordDao: Lazy<HabitRecordDao> = lazy {
    HabitRecordDao_Impl(this)
  }


  protected override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
    val _openCallback: SupportSQLiteOpenHelper.Callback = RoomOpenHelper(config, object :
        RoomOpenHelper.Delegate(1) {
      public override fun createAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `habits` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `colorHex` TEXT NOT NULL, `iconName` TEXT NOT NULL, `reminderTimeMinutes` INTEGER, `createdAt` INTEGER NOT NULL, `isArchived` INTEGER NOT NULL, `syncStatus` TEXT NOT NULL, `lastModified` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `habit_records` (`id` TEXT NOT NULL, `habitId` TEXT NOT NULL, `date` TEXT NOT NULL, `isCompleted` INTEGER NOT NULL, `completedAt` INTEGER, `syncStatus` TEXT NOT NULL, `lastModified` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`habitId`) REFERENCES `habits`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_habit_records_habitId` ON `habit_records` (`habitId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_habit_records_date` ON `habit_records` (`date`)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_habit_records_habitId_date` ON `habit_records` (`habitId`, `date`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6861a2d97902128f618f624994ce3d6b')")
      }

      public override fun dropAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `habits`")
        db.execSQL("DROP TABLE IF EXISTS `habit_records`")
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onDestructiveMigration(db)
          }
        }
      }

      public override fun onCreate(db: SupportSQLiteDatabase) {
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onCreate(db)
          }
        }
      }

      public override fun onOpen(db: SupportSQLiteDatabase) {
        mDatabase = db
        db.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(db)
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onOpen(db)
          }
        }
      }

      public override fun onPreMigrate(db: SupportSQLiteDatabase) {
        dropFtsSyncTriggers(db)
      }

      public override fun onPostMigrate(db: SupportSQLiteDatabase) {
      }

      public override fun onValidateSchema(db: SupportSQLiteDatabase):
          RoomOpenHelper.ValidationResult {
        val _columnsHabits: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(11)
        _columnsHabits.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabits.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabits.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabits.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabits.put("colorHex", TableInfo.Column("colorHex", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabits.put("iconName", TableInfo.Column("iconName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabits.put("reminderTimeMinutes", TableInfo.Column("reminderTimeMinutes", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHabits.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabits.put("isArchived", TableInfo.Column("isArchived", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabits.put("syncStatus", TableInfo.Column("syncStatus", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabits.put("lastModified", TableInfo.Column("lastModified", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHabits: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesHabits: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoHabits: TableInfo = TableInfo("habits", _columnsHabits, _foreignKeysHabits,
            _indicesHabits)
        val _existingHabits: TableInfo = read(db, "habits")
        if (!_infoHabits.equals(_existingHabits)) {
          return RoomOpenHelper.ValidationResult(false, """
              |habits(com.pankaj.habitflow.data.local.entity.HabitEntity).
              | Expected:
              |""".trimMargin() + _infoHabits + """
              |
              | Found:
              |""".trimMargin() + _existingHabits)
        }
        val _columnsHabitRecords: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(7)
        _columnsHabitRecords.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabitRecords.put("habitId", TableInfo.Column("habitId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabitRecords.put("date", TableInfo.Column("date", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabitRecords.put("isCompleted", TableInfo.Column("isCompleted", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHabitRecords.put("completedAt", TableInfo.Column("completedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHabitRecords.put("syncStatus", TableInfo.Column("syncStatus", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHabitRecords.put("lastModified", TableInfo.Column("lastModified", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHabitRecords: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysHabitRecords.add(TableInfo.ForeignKey("habits", "CASCADE", "NO ACTION",
            listOf("habitId"), listOf("id")))
        val _indicesHabitRecords: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(3)
        _indicesHabitRecords.add(TableInfo.Index("index_habit_records_habitId", false,
            listOf("habitId"), listOf("ASC")))
        _indicesHabitRecords.add(TableInfo.Index("index_habit_records_date", false, listOf("date"),
            listOf("ASC")))
        _indicesHabitRecords.add(TableInfo.Index("index_habit_records_habitId_date", true,
            listOf("habitId", "date"), listOf("ASC", "ASC")))
        val _infoHabitRecords: TableInfo = TableInfo("habit_records", _columnsHabitRecords,
            _foreignKeysHabitRecords, _indicesHabitRecords)
        val _existingHabitRecords: TableInfo = read(db, "habit_records")
        if (!_infoHabitRecords.equals(_existingHabitRecords)) {
          return RoomOpenHelper.ValidationResult(false, """
              |habit_records(com.pankaj.habitflow.data.local.entity.HabitRecordEntity).
              | Expected:
              |""".trimMargin() + _infoHabitRecords + """
              |
              | Found:
              |""".trimMargin() + _existingHabitRecords)
        }
        return RoomOpenHelper.ValidationResult(true, null)
      }
    }, "6861a2d97902128f618f624994ce3d6b", "e4347c6dd79dec9bb8cece4d280ef28a")
    val _sqliteConfig: SupportSQLiteOpenHelper.Configuration =
        SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build()
    val _helper: SupportSQLiteOpenHelper = config.sqliteOpenHelperFactory.create(_sqliteConfig)
    return _helper
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: HashMap<String, String> = HashMap<String, String>(0)
    val _viewTables: HashMap<String, Set<String>> = HashMap<String, Set<String>>(0)
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "habits","habit_records")
  }

  public override fun clearAllTables() {
    super.assertNotMainThread()
    val _db: SupportSQLiteDatabase = super.openHelper.writableDatabase
    val _supportsDeferForeignKeys: Boolean = android.os.Build.VERSION.SDK_INT >=
        android.os.Build.VERSION_CODES.LOLLIPOP
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE")
      }
      super.beginTransaction()
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE")
      }
      _db.execSQL("DELETE FROM `habits`")
      _db.execSQL("DELETE FROM `habit_records`")
      super.setTransactionSuccessful()
    } finally {
      super.endTransaction()
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE")
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close()
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM")
      }
    }
  }

  protected override fun getRequiredTypeConverters(): Map<Class<out Any>, List<Class<out Any>>> {
    val _typeConvertersMap: HashMap<Class<out Any>, List<Class<out Any>>> =
        HashMap<Class<out Any>, List<Class<out Any>>>()
    _typeConvertersMap.put(HabitDao::class.java, HabitDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(HabitRecordDao::class.java, HabitRecordDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecs(): Set<Class<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: HashSet<Class<out AutoMigrationSpec>> =
        HashSet<Class<out AutoMigrationSpec>>()
    return _autoMigrationSpecsSet
  }

  public override
      fun getAutoMigrations(autoMigrationSpecs: Map<Class<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = ArrayList<Migration>()
    return _autoMigrations
  }

  public override fun habitDao(): HabitDao = _habitDao.value

  public override fun habitRecordDao(): HabitRecordDao = _habitRecordDao.value
}
