package com.a0100019.mypat.data.room.item

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.world.World
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Update
    suspend fun update(item: Item)

    @Query("SELECT * FROM item_table WHERE type != 'map' ORDER BY id DESC")
    suspend fun getAllItemData(): List<Item>

    @Query("SELECT * FROM item_table WHERE type != 'map' AND date != '0' ORDER BY id DESC")
    suspend fun getAllOpenItemData(): List<Item>

    @Query("SELECT * FROM item_table WHERE type != 'map' AND date == '0' ORDER BY id DESC")
    suspend fun getAllCloseItemData(): List<Item>

    @Query("SELECT * FROM item_table WHERE type = 'map' ORDER BY id DESC")
    suspend fun getAllMapData(): List<Item>

    @Query("SELECT * FROM item_table WHERE type = 'map' AND date != '0' ORDER BY id DESC")
    suspend fun getAllOpenMapData(): List<Item>

    @Query("SELECT * FROM item_table WHERE type = 'map' AND date == '0' ORDER BY id DESC")
    suspend fun getAllCloseMapData(): List<Item>

    @Query("SELECT * FROM item_table WHERE id = :id")
    suspend fun getItemDataById(id: String): Item

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<Item>)

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