package simple.compose.calculator

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import simple.compose.calculator.ui.theme.CalculatorComposeTheme
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Stack

class MainActivity : ComponentActivity() {

    companion object {
        private const val ADD = "+"
        private const val SUB = "-"
        private const val MUL = "*"
        private const val DIV = "/"
    }

    private val inputStack = Stack<String>()
    private val infixStack = Stack<String>()
    private val suffixStack = Stack<String>()

    private val operatorSet = hashSetOf(ADD, SUB, MUL, DIV)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        testCalc()

        setContent {
            val isDarkModel by remember { mutableStateOf(false) }

            CalculatorComposeTheme(darkTheme = isDarkModel) {
                CalculatorUI()
            }
        }
    }

    private fun testCalc() {
        inputStack.push("1")
        inputStack.push(ADD)
        inputStack.push("2")
        inputStack.push(ADD)
        inputStack.push("1")
        inputStack.push(".")
        inputStack.push("5")
        inputStack.push(MUL)
        inputStack.push("2")
        inputStack.push(DIV)
        inputStack.push("0")
        convertToInfix()
    }

    @Composable
    fun CalculatorUI() {
        Column {
            val displayModifier = Modifier
                .background(Color.Black)
                .fillMaxWidth()
                .weight(1f)

            val btnGroupModifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()

            DisplayUI(displayModifier)
            ButtonGroup(btnGroupModifier)
        }
    }

    @Composable
    fun DisplayUI(modifier: Modifier) {
        Column(modifier) {

        }
    }

    @Composable
    fun ButtonGroup(modifier: Modifier) {
        val optBtnColor = MaterialTheme.colorScheme.secondary
        val numBtnColor = MaterialTheme.colorScheme.tertiary
        Column(modifier) {
            Row() {
                ButtonUI(Modifier.weight(1f), text = "", textColor = optBtnColor) {

                }
                ButtonUI(Modifier.weight(1f), text = "AC", textColor = optBtnColor) {
                    acClear()
                }
                ButtonUI(Modifier.weight(1f), text = "Del", textColor = optBtnColor) {
                    delItem()
                }
                ButtonUI(Modifier.weight(1f), text = "/", textColor = optBtnColor) {
                    addItem(DIV)
                }
            }
            Row() {
                ButtonUI(Modifier.weight(1f), text = "7", textColor = numBtnColor) {
                    addItem("7")
                }
                ButtonUI(Modifier.weight(1f), text = "8", textColor = numBtnColor) {
                    addItem("8")
                }
                ButtonUI(Modifier.weight(1f), text = "9", textColor = numBtnColor) {
                    addItem("9")
                }
                ButtonUI(Modifier.weight(1f), text = "x", textColor = optBtnColor) {
                    addItem(MUL)
                }
            }
            Row() {
                ButtonUI(Modifier.weight(1f), text = "4", textColor = numBtnColor) {
                    addItem("4")
                }
                ButtonUI(Modifier.weight(1f), text = "5", textColor = numBtnColor) {
                    addItem("5")
                }
                ButtonUI(Modifier.weight(1f), text = "6", textColor = numBtnColor) {
                    addItem("6")
                }
                ButtonUI(Modifier.weight(1f), text = "-", textColor = optBtnColor) {
                    addItem(SUB)
                }
            }
            Row() {
                ButtonUI(Modifier.weight(1f), text = "1", textColor = numBtnColor) {
                    addItem("1")
                }
                ButtonUI(Modifier.weight(1f), text = "2", textColor = numBtnColor) {
                    addItem("2")
                }
                ButtonUI(Modifier.weight(1f), text = "3", textColor = numBtnColor) {
                    addItem("3")
                }
                ButtonUI(Modifier.weight(1f), text = "+", textColor = optBtnColor) {
                    addItem(ADD)
                }
            }
            Row() {
                ButtonUI(Modifier.weight(1f), text = "", textColor = optBtnColor)
                ButtonUI(Modifier.weight(1f), text = "0", textColor = numBtnColor) {
                    addItem("0")
                }
                ButtonUI(Modifier.weight(1f), text = ".", textColor = numBtnColor) {
                    addItem(".")
                }
                ButtonUI(Modifier.weight(1f), text = "=", textColor = optBtnColor) {
                    calcResult()
                }
            }
        }
    }

    @Composable
    fun ButtonUI(
        modifier: Modifier = Modifier,
        text: String = "",
        textColor: Color = Color.Black,
        onClick: () -> Unit = {}
    ) {
        Button(
            onClick, modifier.aspectRatio(1f),
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Text(text, color = textColor, fontSize = 20f.sp)
        }
    }

    private fun addItem(item: String) {
        inputStack.push(item)
        debugLog("items = $inputStack")

        convertToInfix()
    }

    private fun delItem() {
        inputStack.pop()
    }

    private fun acClear() {
        inputStack.clear()
    }

    private fun isOperator(item: String) = operatorSet.contains(item)

    private fun convertToInfix() {
        infixStack.clear()
        val strBuilder = StringBuilder()

        inputStack.forEach { item ->
            if (isOperator(item)) {
                val num = strBuilder.toString()
                infixStack.push(num)
                strBuilder.clear()
                infixStack.push(item)
            } else {
                strBuilder.append(item)
            }
        }
        if (strBuilder.isNotEmpty()) {
            val num = strBuilder.toString()
            infixStack.push(num)
            strBuilder.clear()
        }

        debugLog("infix = ${stackToString(infixStack)}")

        convertToSuffix()
    }

    private fun stackToString(stack: Stack<String>): String {
        val strBuilder = StringBuilder()
        stack.forEach { strBuilder.append(it) }
        return strBuilder.toString()
    }

    private fun debugLog(msg: String) {
        Log.d("SimplePeng", msg)
    }

    //转换为后缀表达式
    private fun convertToSuffix() {
        suffixStack.clear()
        val operatorStack = Stack<String>()//计算符号的栈

        for (item in infixStack) {
            if (isOperator(item)) {//如果是计算符号
                if (operatorStack.isEmpty()) {
                    operatorStack.push(item)
                    continue
                }
                while (operatorStack.isNotEmpty()) {
                    if (highPriority(item, operatorStack.last())) {//如果当前的计算符比栈顶的大，入栈
                        operatorStack.push(item)
                        break
                    }
                    val top = operatorStack.pop()
                    suffixStack.push(top)
                }
                if (operatorStack.isEmpty()) {
                    operatorStack.push(item)
                }
            } else {//如果是数字
                suffixStack.push(item)
            }
        }
        if (operatorStack.isNotEmpty()) {
            suffixStack.addAll(operatorStack.reversed())
            operatorStack.clear()
        }

        debugLog("suffix = $suffixStack")

        calcResult()
    }

    private fun highPriority(
        wait: String,
        top: String,
    ): Boolean {
        return (wait == MUL || wait == DIV) && (top == ADD || top == SUB)
    }

    private fun calcResult() {
        if (suffixStack.isEmpty()) return

        val numStack = Stack<BigDecimal>()

        for (item in suffixStack) {
            if (isOperator(item)) {
                if (numStack.size > 1) {
                    val one = numStack.pop()
                    val two = numStack.pop()
                    val tmpResult = calc(one, two, item)
                    numStack.push(tmpResult)
                }
            } else {
                numStack.push(item.toBigDecimal())
            }
        }

        val result = numStack.pop()
        debugLog("result = $result")
    }

    private fun calc(
        one: BigDecimal,
        two: BigDecimal,
        operator: String
    ): BigDecimal {
        return when (operator) {
            ADD -> two.add(one)
            SUB -> two.subtract(one)
            MUL -> two.multiply(one)
            DIV -> {
                if (one == BigDecimal(0)) {
                    showToast("不能除以0")
                    return BigDecimal(1)
                }
                two.divide(one, RoundingMode.HALF_UP)
            }
            else -> {
                debugLog("哪里来的操作符？")
                BigDecimal(1)
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

