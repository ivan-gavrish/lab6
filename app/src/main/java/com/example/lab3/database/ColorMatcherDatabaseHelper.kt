package com.example.lab3.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.lab3.R

class ColorMatcherDatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ColorMatcher.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "score_records"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_DIFFICULTY_EASY_SCORE = "difficulty_easy_score"
        private const val COLUMN_DIFFICULTY_MEDIUM_SCORE = "difficulty_medium_score"
        private const val COLUMN_DIFFICULTY_HARD_SCORE = "difficulty_hard_score"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_EMAIL TEXT NOT NULL UNIQUE, " +
                "$COLUMN_DIFFICULTY_EASY_SCORE INTEGER, " +
                "$COLUMN_DIFFICULTY_MEDIUM_SCORE INTEGER, " +
                "$COLUMN_DIFFICULTY_HARD_SCORE INTEGER);"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    /**
     * Add newly registered user to the database
     *
     * @param email an email of the registered user
     */
    fun addUser(email: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_DIFFICULTY_EASY_SCORE, 0)
            put(COLUMN_DIFFICULTY_MEDIUM_SCORE, 0)
            put(COLUMN_DIFFICULTY_HARD_SCORE, 0)
        }

        val result = db.insert(TABLE_NAME, null, contentValues)

        if (result == -1L) {
            Toast.makeText(context, "Failed to add record in the database", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Record successfully added to the database", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Get all user's emails and scores for specified difficulty
     *
     * @param difficulty a difficulty of the game for which we need to retrieve data
     */
    fun getUserEmailAndScore(difficulty: String): Cursor? {
        val difficultyScore = when (difficulty) {
            context.getString(R.string.easy) -> {
                COLUMN_DIFFICULTY_EASY_SCORE
            }
            context.getString(R.string.medium) -> {
                COLUMN_DIFFICULTY_MEDIUM_SCORE
            }
            else -> {
                COLUMN_DIFFICULTY_HARD_SCORE
            }
        }
        val query = "SELECT $COLUMN_EMAIL, $difficultyScore FROM $TABLE_NAME ORDER BY $difficultyScore DESC"

        return this.readableDatabase?.rawQuery(query, null)
    }

    /**
     * Get user's score from the database
     *
     * @param email an user's email for which we need to get data
     * @param difficulty a difficulty of the game for which we need to get score
     */
    fun getUserScore(email: String, difficulty: String): Cursor? {
        val difficultyScore = when (difficulty) {
            context.getString(R.string.easy) -> {
                COLUMN_DIFFICULTY_EASY_SCORE
            }
            context.getString(R.string.medium) -> {
                COLUMN_DIFFICULTY_MEDIUM_SCORE
            }
            else -> {
                COLUMN_DIFFICULTY_HARD_SCORE
            }
        }
        val query = "SELECT $difficultyScore FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ?"
        return this.readableDatabase?.rawQuery(query, arrayOf(email))
    }

    /**
     * Update user's score in the database with new record one
     *
     * @param email an user's email for which we need to update data
     * @param score a new record score
     */
    fun updateScore(email: String, difficulty: String, score: Int) {
        val db = this.writableDatabase
        val difficultyScore = when (difficulty) {
            context.getString(R.string.easy) -> {
                COLUMN_DIFFICULTY_EASY_SCORE
            }
            context.getString(R.string.medium) -> {
                COLUMN_DIFFICULTY_MEDIUM_SCORE
            }
            else -> {
                COLUMN_DIFFICULTY_HARD_SCORE
            }
        }
        val contentValues = ContentValues().apply {
            put(difficultyScore, score)
        }
        val result = db.update(TABLE_NAME, contentValues, "$COLUMN_EMAIL = ?", arrayOf(email))

        if (result == -1) {
            Toast.makeText(context, "Failed to update record", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Record successfully updated", Toast.LENGTH_SHORT).show()
        }
    }
}