package com.example.git

import com.example.web_api.pipeline.git.Sample
import com.example.web_api.pipeline.git.SampleBuilder
import com.example.web_api.pipeline.git.SampleFile
import com.example.web_api.pipeline.git.SampleRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SampleBuilderTest {

    @Test
    fun testGetBlobs() {
        val result = SampleBuilder.buildSample(SampleRequest("2Pit", "test", "a/b/"))
        assertEquals(
            listOf(
                SampleFile("B1.txt", fileContent("B1")),
                SampleFile("B2.txt", fileContent("B2")),
                SampleFile("c/C1.txt", fileContent("C1")),
                SampleFile("c/C2.txt", fileContent("C2"))
            ),
            result.files
        )
    }

    @Test
    fun testGetBlobs_2() {
        val result = SampleBuilder.buildSample(SampleRequest("2Pit", "test", "/"))
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
            result.files
        )
    }

    @Test
    fun testGetBlobs_3() {
        val result = SampleBuilder.buildSample(SampleRequest("2Pit", "test", "a/"))
        assertEquals(
            listOf(
                SampleFile("A1.txt", fileContent("A1")),
                SampleFile("A2.txt", fileContent("A2")),
                SampleFile("b/B1.txt", fileContent("B1")),
                SampleFile("b/B2.txt", fileContent("B2")),
                SampleFile("b/c/C1.txt", fileContent("C1")),
                SampleFile("b/c/C2.txt", fileContent("C2"))
            ),
            result.files
        )
    }

    @Test
    fun testWrite() {
        val sampleRequest = SampleRequest("2Pit", "test", "a/")
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

        SampleBuilder.write(sampleRequest, sample)
    }

    private fun fileContent(name: String): String {
        return "Hello from $name file!\n2\n3\n4\n"
    }
}