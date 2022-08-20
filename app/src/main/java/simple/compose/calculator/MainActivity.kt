package simple.compose.calculator

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.simple.spiderman.SpiderMan
import simple.compose.calculator.ui.theme.CalculatorComposeTheme
import java.lang.ArithmeticException
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Stack

class MainActivity : ComponentActivity() {

    companion object {
        private const val ADD = "+"
        private const val SUB = "-"
        private const val MUL = "*"
        private const val DIV = "/"
        private const val DOT = "."
    }

    private val inputStack = Stack<String>()
    private val infixStack = Stack<String>()
    private val suffixStack = Stack<String>()

    private val operatorSet = hashSetOf(ADD, SUB, MUL, DIV)

    private val items = mutableStateListOf<CalcItem>()

    private val isDarkModel = mutableStateOf(false)

    private val expText = mutableStateOf("")
    private val resultText = mutableStateOf("")

    private val orientation by lazy { mutableStateOf(resources.configuration.orientation) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        testCalc()

        setContent {
            CalculatorComposeTheme(darkTheme = isDarkModel.value) {
                if (orientation.value == Configuration.ORIENTATION_PORTRAIT) {
                    PortraitUI()
                } else {
                    LandscapeUI()
                }
            }
        }
    }

    //测试计算结果
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
    fun PortraitUI() {
        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
            DisplayUI(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5f.dp)
                    .padding(horizontal = 10f.dp)
                    .background(MaterialTheme.colorScheme.onBackground)
            )
            ButtonGroup(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
            )
        }
    }

    @Composable
    fun LandscapeUI() {
        Row(Modifier.background(Color.Red)) {
//            DisplayUI(
//                Modifier
//                    .fillMaxHeight()
//                    .weight(1f)
//                    .background(Color.Black)
//            )
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .height(0.5f.dp)
                    .padding(horizontal = 10f.dp)
                    .background(MaterialTheme.colorScheme.onBackground)
            )
            ButtonGroup(
                Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
            )
        }
    }

    @Composable
    fun DisplayUI(modifier: Modifier) {
        val textModifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
        val textColor = MaterialTheme.colorScheme.tertiary

        Column(modifier) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(items = items) { item ->
                    Text(
                        text = item.text,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        fontSize = 25.sp,
                        textAlign = TextAlign.End,
                    )
                }
            }
            Text(
                text = expText.value,
                modifier = textModifier,
                color = textColor,
                fontSize = 40.sp,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                lineHeight = 45.sp
            )
            Text(
                text = resultText.value,
                modifier = textModifier,
                color = textColor,
                fontSize = 26.sp,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                lineHeight = 35.sp
            )
        }

    }

    @Composable
    fun themeName() = if (isDarkModel.value) "Dark" else "Light"

    @Composable
    fun ButtonGroup(
        modifier: Modifier
    ) {
        val optBtnColor = MaterialTheme.colorScheme.secondary
        val numBtnColor = MaterialTheme.colorScheme.tertiary

        Column(modifier) {
            Row() {
                Button(
                    onClick = { isDarkModel.value = !isDarkModel.value },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Text(
                        themeName(),
                        color = optBtnColor,
                        fontSize = 17f.sp,
                        fontWeight = FontWeight.Bold
                    )
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
                    addItem(DOT)
                }
                ButtonUI(Modifier.weight(1f), text = "=", textColor = optBtnColor) {
                    equalBtnClick()
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
            onClick,
            modifier.aspectRatio(1f),
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Text(
                text,
                color = textColor,
                fontSize = 20f.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    private fun addItem(item: String) {
        if (isOperator(item) && inputStack.isEmpty()) {
            return
        }

        if (item == DOT && inputStack.isEmpty()) {
            return
        }
        if (item == DOT && isOperator(inputStack.last())) {
            return
        }

        if (isOperator(item) && isOperator(inputStack.last())) {
            inputStack.pop()
        }

        inputStack.push(item)
        debugLog("items = $inputStack")

        convertToInfix()
    }

    private fun delItem() {
        if (inputStack.isEmpty()) return
        inputStack.pop()

        convertToInfix()
    }

    private fun clearStack() {
        inputStack.clear()
        infixStack.clear()
        suffixStack.clear()
    }

    private fun acClear() {
        clearStack()
        expText.value = ""
        resultText.value = ""
    }

    //是否是计算符号
    private fun isOperator(item: String) = operatorSet.contains(item)

    //转成中缀表达式
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

        val text = stackToString(infixStack)
        debugLog("infix = $text")
        expText.value = text

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

    //转成后缀表达式
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

    //高优先级计算符号
    private fun highPriority(
        wait: String,
        top: String,
    ): Boolean {
        return (wait == MUL || wait == DIV) && (top == ADD || top == SUB)
    }

    //计算结果
    private fun calcResult() {
        if (infixStack.size < 2) return
        if (suffixStack.isEmpty()) return
        if (isOperator(inputStack.last())) return

        val numStack = Stack<BigDecimal>()

        try {
            for (item in suffixStack) {
                if (isOperator(item)) {
                    if (numStack.size > 1) {
                        val one = numStack.pop()
                        val two = numStack.pop()
                        val tmpResult = calc(one, two, item)
                        numStack.push(tmpResult)
                    }
                } else {
                    val num = item.toBigDecimal()
                    numStack.push(num)
                }
            }

            val result = numStack.pop()
            debugLog("result = $result")

            resultText.value = result.toString()
        } catch (e: Throwable) {
            if (e is ArithmeticException && e.message == "Division by zero") {
                resultText.value = "不能除以0"
            } else {
                SpiderMan.show(e)
            }
        }
    }

    //计算
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
                two.divide(one, 1, RoundingMode.HALF_UP)
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

    private fun equalBtnClick() {
        expText.value = resultText.value
        resultText.value = ""
        clearStack()
        addItem(expText.value)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            debugLog("ORIENTATION_PORTRAIT")
        } else {
            debugLog("ORIENTATION_LANDSCAPE")
        }
        orientation.value = newConfig.orientation
    }
}

