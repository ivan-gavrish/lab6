package com.example.lab3.shared.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab3.ui.gamescreen.Colors
import com.example.lab3.ui.gamescreen.RandomColor
import com.google.firebase.auth.FirebaseAuth

class GameViewModel : ViewModel(), RandomColor {
    private lateinit var _email: String
    private lateinit var _difficulty: String
    private var _leftWord = MutableLiveData<String>()
    private var _leftWordColor = MutableLiveData<String>()
    private var _rightWord = MutableLiveData<String>()
    private var _rightWordColor = MutableLiveData<String>()
    private var _score = MutableLiveData<Int>()
    private val colorNames = Colors.colorNames()
    private val colorValues = Colors.colorValues()
    private var _auth: FirebaseAuth

    val email: String get() = _email
    val difficulty: String get() = _difficulty
    val leftWord: LiveData<String> get() = _leftWord
    val leftWordColor: LiveData<String> get() = _leftWordColor
    val rightWord: LiveData<String> get() = _rightWord
    val rightWordColor: LiveData<String> get() = _rightWordColor
    val score: LiveData<Int> get() = _score
    val auth: FirebaseAuth get() = _auth

    init {
        getNextWords()
        getNextWordColors()
        _auth = FirebaseAuth.getInstance()
    }

    fun setEmail(email: String) {
        _email = email
    }

    fun setDifficulty(difficulty: String = "Easy") {
        _difficulty = difficulty
    }

    override fun randomColorName(): String = colorNames.random()

    override fun randomColorValue(): String = colorValues.random()

    /**
     * Gets next color names for left and right TextViews
     */
    private fun getNextWords() {
        _leftWord.value = randomColorName()
        _rightWord.value = randomColorName()
    }

    /**
     * Gets next left and right TextViews colors
     */
    private fun getNextWordColors() {
        _leftWordColor.value = randomColorValue()
        _rightWordColor.value = randomColorValue()
    }

    /**
     * Checks whether we can set next color name and color value or not.
     * @return false if difficulty is "Hard" and user scored 100 points
     */
    fun nextWords(): Boolean {
        if (difficulty == "Hard" && score.value == 100) {
            return false
        }

        getNextWords()
        getNextWordColors()
        return true
    }

    /**
     * Checks whether user answer correct, i.e. if color name (left TextView element) matches
     * color value (right TextView element color)
     * @param colorName the name of the color from the left TextView element
     * @param colorValue the actual color of the right TextView element
     * @param userAnswer the text from button clicked that is "Yes" or "No"
     * @return true:
     * - if the color name matches color value and the user answer is yes;
     * - if the color name doesn't match color value and the user answer is no
     */
    fun isUserAnswerCorrect(colorName: String, colorValue: Int, userAnswer: String): Boolean {
        val color = Colors.colors[String.format("#%06X", 0xFFFFFF and colorValue)]
        var isCorrect = false
        val answerYes = "yes"
        val answerNo = "no"

        if (colorName == color && userAnswer.lowercase() == answerYes ||
            (colorName != color && userAnswer.lowercase() == answerNo)) {
            isCorrect = true
            _score.value = _score.value!!.plus(10)
        }

        return isCorrect
    }

    /**
     * Resets score to zero and initializes color names and values before starting new game
     */
    fun reinitializeGameData() {
        _score.value = 0
        getNextWords()
        getNextWordColors()
    }
}