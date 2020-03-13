package com.justai.jaicf.context.manager.test

import com.justai.jaicf.context.BotContext
import com.justai.jaicf.context.manager.mongo.MongoBotContextManager
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class MongoBotContextManagerIT {

    private val uri = MongoClientURI("mongodb://test:testtest1@ds125385.mlab.com:25385/jaicf")

    private val client = MongoClient(uri)
    private val manager = MongoBotContextManager(client.getDatabase(uri.database!!).getCollection("contexts"))

    @Test
    fun testSave() {
        val context = BotContext("client1").apply {
            result = "some result"
            session["key1"] = "some value"
        }

        manager.saveContext(context)

        val result = manager.loadContext("client1")

        assertNotNull(result)
        assertEquals(context.result, result.result)
        assertEquals(context.session, result.session)
    }

    @Test
    fun testSaveCustomBean() {
        val context = BotContext("client2").apply {
            result = CustomValue(1)
            session["value"] = CustomValue(CustomValue(2))
        }

        manager.saveContext(context)

        val result = manager.loadContext("client2")

        assertNotNull(result)
        assertTrue(result.result is CustomValue)
        assertTrue(result.session["value"] is CustomValue)
    }
}