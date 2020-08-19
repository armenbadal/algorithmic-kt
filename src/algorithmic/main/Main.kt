package algorithmic.main

import algorithmic.parser.Parser
import algorithmic.parser.Scanner

fun main()
{
    val scan = Scanner("C:\\Projects\\algorithmic-kt\\cases\\ex0.alg")
    val pars = Parser(scan)
    pars.parse()
}