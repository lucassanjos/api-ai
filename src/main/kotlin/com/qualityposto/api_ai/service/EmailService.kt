package com.qualityposto.api_ai.service

import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Service
class EmailService(
    webClientBuilder: WebClient.Builder
) {
    private val webClient =
        webClientBuilder
            .baseUrl("http://localhost:8080/LOG_REQUISICAO")
            .build()

    fun enviar(pdfBytes: ByteArray, html: String) {

        val bodyBuilder = MultipartBodyBuilder()

        bodyBuilder.part(
            "pdfBytes",
            ByteArrayResource(pdfBytes)
        )
            .filename("relatorio.pdf")
            .contentType(MediaType.APPLICATION_PDF)

        bodyBuilder.part("texto", html)

        webClient.post()
            .uri("/EMAIL_TESTE")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
            .retrieve()
            .bodyToMono(Void::class.java)
            .block()
    }
}