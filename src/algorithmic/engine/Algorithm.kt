package algorithmic.engine

import java.lang.StringBuilder

class Signature(val name: Symbol, val parameters: ArrayList<Symbol>) {
    fun isApplicable(args: ArrayList<Expression>): Boolean
    {
        if( parameters.size != args.size)
            return false

        for( i in 0..parameters.size ) {
            if( parameters[i].type != args[i].type() )
                return false
        }
        return true
    }

    fun isApplicable(ret: Symbol, args: ArrayList<Expression>): Boolean
    {
        return name.type == ret.type && isApplicable(args)
    }
}

class Algorithm(val signature: Signature, val body: Statement) {

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