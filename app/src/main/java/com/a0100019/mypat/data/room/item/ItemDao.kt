package com.a0100019.mypat.data.room.item

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("DELETE FROM item_table")
    suspend fun deleteAllItems()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'item_table'")
    suspend fun resetItemPrimaryKey()

    @Update
    suspend fun update(item: Item)

    @Query("UPDATE item_table SET date = :date, x = :x, y = :y, sizeFloat = :size WHERE id = :id")
    suspend fun updateItemData(id: Int, date: String, x: Float, y: Float, size: Float)

    @Query("SELECT * FROM item_table ORDER BY id DESC")
    suspend fun getAllItemData(): List<Item>

    @Query("SELECT * FROM item_table WHERE date != '0' ORDER BY id DESC")
    suspend fun getAllOpenItemData(): List<Item>

    @Query("SELECT * FROM item_table WHERE date == '0' ORDER BY id DESC")
    suspend fun getAllCloseItemData(): List<Item>

    @Query("SELECT * FROM item_table WHERE id = :id")
    suspend fun getItemDataById(id: String): Item

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Item>)

    // 특정 필드만 업데이트하는 쿼리
    @Query("UPDATE item_table SET x = :x, y = :y WHERE id = :id")
    suspend fun updateItemPosition(id: Int, x: Float, y: Float)

    // 전체 리스트의 x, y 값을 업데이트
    @Transaction
    suspend fun updateItemPositions(items: List<Item>) {
        for (item in items) {
            updateItemPosition(item.id, item.x, item.y)
        }
    }
}