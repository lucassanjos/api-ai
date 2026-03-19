package com.qualityposto.api_ai.service

import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.nio.charset.StandardCharsets
import java.util.Base64

@Service
class EmailService(
    webClientBuilder: WebClient.Builder
) {
    private val webClient =
        webClientBuilder
            .baseUrl("http://localhost:8080/LOG_REQUISICAO")
            .build()

    fun enviar(pdfBytes: ByteArray, html: String) {

        val request = mapOf(
            "texto" to html,
            "pdfBytes" to Base64.getEncoder().encodeToString(pdfBytes)
        )

        val mediaTypeUtf8 = MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)

        webClient.post()
            .uri("/EMAIL_TESTE")
            .contentType(mediaTypeUtf8)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Void::class.java)
            .block()
    }
}