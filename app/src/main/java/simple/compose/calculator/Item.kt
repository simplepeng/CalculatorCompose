package simple.compose.calculator

data class Item(
    val operator: String,
    val type:Int
){
    companion object{
        const val TYPE_NUM =1
        const val TYPE_SYMBOL = 2
    }
}
