package algorithmic.main

import algorithmic.compiler.ByteCode
import algorithmic.parser.Parser
import algorithmic.parser.Scanner
import java.nio.file.Files
import java.nio.file.Paths

const val version = "0.0.1"

fun about()
{
    System.out.println("Ալգորիթմական լեզու։ Տարբերակ. $version։")
}

fun main(args: Array<String>)
{
    //val args = arrayOf("cases/ex10.alg")
    about()

    try {
        val input = Paths.get(args[0]).toAbsolutePath()
        if( Files.notExists(input) ) {
            System.err.println("«$input» ֆայլը գոյություն չունի։")
            return
        }

        val ast = Parser(Scanner(input)).parse()
        ByteCode(ast).compile(input.parent)
    }
    catch(ex: Exception) {
        System.err.println(ex)
    }
}

