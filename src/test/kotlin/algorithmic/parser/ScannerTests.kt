package algorithmic.parser

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.AccessDeniedException
import java.nio.file.Files
import java.nio.file.Paths

class ScannerTests {
    @Test
    fun `scanner with empty path`()
    {
        Assertions.assertThrows(AccessDeniedException::class.java) {
            Scanner(Paths.get(""))
        }
    }

    @Test
    fun `read all tokens`()
    {
        val allTokens = arrayListOf<Token>()
        val lexemeAndValues = mapOf<String,Token>(
                "3.1415" to Token.ԹՎԱՅԻՆ,
                "777" to Token.ԹՎԱՅԻՆ,
                "«Լինե՞լ, թե՞ չլինել. այս է խնդիրը»" to Token.ՏԵՔՍՏԱՅԻՆ,
                "\"Nel mezzo del cammin di nostra vita\"" to Token.ՏԵՔՍՏԱՅԻՆ,
                "ՃԻՇՏ" to Token.ՃԻՇՏ,
                "ԿԵՂԾ" to Token.ԿԵՂԾ,
                "անուն" to Token.ԱՆՈՒՆ,
                "անո՞ւն" to Token.ԱՆՈՒՆ,
                "անո՜ւն" to Token.ԱՆՈՒՆ,
                "անո՛ւն" to Token.ԱՆՈՒՆ,
                "identifier" to Token.ԱՆՈՒՆ,
                "ճ0" to Token.ԱՆՈՒՆ,
                "x7y" to Token.ԱՆՈՒՆ,
                "ԾՐԱԳԻՐ" to Token.ԾՐԱԳԻՐ,
                "ԳՐԱԴԱՐԱՆ" to Token.ԳՐԱԴԱՐԱՆ,
                "ԱԼԳՈՐԻԹՄ" to Token.ԱԼԳՈՐԻԹՄ,
                "ՍՏՈՐԵՎ" to Token.ՍՏՈՐԵՎ,
                "ՍԿԻԶԲ" to Token.ՍԿԻԶԲ,
                "ՎԵՐՋ" to Token.ՎԵՐՋ,
                "ԻՐԱԿԱՆ" to Token.ԻՐԱԿԱՆ,
                "ՏԵՔՍՏ" to Token.ՏԵՔՍՏ,
                "ԲՈՒԼՅԱՆ" to Token.ԲՈՒԼՅԱՆ,
                "ԱՂՅՈՒՍԱԿ" to Token.ԱՂՅՈՒՍԱԿ,
                "ԵԹԵ" to Token.ԵԹԵ,
                "ԱՊԱ" to Token.ԱՊԱ,
                "ԻՍԿ" to Token.ԻՍԿ,
                "ԱՅԼԱՊԵՍ" to Token.ԱՅԼԱՊԵՍ,
                "ԱՎԱՐՏ" to Token.ԱՎԱՐՏ,
                "ՔԱՆԻ" to Token.ՔԱՆԻ,
                "ԴԵՌ" to Token.ԴԵՌ,
                "ԱՐԴՅՈՒՆՔ" to Token.ԱՐԴՅՈՒՆՔ,
                "ՍՏՈՐԵՒ" to Token.ՍՏՈՐԵՎ,
                "ԵԹԷ" to Token.ԵԹԵ,
                "ԱՅԼԱՊԷՍ" to Token.ԱՅԼԱՊԵՍ,
                "ԱՒԱՐՏ" to Token.ԱՎԱՐՏ,
                "ԱՐԴԻՒՆՔ" to Token.ԱՐԴՅՈՒՆՔ,
                "ԵՒ" to Token.ԵՎ,
                "ԲՈՒԼԵԱՆ" to Token.ԲՈՒԼՅԱՆ,
                "ԱՂԻՒՍԱԿ" to Token.ԱՂՅՈՒՍԱԿ,
                "+" to Token.ADD,
                "-" to Token.SUB,
                "*" to Token.MUL,
                "/" to Token.DIV,
                "=" to Token.EQ,
                "<>" to Token.NE,
                ">" to Token.GT,
                ">=" to Token.GE,
                "<" to Token.LT,
                "<=" to Token.LE,
                "ԵՎ" to Token.ԵՎ,
                "ԿԱՄ" to Token.ԿԱՄ,
                "ՈՉ" to Token.ՈՉ,
                ":=" to Token.ՎԵՐԱԳՐԵԼ,
                ":" to Token.ՎԵՐՋԱԿԵՏ,
                "։" to Token.ՎԵՐՋԱԿԵՏ,
                "," to Token.ՍՏՈՐԱԿԵՏ,
                ";" to Token.ԿԵՏ_ՍՏՈՐԱԿԵՏ,
                "(" to Token.ՁԱԽ_ՓԱԿԱԳԻԾ,
                ")" to Token.ԱՋ_ՓԱԿԱԳԻԾ,
                "?" to Token.ԱՆԾԱՆՈԹ,
                "￿" to Token.ԱՆԾԱՆՈԹ
            )

        val ph = Paths.get("src", "test", "resources", "scanner-data.txt")
        Files.newBufferedWriter(ph).use {
            wr -> wr.write("{ Մեկնաբանություն }")
                wr.newLine()
                lexemeAndValues.forEach {
                wr.write(it.key)
                wr.newLine()
                allTokens.add(it.value)
            }
        }

        val scan = Scanner(ph)
        var el = scan.next()
        for( i in 0 until lexemeAndValues.size ) {
            Assertions.assertEquals(el.token, allTokens[i])
            el = scan.next()
        }

        Files.delete(ph)
    }

    companion object {
        @BeforeAll
        @JvmStatic
        internal fun createDataFile()
        {}

        @AfterAll
        @JvmStatic
        internal fun removeDataFile()
        {}
    }
}