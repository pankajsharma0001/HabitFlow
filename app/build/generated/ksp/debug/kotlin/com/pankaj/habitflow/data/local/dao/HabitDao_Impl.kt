package com.pankaj.habitflow.`data`.local.dao

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityDeletionOrUpdateAdapter
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.pankaj.habitflow.`data`.local.entity.HabitEntity
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class HabitDao_Impl(
  __db: RoomDatabase,
) : HabitDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfHabitEntity: EntityInsertionAdapter<HabitEntity>

  private val __updateAdapterOfHabitEntity: EntityDeletionOrUpdateAdapter<HabitEntity>

  private val __preparedStmtOfArchiveHabit: SharedSQLiteStatement

  private val __preparedStmtOfUnarchiveHabit: SharedSQLiteStatement

  private val __preparedStmtOfSoftDeleteHabit: SharedSQLiteStatement

  private val __preparedStmtOfHardDeleteHabit: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfHabitEntity = object : EntityInsertionAdapter<HabitEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `habits` (`id`,`name`,`description`,`category`,`colorHex`,`iconName`,`reminderTimeMinutes`,`createdAt`,`isArchived`,`syncStatus`,`lastModified`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: HabitEntity) {
        statement.bindString(1, entity.id)
        statement.bindString(2, entity.name)
        statement.bindString(3, entity.description)
        statement.bindString(4, entity.category)
        statement.bindString(5, entity.colorHex)
        statement.bindString(6, entity.iconName)
        val _tmpReminderTimeMinutes: Int? = entity.reminderTimeMinutes
        if (_tmpReminderTimeMinutes == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpReminderTimeMinutes.toLong())
        }
        statement.bindLong(8, entity.createdAt)
        val _tmp: Int = if (entity.isArchived) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindString(10, entity.syncStatus)
        statement.bindLong(11, entity.lastModified)
      }
    }
    this.__updateAdapterOfHabitEntity = object : EntityDeletionOrUpdateAdapter<HabitEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `habits` SET `id` = ?,`name` = ?,`description` = ?,`category` = ?,`colorHex` = ?,`iconName` = ?,`reminderTimeMinutes` = ?,`createdAt` = ?,`isArchived` = ?,`syncStatus` = ?,`lastModified` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: HabitEntity) {
        statement.bindString(1, entity.id)
        statement.bindString(2, entity.name)
        statement.bindString(3, entity.description)
        statement.bindString(4, entity.category)
        statement.bindString(5, entity.colorHex)
        statement.bindString(6, entity.iconName)
        val _tmpReminderTimeMinutes: Int? = entity.reminderTimeMinutes
        if (_tmpReminderTimeMinutes == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpReminderTimeMinutes.toLong())
        }
        statement.bindLong(8, entity.createdAt)
        val _tmp: Int = if (entity.isArchived) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindString(10, entity.syncStatus)
        statement.bindLong(11, entity.lastModified)
        statement.bindString(12, entity.id)
      }
    }
    this.__preparedStmtOfArchiveHabit = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String =
            "UPDATE habits SET isArchived = 1, lastModified = ?, syncStatus = 'PENDING_UPDATE' WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfUnarchiveHabit = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String =
            "UPDATE habits SET isArchived = 0, lastModified = ?, syncStatus = 'PENDING_UPDATE' WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfSoftDeleteHabit = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String =
            "UPDATE habits SET syncStatus = 'PENDING_DELETE', lastModified = ? WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfHardDeleteHabit = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM habits WHERE id = ?"
        return _query
      }
    }
  }

  public override suspend fun insertHabit(habit: HabitEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfHabitEntity.insert(habit)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateHabit(habit: HabitEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfHabitEntity.handle(habit)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun archiveHabit(habitId: String, now: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfArchiveHabit.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, now)
      _argIndex = 2
      _stmt.bindString(_argIndex, habitId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfArchiveHabit.release(_stmt)
      }
    }
  })

  public override suspend fun unarchiveHabit(habitId: String, now: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfUnarchiveHabit.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, now)
      _argIndex = 2
      _stmt.bindString(_argIndex, habitId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfUnarchiveHabit.release(_stmt)
      }
    }
  })

  public override suspend fun softDeleteHabit(habitId: String, now: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSoftDeleteHabit.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, now)
      _argIndex = 2
      _stmt.bindString(_argIndex, habitId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSoftDeleteHabit.release(_stmt)
      }
    }
  })

  public override suspend fun hardDeleteHabit(habitId: String): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfHardDeleteHabit.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, habitId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfHardDeleteHabit.release(_stmt)
      }
    }
  })

  public override fun getAllHabitsFlow(): Flow<List<HabitEntity>> {
    val _sql: String =
        "SELECT * FROM habits WHERE syncStatus != 'PENDING_DELETE' ORDER BY createdAt DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("habits"), object :
        Callable<List<HabitEntity>> {
      public override fun call(): List<HabitEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfColorHex: Int = getColumnIndexOrThrow(_cursor, "colorHex")
          val _cursorIndexOfIconName: Int = getColumnIndexOrThrow(_cursor, "iconName")
          val _cursorIndexOfReminderTimeMinutes: Int = getColumnIndexOrThrow(_cursor,
              "reminderTimeMinutes")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "syncStatus")
          val _cursorIndexOfLastModified: Int = getColumnIndexOrThrow(_cursor, "lastModified")
          val _result: MutableList<HabitEntity> = ArrayList<HabitEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: HabitEntity
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpReminderTimeMinutes: Int?
            if (_cursor.isNull(_cursorIndexOfReminderTimeMinutes)) {
              _tmpReminderTimeMinutes = null
            } else {
              _tmpReminderTimeMinutes = _cursor.getInt(_cursorIndexOfReminderTimeMinutes)
            }
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpIsArchived: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            val _tmpLastModified: Long
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified)
            _item =
                HabitEntity(_tmpId,_tmpName,_tmpDescription,_tmpCategory,_tmpColorHex,_tmpIconName,_tmpReminderTimeMinutes,_tmpCreatedAt,_tmpIsArchived,_tmpSyncStatus,_tmpLastModified)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun getActiveHabitsFlow(): Flow<List<HabitEntity>> {
    val _sql: String =
        "SELECT * FROM habits WHERE isArchived = 0 AND syncStatus != 'PENDING_DELETE' ORDER BY createdAt DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("habits"), object :
        Callable<List<HabitEntity>> {
      public override fun call(): List<HabitEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfColorHex: Int = getColumnIndexOrThrow(_cursor, "colorHex")
          val _cursorIndexOfIconName: Int = getColumnIndexOrThrow(_cursor, "iconName")
          val _cursorIndexOfReminderTimeMinutes: Int = getColumnIndexOrThrow(_cursor,
              "reminderTimeMinutes")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "syncStatus")
          val _cursorIndexOfLastModified: Int = getColumnIndexOrThrow(_cursor, "lastModified")
          val _result: MutableList<HabitEntity> = ArrayList<HabitEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: HabitEntity
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpReminderTimeMinutes: Int?
            if (_cursor.isNull(_cursorIndexOfReminderTimeMinutes)) {
              _tmpReminderTimeMinutes = null
            } else {
              _tmpReminderTimeMinutes = _cursor.getInt(_cursorIndexOfReminderTimeMinutes)
            }
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpIsArchived: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            val _tmpLastModified: Long
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified)
            _item =
                HabitEntity(_tmpId,_tmpName,_tmpDescription,_tmpCategory,_tmpColorHex,_tmpIconName,_tmpReminderTimeMinutes,_tmpCreatedAt,_tmpIsArchived,_tmpSyncStatus,_tmpLastModified)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun getHabitByIdFlow(habitId: String): Flow<HabitEntity?> {
    val _sql: String = "SELECT * FROM habits WHERE id = ? AND syncStatus != 'PENDING_DELETE'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, habitId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("habits"), object : Callable<HabitEntity?>
        {
      public override fun call(): HabitEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfColorHex: Int = getColumnIndexOrThrow(_cursor, "colorHex")
          val _cursorIndexOfIconName: Int = getColumnIndexOrThrow(_cursor, "iconName")
          val _cursorIndexOfReminderTimeMinutes: Int = getColumnIndexOrThrow(_cursor,
              "reminderTimeMinutes")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "syncStatus")
          val _cursorIndexOfLastModified: Int = getColumnIndexOrThrow(_cursor, "lastModified")
          val _result: HabitEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpReminderTimeMinutes: Int?
            if (_cursor.isNull(_cursorIndexOfReminderTimeMinutes)) {
              _tmpReminderTimeMinutes = null
            } else {
              _tmpReminderTimeMinutes = _cursor.getInt(_cursorIndexOfReminderTimeMinutes)
            }
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpIsArchived: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            val _tmpLastModified: Long
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified)
            _result =
                HabitEntity(_tmpId,_tmpName,_tmpDescription,_tmpCategory,_tmpColorHex,_tmpIconName,_tmpReminderTimeMinutes,_tmpCreatedAt,_tmpIsArchived,_tmpSyncStatus,_tmpLastModified)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override suspend fun getHabitById(habitId: String): HabitEntity? {
    val _sql: String = "SELECT * FROM habits WHERE id = ? AND syncStatus != 'PENDING_DELETE'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, habitId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<HabitEntity?> {
      public override fun call(): HabitEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfColorHex: Int = getColumnIndexOrThrow(_cursor, "colorHex")
          val _cursorIndexOfIconName: Int = getColumnIndexOrThrow(_cursor, "iconName")
          val _cursorIndexOfReminderTimeMinutes: Int = getColumnIndexOrThrow(_cursor,
              "reminderTimeMinutes")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "syncStatus")
          val _cursorIndexOfLastModified: Int = getColumnIndexOrThrow(_cursor, "lastModified")
          val _result: HabitEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpReminderTimeMinutes: Int?
            if (_cursor.isNull(_cursorIndexOfReminderTimeMinutes)) {
              _tmpReminderTimeMinutes = null
            } else {
              _tmpReminderTimeMinutes = _cursor.getInt(_cursorIndexOfReminderTimeMinutes)
            }
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpIsArchived: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            val _tmpLastModified: Long
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified)
            _result =
                HabitEntity(_tmpId,_tmpName,_tmpDescription,_tmpCategory,_tmpColorHex,_tmpIconName,_tmpReminderTimeMinutes,_tmpCreatedAt,_tmpIsArchived,_tmpSyncStatus,_tmpLastModified)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getActiveHabits(): List<HabitEntity> {
    val _sql: String =
        "SELECT * FROM habits WHERE isArchived = 0 AND syncStatus != 'PENDING_DELETE'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<HabitEntity>> {
      public override fun call(): List<HabitEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfColorHex: Int = getColumnIndexOrThrow(_cursor, "colorHex")
          val _cursorIndexOfIconName: Int = getColumnIndexOrThrow(_cursor, "iconName")
          val _cursorIndexOfReminderTimeMinutes: Int = getColumnIndexOrThrow(_cursor,
              "reminderTimeMinutes")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "syncStatus")
          val _cursorIndexOfLastModified: Int = getColumnIndexOrThrow(_cursor, "lastModified")
          val _result: MutableList<HabitEntity> = ArrayList<HabitEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: HabitEntity
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpReminderTimeMinutes: Int?
            if (_cursor.isNull(_cursorIndexOfReminderTimeMinutes)) {
              _tmpReminderTimeMinutes = null
            } else {
              _tmpReminderTimeMinutes = _cursor.getInt(_cursorIndexOfReminderTimeMinutes)
            }
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpIsArchived: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            val _tmpLastModified: Long
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified)
            _item =
                HabitEntity(_tmpId,_tmpName,_tmpDescription,_tmpCategory,_tmpColorHex,_tmpIconName,_tmpReminderTimeMinutes,_tmpCreatedAt,_tmpIsArchived,_tmpSyncStatus,_tmpLastModified)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getAllHabits(): List<HabitEntity> {
    val _sql: String = "SELECT * FROM habits WHERE syncStatus != 'PENDING_DELETE'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<HabitEntity>> {
      public override fun call(): List<HabitEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfColorHex: Int = getColumnIndexOrThrow(_cursor, "colorHex")
          val _cursorIndexOfIconName: Int = getColumnIndexOrThrow(_cursor, "iconName")
          val _cursorIndexOfReminderTimeMinutes: Int = getColumnIndexOrThrow(_cursor,
              "reminderTimeMinutes")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "syncStatus")
          val _cursorIndexOfLastModified: Int = getColumnIndexOrThrow(_cursor, "lastModified")
          val _result: MutableList<HabitEntity> = ArrayList<HabitEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: HabitEntity
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpReminderTimeMinutes: Int?
            if (_cursor.isNull(_cursorIndexOfReminderTimeMinutes)) {
              _tmpReminderTimeMinutes = null
            } else {
              _tmpReminderTimeMinutes = _cursor.getInt(_cursorIndexOfReminderTimeMinutes)
            }
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpIsArchived: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            val _tmpLastModified: Long
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified)
            _item =
                HabitEntity(_tmpId,_tmpName,_tmpDescription,_tmpCategory,_tmpColorHex,_tmpIconName,_tmpReminderTimeMinutes,_tmpCreatedAt,_tmpIsArchived,_tmpSyncStatus,_tmpLastModified)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getActiveHabitCount(): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM habits WHERE isArchived = 0 AND syncStatus != 'PENDING_DELETE'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Int> {
      public override fun call(): Int {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Int
          if (_cursor.moveToFirst()) {
            val _tmp: Int
            _tmp = _cursor.getInt(0)
            _result = _tmp
          } else {
            _result = 0
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getHabitsWithReminders(): List<HabitEntity> {
    val _sql: String =
        "SELECT * FROM habits WHERE reminderTimeMinutes IS NOT NULL AND isArchived = 0 AND syncStatus != 'PENDING_DELETE'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<HabitEntity>> {
      public override fun call(): List<HabitEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfColorHex: Int = getColumnIndexOrThrow(_cursor, "colorHex")
          val _cursorIndexOfIconName: Int = getColumnIndexOrThrow(_cursor, "iconName")
          val _cursorIndexOfReminderTimeMinutes: Int = getColumnIndexOrThrow(_cursor,
              "reminderTimeMinutes")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "syncStatus")
          val _cursorIndexOfLastModified: Int = getColumnIndexOrThrow(_cursor, "lastModified")
          val _result: MutableList<HabitEntity> = ArrayList<HabitEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: HabitEntity
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpReminderTimeMinutes: Int?
            if (_cursor.isNull(_cursorIndexOfReminderTimeMinutes)) {
              _tmpReminderTimeMinutes = null
            } else {
              _tmpReminderTimeMinutes = _cursor.getInt(_cursorIndexOfReminderTimeMinutes)
            }
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpIsArchived: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            val _tmpLastModified: Long
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified)
            _item =
                HabitEntity(_tmpId,_tmpName,_tmpDescription,_tmpCategory,_tmpColorHex,_tmpIconName,_tmpReminderTimeMinutes,_tmpCreatedAt,_tmpIsArchived,_tmpSyncStatus,_tmpLastModified)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
