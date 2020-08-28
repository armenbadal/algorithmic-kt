package algorithmic.main

import algorithmic.compiler.ByteCode
import algorithmic.compiler.JavaScript
import algorithmic.parser.Parser
import algorithmic.parser.Scanner

fun main()
{
    try {
        val scan = Scanner("C:\\Projects\\algorithmic-kt\\cases\\ex0.alg")
        val pars = Parser(scan)
        val prog = pars.parse()
        val comp = JavaScript(prog)
        println(comp.compile())
        val bc = ByteCode(prog)
        bc.compile()
    }
    catch(ex: Exception) {
        println(ex)
    }
}
