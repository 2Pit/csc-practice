import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SampleBuilderTest {

    @Test
    fun testGetBlobs() {
        val sample = SampleBuilder.buildSample(SampleRequest("2Pit", "test", "a/b/"))
        assertEquals(
            sample.files,
            listOf(
                SampleFile("B1.txt", fileContent("B1")),
                SampleFile("B2.txt", fileContent("B2")),
                SampleFile("c/C1.txt", fileContent("C1")),
                SampleFile("c/C2.txt", fileContent("C2"))
            )
        )
    }

    @Test
    fun testGetBlobs_2() {
        val sample = SampleBuilder.buildSample(SampleRequest("2Pit", "test", ""))
        assertEquals(
            sample.files,
            listOf(
                SampleFile("a/A1.txt", fileContent("A1")),
                SampleFile("a/A2.txt", fileContent("A2")),
                SampleFile("a/b/B1.txt", fileContent("B1")),
                SampleFile("a/b/B2.txt", fileContent("B2")),
                SampleFile("a/b/c/C1.txt", fileContent("C1")),
                SampleFile("a/b/c/C2.txt", fileContent("C2"))
            )
        )
    }

    @Test
    fun testGetBlobs_3() {
        val sample = SampleBuilder.buildSample(SampleRequest("2Pit", "test", "a/"))
        assertEquals(
            sample.files,
            listOf(
                SampleFile("b/B1.txt", fileContent("B1")),
                SampleFile("b/B2.txt", fileContent("B2")),
                SampleFile("b/c/C1.txt", fileContent("C1")),
                SampleFile("b/c/C2.txt", fileContent("C2"))
            )
        )
    }

    private fun fileContent(name: String): String {
        return "Hello from $name file!\n2\n3\n4\n"
    }
}