package com.example.sevenminworkout

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//insert metoduyla bir dao arabirimi oluşturdum
@Dao
interface HistoryDao {

    @Insert
    suspend fun insert(historyEntity: HistoryEntity)

    @Query("Select * from 'history-table'")
    fun fetchALlDates():Flow<List<HistoryEntity>>
}