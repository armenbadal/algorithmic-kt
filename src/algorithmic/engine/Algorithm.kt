package algorithmic.engine

import java.lang.StringBuilder

class Algorithm(val signature: Signature, val body: Statement) {
    data class Signature(val name: Symbol, val parameters: ArrayList<Symbol>)

    override fun toString(): String
    {
        // TODO: վերանայել այս ֆունկցիան
//        val sb = StringBuilder()
//        sb.append("ԱԼԳ ${rtype.text} $name( ")
//        parameters.forEach { sb.append("${it.type.text} ${it.name} ") }
//        sb.append(")\n")
//        sb.append("ՍԿԻԶԲ\n")
//        for(s in body) {
//            sb.append("  ")
//            sb.append(s)
//            sb.append("\n")
//        }
//        sb.append("ՎԵՐՋ")
//        return sb.toString()
        return ""
    }
}