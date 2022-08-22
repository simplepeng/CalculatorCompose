package simple.compose.calculator

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simple.spiderman.SpiderMan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import simple.compose.calculator.ui.theme.CalculatorComposeTheme
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

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

    private val resultItems = mutableStateListOf<String>()

    private val isDarkModel = mutableStateOf(false)

    private val expText = mutableStateOf("")
    private val resultText = mutableStateOf("")

    private val orientation by lazy { mutableStateOf(resources.configuration.orientation) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        testCalc()

        setContent {
            CalculatorComposeTheme(darkTheme = isDarkModel.value) {
                val listState = rememberLazyListState()

                if (orientation.value == Configuration.ORIENTATION_PORTRAIT) {
                    PortraitUI(listState)
                } else {
                    LandscapeUI(listState)
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
    fun PortraitUI(listState: LazyListState) {
        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
            DisplayUI(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                listState,
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
                listState,
            )
        }
    }

    @Composable
    fun LandscapeUI(listState: LazyListState) {
        Row(Modifier.background(MaterialTheme.colorScheme.background)) {
            DisplayUI(
                Modifier
                    .fillMaxHeight()
                    .weight(1f),
                listState
            )
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(0.5f.dp)
                    .padding(horizontal = 10f.dp)
            )
            ButtonGroup(
                Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                listState
            )
        }
    }

    @Composable
    fun DisplayUI(
        modifier: Modifier,
        listState: LazyListState
    ) {
        val textModifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
        val textColor = MaterialTheme.colorScheme.tertiary

        Column(modifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = listState,
                verticalArrangement = Arrangement.Bottom
            ) {
                items(items = resultItems) { item ->
                    Text(
                        text = item,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        color = Color.LightGray,
                        fontSize = 20.sp,
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
        modifier: Modifier,
        listState: LazyListState
    ) {
        val coroutineScope = rememberCoroutineScope()

        val optBtnColor = MaterialTheme.colorScheme.secondary
        val numBtnColor = MaterialTheme.colorScheme.tertiary

        Column(modifier) {
            val rowModifier = if (orientation.value == Configuration.ORIENTATION_LANDSCAPE) {
                Modifier.weight(1f)
            } else {
                Modifier.fillMaxWidth()
            }

            Row(rowModifier) {
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
            Row(rowModifier) {
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
            Row(rowModifier) {
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
            Row(rowModifier) {
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
            Row(rowModifier) {
                ButtonUI(Modifier.weight(1f), text = "", textColor = optBtnColor)
                ButtonUI(Modifier.weight(1f), text = "0", textColor = numBtnColor) {
                    addItem("0")
                }
                ButtonUI(Modifier.weight(1f), text = ".", textColor = numBtnColor) {
                    addItem(DOT)
                }
                ButtonUI(Modifier.weight(1f), text = "=", textColor = optBtnColor) {
                    equalBtnClick(listState, coroutineScope)
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

        //如果是小数点 and (输入栈为空 or 上一个输入是计算符 or 上一个输入也是小数点)
        if (item == DOT && (inputStack.isEmpty() || isOperator(inputStack.last()) || inputStack.last() == DOT)) {
            return
        }

        if (isOperator(item) && inputStack.isEmpty()) {//输入的是计算符 and 输入栈为空
            return
        }
        if (isOperator(item) && isOperator(inputStack.last())) {//输入的是计算符 and 上一个输入的也是计算符
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
        resultItems.clear()
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
                if (item.isEmpty()) continue
                if (isOperator(item)) {
                    if (numStack.size > 1) {
                        val one = numStack.pop()
                        val two = numStack.pop()

                        if (item == DIV && one == BigDecimal.ZERO) {
                            throw DivZeroException()
                        }

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
            if (e is DivZeroException) {
                resultText.value = "不能除以0"
            } else if (e is ArithmeticException && (e.message == "Division by zero" || e.message == "divide by zero")) {
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

    private fun equalBtnClick(listState: LazyListState, coroutineScope: CoroutineScope) {
        val resultExp = String.format("%s=%s", expText.value, resultText.value)
        resultItems.add(resultExp)

        expText.value = resultText.value
        resultText.value = ""
        clearStack()
        addItem(expText.value)

        coroutineScope.launch {
            listState.animateScrollToItem(resultItems.lastIndex)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientation.value = newConfig.orientation
    }
}

