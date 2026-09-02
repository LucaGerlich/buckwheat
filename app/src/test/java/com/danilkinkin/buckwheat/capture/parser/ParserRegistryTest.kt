package com.danilkinkin.buckwheat.capture.parser

import com.danilkinkin.buckwheat.capture.FakeParser
import com.danilkinkin.buckwheat.capture.registryOf
import com.danilkinkin.buckwheat.capture.snapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ParserRegistryTest {

    @Test
    fun `returns null when no parser is registered`() {
        assertNull(registryOf().parse(snapshot()))
    }

    @Test
    fun `skips parsers that do not support the notification`() {
        val unrelated = FakeParser(supportedPackage = "com.other.bank")

        assertNull(registryOf(unrelated).parse(snapshot()))
        assertEquals(0, unrelated.parseCalls)
    }

    @Test
    fun `uses the supporting parser`() {
        val unrelated = FakeParser(supportedPackage = "com.other.bank")
        val matching = FakeParser(supportedPackage = "de.traderepublic.app")

        val result = registryOf(unrelated, matching).parse(snapshot())

        assertNotNull(result)
        assertEquals(1, matching.parseCalls)
        assertEquals(0, unrelated.parseCalls)
    }

    @Test
    fun `propagates a parser that cannot extract a candidate`() {
        val matching = FakeParser(supportedPackage = "de.traderepublic.app", result = null)

        assertNull(registryOf(matching).parse(snapshot()))
        assertEquals(1, matching.parseCalls)
    }
}
