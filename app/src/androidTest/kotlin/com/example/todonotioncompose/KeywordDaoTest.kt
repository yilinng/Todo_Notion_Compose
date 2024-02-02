package com.example.todonotioncompose

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todonotioncompose.data.Keyword.KeywordDao
import com.example.todonotioncompose.data.TodoNotionDatabase
import com.example.todonotioncompose.data.Keyword.Keyword

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.Before

import org.junit.runner.RunWith
import org.junit.After
import java.io.IOException

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


//https://developer.android.com/codelabs/basic-android-kotlin-compose-update-data-room?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-6-pathway-2%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-update-data-room#4
@RunWith(AndroidJUnit4::class)
class KeywordDaoTest {
    private lateinit var keywordDao: KeywordDao
    private lateinit var todoNotionDatabase: TodoNotionDatabase
    //SELECT * from keyword ORDER BY key_name ASC
    private val keyWord1 = Keyword(id = 1, keyName = "moon")
    private val keyWord2 = Keyword(id = 2, keyName = "sea")

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

    @Test
    @Throws(Exception::class)
    fun daoDeleteKeywords_deleteAllKeywordsInDB() = runBlocking{
        addTwoKeywordsToDb()
        keywordDao.delete(keyWord1)
        keywordDao.delete(keyWord2)
        val allKeywords =  keywordDao.getKeys().first()
        assertTrue(allKeywords.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun daoGetKeyword_returnsKeywordFromDB() =  runBlocking{
        addOneKeywordToDb()
        val keyword = keywordDao.getKey(1)
        assertEquals(keyword.first(), keyWord1)
    }


    private suspend fun addOneKeywordToDb() {
        keywordDao.insert(keyWord1)
    }

    private suspend fun addTwoKeywordsToDb() {
        keywordDao.insert(keyWord1)
        keywordDao.insert(keyWord2)
    }


}