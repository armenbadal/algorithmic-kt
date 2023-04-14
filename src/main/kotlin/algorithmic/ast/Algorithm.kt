package algorithmic.ast

class Algorithm(val name: String, val returnType: Type, val parameters: List<Symbol>, val body: Statement) {
    val locals = arrayListOf<Symbol>()
}