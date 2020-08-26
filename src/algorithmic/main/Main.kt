package algorithmic.main

import algorithmic.compiler.JavaScript
import algorithmic.parser.Parser
import algorithmic.parser.Scanner

fun main()
{
    try {
        val scan = Scanner("C:\\Projects\\algorithmic-kt\\cases\\ex2.alg")
        val pars = Parser(scan)
        val prog = pars.parse()
        val comp = JavaScript(prog)
        println(comp.compile())
    }
    catch(ex: Exception) {
        println(ex)
    }
}