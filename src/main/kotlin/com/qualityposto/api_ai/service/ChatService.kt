package com.qualityposto.api_ai.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class ChatService(
    chatClientBuilder: ChatClient.Builder,
    chatMemory: ChatMemory,
    webClientBuilder: WebClient.Builder,
    @Value("\${openai.api.key}")
    private val apiKey: String,
    private val pdfService: PdfService,
    private val promptService: PromptService,
    private val emailService: EmailService
){
    private val chatClient =
        chatClientBuilder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build()

    val webClient = webClientBuilder.baseUrl("http://localhost:8080/LOG_REQUISICAO/").build()

    fun pdfAnalisar(pdfBytes: ByteArray, idChat: String): String {
        val paginas = pdfService.extrairPagina(pdfBytes)

        val memory = paginas.mapIndexed { index, pageText ->
            val response = chatClient.prompt()
                .user(promptService.promptResumoPagina(pageText))
                .advisors { it.param(ChatMemory.CONVERSATION_ID, idChat) }
                .call()
                .content()

            "Página ${index + 1}:\n$response"
        }

        val promptFinal = promptService.promptFinal(memory)

        return chatClient.prompt()
            .user(promptFinal)
            .advisors { it.param(ChatMemory.CONVERSATION_ID, idChat) }
            .call()
            .content() ?: error("Falha ao gerar análise")
    }

    @Async("analiseExecutor")
    fun analisarAsync(pdfBytes: ByteArray, idChat: String) {
        val retornoAnaliseHtml = pdfAnalisar(pdfBytes, idChat)
        emailService.enviar(pdfBytes, retornoAnaliseHtml)
    }
}