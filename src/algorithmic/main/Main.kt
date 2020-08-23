package algorithmic.main

import algorithmic.compiler.Compiler
import algorithmic.parser.Parser
import algorithmic.parser.Scanner

fun main()
{
    try {
        val scan = Scanner("C:\\Projects\\algorithmic-kt\\cases\\ex2.alg")
        val pars = Parser(scan)
        val prog = pars.parse()
        println(prog.name)
        val comp = Compiler(prog)
    }
    catch(ex: Exception) {
        println(ex)
    }
}