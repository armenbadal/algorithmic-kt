package algorithmic.ast

class Signature(val name: String, val resultType: Type, val parametersTypes: List<Type>) {
    fun isApplicable(args: List<Expression>): Boolean
    {
        if( parametersTypes.size != args.size )
            return false

        for( i in parametersTypes.indices )
            if( parametersTypes[i] != args[i].type )
                return false

        return true
    }

    override fun equals(other: Any?): Boolean
    {
        val sec = other as Signature

        if( resultType != sec.resultType )
            return false

        if( parametersTypes.size != sec.parametersTypes.size )
            return false

        for( i in parametersTypes.indices )
            if( parametersTypes[i] != sec.parametersTypes[i] )
                return false

        return true
    }

    override fun toString(): String =
        parametersTypes.joinToString(" × ") { it.toString() }
}


class Algorithm(val name: String, val returnType: Type, val parameters: List<Symbol>, val body: Statement) {
    val locals = arrayListOf<Symbol>()
}