package com.a0100019.mypat.data.room.area

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AreaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(area: Area)

    @Delete
    suspend fun delete(area: Area)

    @Query("DELETE FROM area_table")
    suspend fun deleteAllAreas()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'area_table'")
    suspend fun resetAreaPrimaryKey()

    @Update
    suspend fun update(area: Area)

    @Query("UPDATE area_table SET date = :date WHERE id = :id")
    suspend fun updateAreaData(id: Int, date: String)

    @Query("SELECT * FROM area_table ORDER BY id")
    suspend fun getAllAreaData(): List<Area>

    @Query("SELECT * FROM area_table WHERE date != '0' ORDER BY id")
    suspend fun getAllOpenAreaData(): List<Area>

    @Query("SELECT * FROM area_table WHERE date == '0' ORDER BY id")
    suspend fun getAllCloseAreaData(): List<Area>

    @Query("SELECT * FROM area_table WHERE id = :id")
    suspend fun getAreaDataById(id: String): Area

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(areas: List<Area>)

}