package sime3134.github.io.wheredidiputthat.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import sime3134.github.io.wheredidiputthat.model.database.Database
import javax.inject.Singleton
import androidx.room.Room as RoomDB
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DATABASE_NAME = "database"
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return RoomDB.databaseBuilder(
            context,
            Database::class.java,
            DATABASE_NAME
        ).build()
    }
}