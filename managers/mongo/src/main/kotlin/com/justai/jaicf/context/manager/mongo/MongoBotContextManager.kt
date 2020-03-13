package com.justai.jaicf.context.manager.mongo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.justai.jaicf.context.BotContext
import com.justai.jaicf.context.manager.BotContextManager
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.Document

class MongoBotContextManager(
    private val collection: MongoCollection<Document>
): BotContextManager {

    private val mapper = jacksonObjectMapper().enableDefaultTyping()

    override fun loadContext(clientId: String): BotContext {
        return collection
            .find(Filters.eq("_id", clientId))
            .iterator().tryNext()?.let { doc ->
                val model = mapper.readValue(doc.toJson(), BotContextModel::class.java)

                BotContext(model._id, model.dialogContext).apply {
                    result = model.result
                    client.putAll(model.client)
                    session.putAll(model.session)
                }

        } ?: BotContext(clientId)
    }

    override fun saveContext(botContext: BotContext) {
        BotContextModel(
            _id = botContext.clientId,
            result = botContext.result,
            client = botContext.client.toMap(),
            session = botContext.session.toMap(),
            dialogContext = botContext.dialogContext
        ).apply {
            val doc = Document.parse(mapper.writeValueAsString(this))
            collection.replaceOne(Filters.eq("_id", _id), doc, UpdateOptions().upsert(true))
        }
    }
}