package algorithmic.main

import algorithmic.parser.Parser
import algorithmic.parser.Scanner

fun main()
{
    try {
        val scan = Scanner("C:\\Projects\\algorithmic-kt\\cases\\ex0.alg")
        val pars = Parser(scan)
        pars.parse()
    }
    catch(ex: Exception) {
        println(ex)
    }
}