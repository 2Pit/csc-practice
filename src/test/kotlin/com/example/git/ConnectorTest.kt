package com.example.git

import com.example.app.api.Location
import com.example.app.git.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ConnectorTest {

    @Test
    fun testGetBlobs() {
        val files = Connector.downloadProjectFiles(
            Location(
                "2Pit",
                "test",
                "a/b/"
            )
        )
        assertEquals(
            listOf(
                SampleFile("B1.txt", fileContent("B1")),
                SampleFile("B2.txt", fileContent("B2")),
                SampleFile("c/C1.txt", fileContent("C1")),
                SampleFile("c/C2.txt", fileContent("C2"))
            ),
            files
        )
    }

    @Test
    fun testGetBlobs_2() {
        val files = Connector.downloadProjectFiles(
            Location(
                "2Pit",
                "test",
                "/"
            )
        )
        assertEquals(
            listOf(
                SampleFile("0.txt", fileContent("0")),
                SampleFile("a/A1.txt", fileContent("A1")),
                SampleFile("a/A2.txt", fileContent("A2")),
                SampleFile("a/b/B1.txt", fileContent("B1")),
                SampleFile("a/b/B2.txt", fileContent("B2")),
                SampleFile("a/b/c/C1.txt", fileContent("C1")),
                SampleFile("a/b/c/C2.txt", fileContent("C2"))
            ),
            files
        )
    }

    @Test
    fun testGetBlobs_3() {
        val files = Connector.downloadProjectFiles(
            Location(
                "2Pit",
                "test",
                "a/"
            )
        )
        assertEquals(
            listOf(
                SampleFile("A1.txt", fileContent("A1")),
                SampleFile("A2.txt", fileContent("A2")),
                SampleFile("b/B1.txt", fileContent("B1")),
                SampleFile("b/B2.txt", fileContent("B2")),
                SampleFile("b/c/C1.txt", fileContent("C1")),
                SampleFile("b/c/C2.txt", fileContent("C2"))
            ),
            files
        )
    }

    @Test
    fun testWrite() {
        val location = Location("2Pit", "test", "a/")
        val sample = Sample(
            listOf(
                SampleFile("A1.txt", fileContent("A1")),
                SampleFile("A2.txt", fileContent("A2")),
                SampleFile("b/B1.txt", fileContent("B1")),
                SampleFile("b/B2.txt", fileContent("B2")),
                SampleFile("b/c/C1.txt", fileContent("C1")),
                SampleFile("b/c/C2.txt", fileContent("C2"))
            )
        )

        write("/home/peter.bogdanov/IdeaProjects/csc-practice/out", location, sample)
    }

    private fun fileContent(name: String): String {
        return "Hello from $name file!\n2\n3\n4\n"
    }
}