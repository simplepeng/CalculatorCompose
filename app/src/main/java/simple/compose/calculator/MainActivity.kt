package simple.compose.calculator

import android.os.Bundle
import android.util.Log
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
import java.util.Stack

class MainActivity : ComponentActivity() {

    private val inputStack = Stack<Item>()
    private val infixStack = Stack<Item>()
    private val suffixStack = Stack<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkModel by remember { mutableStateOf(false) }

            CalculatorComposeTheme(darkTheme = isDarkModel) {
                CalculatorUI()
            }
        }
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
                ButtonUI(Modifier.weight(1f), text = "theme", textColor = optBtnColor) {

                }
                ButtonUI(Modifier.weight(1f), text = "AC", textColor = optBtnColor) {
                    acClear()
                }
                ButtonUI(Modifier.weight(1f), text = "Del", textColor = optBtnColor) {
                    delItem()
                }
                ButtonUI(Modifier.weight(1f), text = "/", textColor = optBtnColor) {
                    addItem(Item("/", Item.TYPE_SYMBOL))
                }
            }
            Row() {
                ButtonUI(Modifier.weight(1f), text = "7", textColor = numBtnColor) {
                    addItem(Item("7", Item.TYPE_NUM))
                }
                ButtonUI(Modifier.weight(1f), text = "8", textColor = numBtnColor) {
                    addItem(Item("8", Item.TYPE_NUM))
                }
                ButtonUI(Modifier.weight(1f), text = "9", textColor = numBtnColor) {
                    addItem(Item("9", Item.TYPE_NUM))
                }
                ButtonUI(Modifier.weight(1f), text = "x", textColor = optBtnColor) {
                    addItem(Item("*", Item.TYPE_SYMBOL))
                }
            }
            Row() {
                ButtonUI(Modifier.weight(1f), text = "4", textColor = numBtnColor) {
                    addItem(Item("4", Item.TYPE_NUM))
                }
                ButtonUI(Modifier.weight(1f), text = "5", textColor = numBtnColor) {
                    addItem(Item("5", Item.TYPE_NUM))
                }
                ButtonUI(Modifier.weight(1f), text = "6", textColor = numBtnColor) {
                    addItem(Item("6", Item.TYPE_NUM))
                }
                ButtonUI(Modifier.weight(1f), text = "-", textColor = optBtnColor) {
                    addItem(Item("-", Item.TYPE_SYMBOL))
                }
            }
            Row() {
                ButtonUI(Modifier.weight(1f), text = "1", textColor = numBtnColor) {
                    addItem(Item("1", Item.TYPE_NUM))
                }
                ButtonUI(Modifier.weight(1f), text = "2", textColor = numBtnColor) {
                    addItem(Item("2", Item.TYPE_NUM))
                }
                ButtonUI(Modifier.weight(1f), text = "3", textColor = numBtnColor) {
                    addItem(Item("3", Item.TYPE_NUM))
                }
                ButtonUI(Modifier.weight(1f), text = "+", textColor = optBtnColor) {
                    addItem(Item("+", Item.TYPE_SYMBOL))
                }
            }
            Row() {
                ButtonUI(Modifier.weight(1f), text = "", textColor = optBtnColor)
                ButtonUI(Modifier.weight(1f), text = "0", textColor = numBtnColor) {
                    addItem(Item("0", Item.TYPE_NUM))
                }
                ButtonUI(Modifier.weight(1f), text = ".", textColor = numBtnColor) {
                    addItem(Item(".", Item.TYPE_NUM))
                }
                ButtonUI(Modifier.weight(1f), text = "=", textColor = optBtnColor) {
                    calc()
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

    private fun addItem(item: Item) {
        inputStack.add(item)
        convertToInfix()
    }

    private fun delItem() {
        inputStack.pop()
    }

    private fun acClear() {
        inputStack.clear()
    }

    private fun calc() {

    }

    private fun convertToInfix() {
        infixStack.clear()
        val strBuilder = StringBuilder()

        inputStack.forEach { item ->
            if (item.type == Item.TYPE_SYMBOL) {
                val num = strBuilder.toString()
                infixStack.add(Item(num, Item.TYPE_NUM))
                strBuilder.clear()
                infixStack.add(Item(item.operator, Item.TYPE_SYMBOL))
            } else {
                strBuilder.append(item.operator)
            }
        }

        printStack(infixStack)

        convertToSuffix()
    }

    private fun printStack(stack: Stack<Item>) {
        val strBuilder = StringBuilder()
        stack.forEach { strBuilder.append(it.operator) }
        logTAG("stack = $strBuilder")
    }

    private fun logTAG(msg: String) {
        Log.d("SimplePeng", msg)
    }

    private fun convertToSuffix(){
        suffixStack.clear()
        val tmpStack = Stack<Item>()

        infixStack.forEach { item ->
            if (item.type == Item.TYPE_SYMBOL){
                tmpStack.add(item)
            }else{
                suffixStack.add(item)
            }
        }
    }
}

