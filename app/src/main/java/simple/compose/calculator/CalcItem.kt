package simple.compose.calculator

data class CalcItem(
    var text: String,
    val type: Int,
) {
    companion object {
        const val TYPE_EXP = 1
        const val TYPE_RESULT = 2
    }
}