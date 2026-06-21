package com.pankaj.habitflow.`data`.local.dao

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.pankaj.habitflow.`data`.local.entity.HabitRecordEntity
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
public class HabitRecordDao_Impl(
  __db: RoomDatabase,
) : HabitRecordDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfHabitRecordEntity: EntityInsertionAdapter<HabitRecordEntity>

  private val __preparedStmtOfDeleteRecord: SharedSQLiteStatement

  private val __preparedStmtOfSoftDeleteRecord: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfHabitRecordEntity = object :
        EntityInsertionAdapter<HabitRecordEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `habit_records` (`id`,`habitId`,`date`,`isCompleted`,`completedAt`,`syncStatus`,`lastModified`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: HabitRecordEntity) {
        statement.bindString(1, entity.id)
        statement.bindString(2, entity.habitId)
        statement.bindString(3, entity.date)
        val _tmp: Int = if (entity.isCompleted) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpCompletedAt)
        }
        statement.bindString(6, entity.syncStatus)
        statement.bindLong(7, entity.lastModified)
      }
    }
    this.__preparedStmtOfDeleteRecord = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM habit_records WHERE habitId = ? AND date = ?"
        return _query
      }
    }
    this.__preparedStmtOfSoftDeleteRecord = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = """
            |
            |        UPDATE habit_records SET syncStatus = 'PENDING_DELETE', lastModified = ? 
            |        WHERE habitId = ? AND date = ?
            |    
            """.trimMargin()
        return _query
      }
    }
  }

  public override suspend fun insertRecord(record: HabitRecordEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfHabitRecordEntity.insert(record)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deleteRecord(habitId: String, date: String): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteRecord.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, habitId)
      _argIndex = 2
      _stmt.bindString(_argIndex, date)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteRecord.release(_stmt)
      }
    }
  })

  public override suspend fun softDeleteRecord(
    habitId: String,
    date: String,
    now: Long,
  ): Unit = CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSoftDeleteRecord.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, now)
      _argIndex = 2
      _stmt.bindString(_argIndex, habitId)
      _argIndex = 3
      _stmt.bindString(_argIndex, date)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSoftDeleteRecord.release(_stmt)
      }
    }
  })

  public override suspend fun getRecord(habitId: String, date: String): HabitRecordEntity? {
    val _sql: String = "SELECT * FROM habit_records WHERE habitId = ? AND date = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, habitId)
    _argIndex = 2
    _statement.bindString(_argIndex, date)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<HabitRecordEntity?> {
      public override fun call(): HabitRecordEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfHabitId: Int = getColumnIndexOrThrow(_cursor, "habitId")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfIsCompleted: Int = getColumnIndexOrThrow(_cursor, "isCompleted")
          val _cursorIndexOfCompletedAt: Int = getColumnIndexOrThrow(_cursor, "completedAt")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "syncStatus")
          val _cursorIndexOfLastModified: Int = getColumnIndexOrThrow(_cursor, "lastModified")
          val _result: HabitRecordEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpHabitId: String
            _tmpHabitId = _cursor.getString(_cursorIndexOfHabitId)
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpIsCompleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted)
            _tmpIsCompleted = _tmp != 0
            val _tmpCompletedAt: Long?
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt)
            }
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            val _tmpLastModified: Long
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified)
            _result =
                HabitRecordEntity(_tmpId,_tmpHabitId,_tmpDate,_tmpIsCompleted,_tmpCompletedAt,_tmpSyncStatus,_tmpLastModified)
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

  public override fun getCompletedHabitIdsForDateFlow(date: String): Flow<List<String>> {
    val _sql: String =
        "SELECT habitId FROM habit_records WHERE date = ? AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, date)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("habit_records"), object :
        Callable<List<String>> {
      public override fun call(): List<String> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: MutableList<String> = ArrayList<String>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: String
            _item = _cursor.getString(0)
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

  public override suspend fun getCompletedHabitIdsForDate(date: String): List<String> {
    val _sql: String =
        "SELECT habitId FROM habit_records WHERE date = ? AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, date)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<String>> {
      public override fun call(): List<String> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: MutableList<String> = ArrayList<String>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: String
            _item = _cursor.getString(0)
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

  public override suspend fun getTotalCompletions(habitId: String): Int {
    val _sql: String = """
        |
        |        SELECT COUNT(*) FROM habit_records 
        |        WHERE habitId = ? AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, habitId)
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

  public override suspend fun getCompletedDatesForHabit(habitId: String): List<String> {
    val _sql: String = """
        |
        |        SELECT date FROM habit_records 
        |        WHERE habitId = ? AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
        |        ORDER BY date DESC
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, habitId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<String>> {
      public override fun call(): List<String> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: MutableList<String> = ArrayList<String>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: String
            _item = _cursor.getString(0)
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

  public override suspend fun getCompletedDatesForHabitInRange(
    habitId: String,
    startDate: String,
    endDate: String,
  ): List<String> {
    val _sql: String = """
        |
        |        SELECT date FROM habit_records 
        |        WHERE habitId = ? AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
        |        AND date >= ? AND date <= ?
        |        ORDER BY date ASC
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 3)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, habitId)
    _argIndex = 2
    _statement.bindString(_argIndex, startDate)
    _argIndex = 3
    _statement.bindString(_argIndex, endDate)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<String>> {
      public override fun call(): List<String> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: MutableList<String> = ArrayList<String>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: String
            _item = _cursor.getString(0)
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

  public override suspend fun getCompletionCountsByDate(startDate: String, endDate: String):
      List<DateCount> {
    val _sql: String = """
        |
        |        SELECT date, COUNT(*) as count FROM habit_records 
        |        WHERE isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
        |        AND date >= ? AND date <= ?
        |        GROUP BY date
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, startDate)
    _argIndex = 2
    _statement.bindString(_argIndex, endDate)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<DateCount>> {
      public override fun call(): List<DateCount> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfDate: Int = 0
          val _cursorIndexOfCount: Int = 1
          val _result: MutableList<DateCount> = ArrayList<DateCount>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: DateCount
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpCount: Int
            _tmpCount = _cursor.getInt(_cursorIndexOfCount)
            _item = DateCount(_tmpDate,_tmpCount)
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

  public override suspend fun getCompletedCountForDate(date: String): Int {
    val _sql: String = """
        |
        |        SELECT COUNT(DISTINCT habitId) FROM habit_records 
        |        WHERE date = ? AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, date)
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

  public override suspend fun getCompletedRecordsInRange(startDate: String, endDate: String):
      List<HabitRecordEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM habit_records 
        |        WHERE isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
        |        AND date >= ? AND date <= ?
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, startDate)
    _argIndex = 2
    _statement.bindString(_argIndex, endDate)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<HabitRecordEntity>> {
      public override fun call(): List<HabitRecordEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfHabitId: Int = getColumnIndexOrThrow(_cursor, "habitId")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfIsCompleted: Int = getColumnIndexOrThrow(_cursor, "isCompleted")
          val _cursorIndexOfCompletedAt: Int = getColumnIndexOrThrow(_cursor, "completedAt")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "syncStatus")
          val _cursorIndexOfLastModified: Int = getColumnIndexOrThrow(_cursor, "lastModified")
          val _result: MutableList<HabitRecordEntity> =
              ArrayList<HabitRecordEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: HabitRecordEntity
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpHabitId: String
            _tmpHabitId = _cursor.getString(_cursorIndexOfHabitId)
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpIsCompleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted)
            _tmpIsCompleted = _tmp != 0
            val _tmpCompletedAt: Long?
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt)
            }
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            val _tmpLastModified: Long
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified)
            _item =
                HabitRecordEntity(_tmpId,_tmpHabitId,_tmpDate,_tmpIsCompleted,_tmpCompletedAt,_tmpSyncStatus,_tmpLastModified)
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
