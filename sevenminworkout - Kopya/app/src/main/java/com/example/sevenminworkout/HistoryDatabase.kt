package com.example.sevenminworkout
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
// veritabanını oluştur
@Database(entities = [HistoryEntity::class],version = 1)
abstract class HistoryDatabase:RoomDatabase(){

    abstract fun historyDao():HistoryDao

    companion object {
        /**
         * INSTANCE, getInstance aracılığıyla döndürülen herhangi bir veritabanına bir referans tutacaktır.
         * Paylaşılan verilerdeki iş parçacığı, diğer iş parçacıkları tarafından görülebilir.
         */
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        fun getInstance(context: Context): HistoryDatabase {
            synchronized(this) {
                var instance = INSTANCE

                //'null' ise yeni bir veritabanı örneği oluşturun.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HistoryDatabase::class.java,
                        "history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    // INSTANCE'ı yeni oluşturulan veritabanına atama
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}