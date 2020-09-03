package algorithmic.compiler

import algorithmic.engine.*
import java.io.File
import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Path

class JavaScript(val program: Program) {

    fun compile(output: Path)
    {
        val sb = StringBuilder()
        program.algorithms.forEach { sb.append(compile(it)) }

        val by = compile(program.body)
        sb.append("(function(){$by})()\n")

        Files.newBufferedWriter(output).use { wr -> wr.write(sb.toString()) }
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
            is Branching -> compile(stat)
            is Repetition -> compile(stat)
            is Result -> compile(stat)
            is Call -> compile(stat)
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

    private fun compile(bra: Branching): String
    {
        val sb = StringBuilder()
        val cj = compile(bra.condition)
        sb.append("if($cj)")
        val dj = compile(bra.decision)
        sb.append("\n{$dj}\n")
        val aj = compile(bra.alternative)
        sb.append("else\n{$aj}")
        return sb.toString()
    }

    private fun compile(rep: Repetition): String
    {
        val cj = compile(rep.condition)
        val bj = compile(rep.body)
        return "while($cj)\n{$bj}"
    }

    private fun compile(res: Result): String
    {
        return "return " + compile(res.value)
    }

    private fun compile(cl: Call): String
    {
        val args = cl.apply.arguments.joinToString { compile(it) }
        return "${cl.apply.callee.name}($args)"
    }

    private fun compile(expr: Expression): String =
        when( expr ) {
            is Binary -> compile(expr)
            is Unary -> compile(expr)
            is Apply -> compile(expr)
            is Text -> compile(expr)
            is Numeric -> compile(expr)
            is Variable -> compile(expr)
            is Logical -> compile(expr)
        }

    private fun compile(ex: Binary): String
    {
        val ls = compile(ex.left)
        val rs = compile(ex.right)
        val op = when(ex.operation.text) {
            "=" -> "=="
            "<>" -> "!="
            else -> ex.operation.text
        }
        return "($ls $op $rs)"
    }

    private fun compile(ex: Unary): String
    {
        val vs = compile(ex.right)
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

    private fun compile(ex: Logical): String =
        if( ex.value == "ՃԻՇՏ" ) "true" else "false"

    private fun compile(sym: Symbol): String =
        "let ${sym.id} = " + when(sym.type) {
            Type.BOOL -> "false"
            Type.REAL -> "0.0"
            Type.TEXT -> "''"
            Type.VOID -> ""
        }
}
