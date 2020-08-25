package algorithmic.compiler

import algorithmic.engine.*
import java.lang.StringBuilder

class JavaScript(val program: Program) {
    fun compile(): String
    {
        val sb = StringBuilder()
        program.algorithms.forEach { sb.append(compile(it)) }
        return sb.toString()
    }

    private fun compile(alg: Algorithm): String
    {
        val sb = StringBuilder()
        val pars = alg.parameters.joinToString { it.id }
        sb.append("const ${alg.name} = function($pars) {\n")
        sb.append(alg.locals.joinToString("\n") { compile(it) })
        sb.append('\n')
        sb.append(compile(alg.body))
        sb.append("\n}\n")
        return sb.toString()
    }

    private fun compile(stat: Statement): String =
        when(stat) {
            is Sequence -> compile(stat)
            is Assignment -> compile(stat)
            is Result -> compile(stat)
            else -> throw CompileError("Չիրականացված թարգմանություն")
        }

    private fun compile(seq: Sequence): String
    {
        return seq.items.joinToString("\n") { compile(it) }
    }

    private fun compile(asg: Assignment): String
    {
        val se = compile(asg.value)
        return "${asg.sym.id} = $se"
    }

    private fun compile(res: Result): String
    {
        return "return " + compile(res.value)
    }

    private fun compile(expr: Expression): String =
        when( expr ) {
            is Binary -> compile(expr)
            is Unary -> compile(expr)
            is Apply -> compile(expr)
            is Text -> compile(expr)
            is Numeric -> compile(expr)
            is Variable -> compile(expr)
        }

    private fun compile(ex: Binary): String
    {
        val ls = compile(ex.left)
        val rs = compile(ex.right)
        return "($ls ${ex.operation.text} $rs)"
    }

    private fun compile(ex: Unary): String
    {
        val vs = compile(ex.subexpr)
        return "(${ex.operation.text} $vs)"
    }

    private fun compile(ex: Apply): String
    {
        val args = ex.arguments.joinToString { compile(it) }
        return "${ex.callee.name}($args)"
    }

    private fun compile(ex: Text): String =
        "'${ex.value}'"

    private fun compile(ex: Numeric): String =
        ex.value.toString()

    private fun compile(ex: Variable): String =
        ex.sym.id

    private fun compile(sym: Symbol): String =
        "let ${sym.id} = " + when(sym.type) {
            Symbol.Type.NUMBER -> "0.0"
            Symbol.Type.TEXT -> "''"
            Symbol.Type.VOID -> ""
        }
}
