package algorithmic.engine

import java.lang.StringBuilder

class Algorithm constructor(val name: String, val type: Symbol.Type, val parameters: List<Symbol>) {
    val locals = arrayListOf<Symbol>()

    override fun toString(): String
    {
        // TODO: վերանայել այս ֆունկցիան
        val sb = StringBuilder()
        sb.append("ԱԼԳ ${type.text} $name(")
        parameters.forEach { sb.append("${it.type.text} ${it.name} ") }
        sb.append(")")
        return sb.toString()
    }
}