package simple.compose.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import simple.compose.calculator.ui.theme.CalculatorComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorComposeTheme(darkTheme = false) {
                CalculatorUI()
            }
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
            ButtonUI(Modifier.weight(1f), text = "", textColor = optBtnColor)
            ButtonUI(Modifier.weight(1f), text = "AC", textColor = optBtnColor)
            ButtonUI(Modifier.weight(1f), text = "Del", textColor = optBtnColor)
            ButtonUI(Modifier.weight(1f), text = "/", textColor = optBtnColor)
        }
        Row() {
            ButtonUI(Modifier.weight(1f), text = "7", textColor = numBtnColor)
            ButtonUI(Modifier.weight(1f), text = "8", textColor = numBtnColor)
            ButtonUI(Modifier.weight(1f), text = "9", textColor = numBtnColor)
            ButtonUI(Modifier.weight(1f), text = "x", textColor = optBtnColor)
        }
        Row() {
            ButtonUI(Modifier.weight(1f), text = "4", textColor = numBtnColor)
            ButtonUI(Modifier.weight(1f), text = "5", textColor = numBtnColor)
            ButtonUI(Modifier.weight(1f), text = "6", textColor = numBtnColor)
            ButtonUI(Modifier.weight(1f), text = "-", textColor = optBtnColor)
        }
        Row() {
            ButtonUI(Modifier.weight(1f), text = "1", textColor = numBtnColor)
            ButtonUI(Modifier.weight(1f), text = "2", textColor = numBtnColor)
            ButtonUI(Modifier.weight(1f), text = "3", textColor = numBtnColor)
            ButtonUI(Modifier.weight(1f), text = "+", textColor = optBtnColor)
        }
        Row() {
            ButtonUI(Modifier.weight(1f), text = "", textColor = optBtnColor)
            ButtonUI(Modifier.weight(1f), text = "0", textColor = numBtnColor)
            ButtonUI(Modifier.weight(1f), text = ".", textColor = numBtnColor)
            ButtonUI(Modifier.weight(1f), text = "=", textColor = optBtnColor)
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
