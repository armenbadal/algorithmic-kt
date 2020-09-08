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
    fun `failing case`()
    {
        val p = Paths.get("./abc.txt").toAbsolutePath()

        Assertions.assertTrue(false)
    }

    companion object {
        @BeforeAll
        @JvmStatic
        internal fun createDataFile()
        {
            //Files.newBufferedWriter(output).use {
            //    wr -> wr.write(sb.toString())
            //}
        }

        @AfterAll
        @JvmStatic
        internal fun removeDataFile()
        {

        }
    }
}