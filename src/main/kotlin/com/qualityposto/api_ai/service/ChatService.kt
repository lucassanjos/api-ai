package com.qualityposto.api_ai.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.stereotype.Service

@Service
class ChatService(
    chatClientBuilder: ChatClient.Builder,
    chatMemory: ChatMemory
){
    private val chatClient =
        chatClientBuilder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build()

    fun chatOpenAiMemoryAnaliseExcel(
        instrucao: String,
        arquivoExcel: StringBuilder
    ): String? {
        return chatClient.prompt()
            .user { u ->
                u.text("""
                Instrução do Usuário: ${instrucao}

                Dados do Excel:
                ${arquivoExcel}
            """.trimIndent())
            }
            .call()
            .content()
    }

    fun chatOpenAiMemory(
        userInput: String,
        chatId: String
    ): String? {
        return chatClient.prompt()
            .user(userInput)
            .advisors { it.param(ChatMemory.CONVERSATION_ID, chatId) }
            .call()
            .content()
    }
}