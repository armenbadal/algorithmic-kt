package algorithmic.main

import algorithmic.compiler.ByteCode
import algorithmic.compiler.JavaScript
import algorithmic.parser.Parser
import algorithmic.parser.Scanner
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

const val version = "0.0.1"

fun about()
{
    System.out.println("Ալգորիթմական լեզու։ Տարբերակ. $version։")
}

fun main(args: Array<String>)
{
    val argv = arrayOf("c:\\projects\\algorithmic-kt\\cases\\ex0.alg")
    about()

    try {
        val input = Paths.get(argv[0])
        if( Files.notExists(input) ) {
            System.err.println("«$input» ֆայլը գոյություն չունի։")
            return
        }

        val scan = Scanner(input)
        val ast = Parser(scan).parse()
        JavaScript(ast).compile(input.parent)
        ByteCode(ast).compile(input.parent)
    }
    catch(ex: Exception) {
        println(ex)
    }
}

