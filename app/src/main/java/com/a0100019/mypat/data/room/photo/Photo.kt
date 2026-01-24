package com.a0100019.mypat.data.room.photo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_table")
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String = "0", // Diary 테이블의 date와 연결되는 외래키 역할
    var localPath: String = "0", // 기기 내 복사된 경로
    var firebaseUrl: String = "0", // 파이어스토리지 업로드 주소
    var isSynced: Boolean = false // 업로드 성공 여부
)