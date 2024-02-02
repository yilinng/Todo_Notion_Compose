package com.example.todonotioncompose.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todonotioncompose.data.Keyword.Keyword
import com.example.todonotioncompose.data.Keyword.KeywordDao
import com.example.todonotioncompose.data.Token.TokenDao
import com.example.todonotioncompose.data.Token.Token

@Database(entities = [Keyword::class, Token::class], version = 1, exportSchema = false)
abstract class TodoNotionDatabase : RoomDatabase() {
    abstract fun keywordDao(): KeywordDao
    abstract fun tokenDao(): TokenDao

    companion object {
        @Volatile
        private var Instance: TodoNotionDatabase? = null

        fun getDatabase(context: Context): TodoNotionDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TodoNotionDatabase::class.java, "todoNotion_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}