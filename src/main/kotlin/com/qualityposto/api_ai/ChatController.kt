package com.qualityposto.api_ai

import org.springframework.ai.chat.client.ChatClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController (
    chatClientBuilder: ChatClient.Builder
) {
    private val chatClient = chatClientBuilder.build()

    @GetMapping("/ai")
    fun generation(userInput: String): String? {
        return chatClient.prompt()
            .user(userInput)
            .call()
            .content()
    }
}