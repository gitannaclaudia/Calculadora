package br.edu.ifsp.scl.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.ParseException
import br.edu.ifsp.scl.calculadora.databinding.ActivityMainBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.IllegalArgumentException

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    private var isFutureOperationButtonClicked: Boolean = false
    private var isInstantOperationButtonClicked: Boolean = false
    private var isEqualButtonClicked: Boolean = false

    private var currentNumber: Double = 0.0
    private var currentResult: Double = 0.0

    private var historyText = ""
    private var historyInstantOperationText = ""
    private var historyActionList: ArrayList<String> = ArrayList()

    private val ZERO: String = "0"
    private val ONE: String = "1"
    private val TWO: String = "2"
    private val THREE: String = "3"
    private val FOUR: String = "4"
    private val FIVE: String = "5"
    private val SIX: String = "6"
    private val SEVEN: String = "7"
    private val EIGHT: String = "8"
    private val NINE: String = "9"

    private val INIT = ""

    private val ADDITION = " + "
    private val SUBTRACTION = " − "
    private val MULTIPLICATION = " × "
    private val DIVISION = " ÷ "

    private val NEGATE = "negate"
    private val COMMA = ","
    private val EQUAL = " = "

    private var currentOperation = INIT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        activityMainBinding.button0.setOnClickListener { onNumberButtonClick(ZERO) }
        activityMainBinding.button1.setOnClickListener { onNumberButtonClick(ONE) }
        activityMainBinding.button2.setOnClickListener { onNumberButtonClick(TWO) }
        activityMainBinding.button3.setOnClickListener { onNumberButtonClick(THREE) }
        activityMainBinding.button4.setOnClickListener { onNumberButtonClick(FOUR) }
        activityMainBinding.button5.setOnClickListener { onNumberButtonClick(FIVE) }
        activityMainBinding.button6.setOnClickListener { onNumberButtonClick(SIX) }
        activityMainBinding.button7.setOnClickListener { onNumberButtonClick(SEVEN) }
        activityMainBinding.button8.setOnClickListener { onNumberButtonClick(EIGHT) }
        activityMainBinding.button9.setOnClickListener { onNumberButtonClick(NINE) }
        activityMainBinding.buttonAddition.setOnClickListener{ onFutureOperationButtonClick(ADDITION) }
        activityMainBinding.buttonSubtraction.setOnClickListener { onFutureOperationButtonClick(SUBTRACTION) }
        activityMainBinding.buttonMultiplication.setOnClickListener { onFutureOperationButtonClick(MULTIPLICATION) }
        activityMainBinding.buttonDivision.setOnClickListener { onFutureOperationButtonClick(DIVISION) }
        activityMainBinding.buttonCe.setOnClickListener { clearEntry() }
        activityMainBinding.buttonC.setOnClickListener {
            currentNumber = 0.0
            currentResult = 0.0
            currentOperation = INIT

            historyText = ""
            historyInstantOperationText = ""

            activityMainBinding.numberCurrent.text = formatDoubleToString(currentNumber)
            activityMainBinding.numberHistory.text = historyText

            isFutureOperationButtonClicked = false
            isEqualButtonClicked = false
            isInstantOperationButtonClicked = false
        }
        activityMainBinding.buttonBackspace.setOnClickListener {
            if (isFutureOperationButtonClicked || isInstantOperationButtonClicked || isEqualButtonClicked) return@setOnClickListener

            var currentValue = activityMainBinding.numberCurrent.text.toString()
            val charsLimit = if (currentValue.first().isDigit()) 1 else 2

            currentValue = if (currentValue.length > charsLimit) {
                currentValue.substring(0, currentValue.length - 1)
            } else {
                ZERO
            }

            activityMainBinding.numberCurrent.text = currentValue
            currentNumber = formatStringToDouble(currentValue)
        }
        activityMainBinding.buttonPlusMinus.setOnClickListener {
            val currentValue: String = activityMainBinding.numberCurrent.text.toString()

            currentNumber = formatStringToDouble(currentValue)
            if (currentNumber == 0.0) return@setOnClickListener

            currentNumber *= -1
            activityMainBinding.numberCurrent.text = formatDoubleToString(currentNumber)

            if (isInstantOperationButtonClicked) {
                historyInstantOperationText = "($historyInstantOperationText)"
                historyInstantOperationText = StringBuilder().append(NEGATE).append(historyInstantOperationText).toString()
                activityMainBinding.numberHistory.text = StringBuilder().append(historyText).append(currentOperation).append(historyInstantOperationText).toString()
            }

            if (isEqualButtonClicked) {
                currentOperation = INIT
            }

            isFutureOperationButtonClicked = false
            isEqualButtonClicked = false
        }
        activityMainBinding.buttonComma.setOnClickListener {
            var currentValue: String = activityMainBinding.numberCurrent.text.toString()

            if (isFutureOperationButtonClicked || isInstantOperationButtonClicked || isEqualButtonClicked) {
                currentValue = StringBuilder().append(ZERO).append(COMMA).toString()
                if (isInstantOperationButtonClicked) {
                    historyInstantOperationText = ""
                    activityMainBinding.numberHistory.text = StringBuilder().append(historyText).append(currentOperation).toString()
                }
                if (isEqualButtonClicked) currentOperation = INIT
                currentNumber = 0.0
            } else if (currentValue.contains(COMMA)) {
                return@setOnClickListener
            } else currentValue = StringBuilder().append(currentValue).append(COMMA).toString()

            activityMainBinding.numberCurrent.text = currentValue

            isFutureOperationButtonClicked = false
            isInstantOperationButtonClicked = false
            isEqualButtonClicked = false
        }
        activityMainBinding.buttonEqual.setOnClickListener {
            if (isFutureOperationButtonClicked) {
                currentNumber = currentResult
            }

            val historyAllText = calculateResult()

            historyActionList.add(historyAllText)

            historyText = StringBuilder().append(formatDoubleToString(currentResult)).toString()

            activityMainBinding.numberHistory.text = ""

            isFutureOperationButtonClicked = false
            isEqualButtonClicked = true
        }
    }

    private fun onNumberButtonClick(number: String, isHistory: Boolean = false) {
        var currentValue: String = activityMainBinding.numberCurrent.text.toString()

        currentValue = if (currentValue == ZERO
                || isFutureOperationButtonClicked
                || isInstantOperationButtonClicked
                || isEqualButtonClicked
                || isHistory) number else StringBuilder().append(currentValue).append(number).toString()

        try {
            currentNumber = formatStringToDouble(currentValue)
        } catch (e: ParseException) {
            throw IllegalArgumentException("String deve ser um número")
        }

        activityMainBinding.numberCurrent.text = currentValue

        if (isEqualButtonClicked) {
            currentOperation = INIT
            historyText = ""
        }

        if (isInstantOperationButtonClicked) {
            historyInstantOperationText = ""
            activityMainBinding.numberHistory.text = StringBuilder().append(historyText).append(currentOperation).toString()
        }

        isFutureOperationButtonClicked = false
        isEqualButtonClicked = false

    }

    private fun onFutureOperationButtonClick(operation: String) {
        if (!isFutureOperationButtonClicked && !isEqualButtonClicked) {
            calculateResult()
        }

        currentOperation = operation

        if (isInstantOperationButtonClicked) {
            isInstantOperationButtonClicked = false
            historyText = activityMainBinding.numberHistory.text.toString()
        }
        activityMainBinding.numberHistory.text = StringBuilder().append(historyText).append(operation).toString()

        isFutureOperationButtonClicked = true
        isEqualButtonClicked = false
    }

    private fun calculateResult(): String {
        when (currentOperation) {
            INIT -> {
                currentResult = currentNumber
                historyText = StringBuilder().append(activityMainBinding.numberHistory.text).toString()
            }
            ADDITION -> currentResult += currentNumber
            SUBTRACTION -> currentResult -= currentNumber
            MULTIPLICATION -> currentResult *= currentNumber
            DIVISION -> currentResult /= currentNumber
        }

        activityMainBinding.numberCurrent.text = formatDoubleToString(currentResult)

        if (isInstantOperationButtonClicked) {
            isInstantOperationButtonClicked = false
            historyText = activityMainBinding.numberHistory.text.toString()
            if (isEqualButtonClicked) historyText = StringBuilder().append(historyText).append(currentOperation).append(formatDoubleToString(currentNumber)).toString()
        } else {
            historyText = StringBuilder().append(historyText).append(currentOperation).append(formatDoubleToString(currentNumber)).toString()
        }

        return StringBuilder().append(historyText).append(EQUAL).append(formatDoubleToString(currentResult)).toString()
    }

    private fun useNumberFormat(): DecimalFormat {
        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = ','

        val format = DecimalFormat("#.##############")
        format.decimalFormatSymbols = symbols

        return format
    }

    private fun formatDoubleToString(number: Double): String {
        return useNumberFormat().format(number)
    }

    private fun formatStringToDouble(number: String): Double {
        return useNumberFormat().parse(number)!!.toDouble()
    }

    private fun clearEntry(newNumber: Double = 0.0) {
        historyInstantOperationText = ""

        if (isEqualButtonClicked) {
            currentOperation = INIT
            historyText = ""
        }

        if (isInstantOperationButtonClicked) activityMainBinding.numberHistory.text = StringBuilder().append(historyText).append(currentOperation).toString()

        isInstantOperationButtonClicked = false
        isFutureOperationButtonClicked = false
        isEqualButtonClicked = false

        currentNumber = newNumber
        activityMainBinding.numberCurrent.text = formatDoubleToString(newNumber)
    }
}