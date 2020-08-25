package algorithmic.engine

class Signature(val name: String, val resultType: Symbol.Type, val parametersTypes: List<Symbol.Type>) {
    fun isApplicable(args: List<Expression>): Boolean
    {
        if( parametersTypes.size != args.size)
            return false

        for( i in 0..parametersTypes.size ) {
            if( parametersTypes[i] != args[i].type() )
                return false
        }
        return true
    }

    fun isApplicable(ret: Symbol, args: List<Expression>): Boolean
    {
        return resultType == ret.type && isApplicable(args)
    }

    override fun equals(other: Any?): Boolean
    {
        val sec = other as Signature

        if( resultType != sec.resultType )
            return false

        if( parametersTypes.size != sec.parametersTypes.size )
            return false

        for( i in 0..parametersTypes.size )
            if( parametersTypes[i] != sec.parametersTypes[i] )
                return false

        return true
    }
}


class Algorithm(val name: String, val type: Symbol.Type, val parameters: List<Symbol>, val body: Statement) {
    var locals = arrayListOf<Symbol>()

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