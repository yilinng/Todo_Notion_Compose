package com.example.todonotioncompose

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todonotioncompose.data.Keyword.KeywordDao
import com.example.todonotioncompose.data.TodoNotionDatabase
import com.example.todonotioncompose.data.Keyword.Keyword

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.Before

import org.junit.runner.RunWith
import org.junit.After
import java.io.IOException

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test


//https://developer.android.com/codelabs/basic-android-kotlin-compose-update-data-room?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-6-pathway-2%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-update-data-room#4
@RunWith(AndroidJUnit4::class)
class KeywordDaoTest {
    private lateinit var keywordDao: KeywordDao
    private lateinit var todoNotionDatabase: TodoNotionDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        todoNotionDatabase = Room.inMemoryDatabaseBuilder(context, TodoNotionDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        keywordDao = todoNotionDatabase.keywordDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        todoNotionDatabase.close()
    }

    private val keyWord1 = Keyword(keyName = "sea")
    private val keyWord2 = Keyword(keyName = "moon")

    private suspend fun addOneKeywordToDb() {
        keywordDao.insert(keyWord1)
    }

    private suspend fun addTwoKeywordsToDb() {
        keywordDao.insert(keyWord1)
        keywordDao.insert(keyWord2)
    }


    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsKeywordIntoDB() = runBlocking {
        addOneKeywordToDb()
        val allKeywords = keywordDao.getKeys().first()
        assertEquals(allKeywords[0],  keyWord1)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllKeywords_returnsAllKeywordsFromDB() = runBlocking {
        addTwoKeywordsToDb()
        val allKeywords = keywordDao.getKeys().first()
        assertEquals(allKeywords[0], keyWord1)
        assertEquals(allKeywords[1], keyWord2)
    }

    //https://developer.android.com/codelabs/basic-android-kotlin-compose-update-data-room?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-6-pathway-2%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-update-data-room#7
}