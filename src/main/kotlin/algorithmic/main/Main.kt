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
        val output = input.resolveSibling(input)

        val scan = Scanner(input)
        val pars = Parser(scan)
        val ast = pars.parse()
        JavaScript(ast).compile(output)
        //ByteCode(ast).compile()
    }
    catch(ex: Exception) {
        println(ex)
    }
}

fun Path.changeExtension(ext: String)
{
}
