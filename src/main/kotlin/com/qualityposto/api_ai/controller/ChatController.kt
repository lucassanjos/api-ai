package com.qualityposto.api_ai.controller

import com.qualityposto.api_ai.service.ChatService
import com.qualityposto.api_ai.service.PdfService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ChatController (
    private val chatService: ChatService,
    private val pdfService: PdfService,
) {
    @PostMapping(
        "/ai/pdf/analise",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun pdfAnalise(
        @RequestPart arquivo: MultipartFile,
        @RequestParam idChat: String
    ): ResponseEntity<Void> {
        chatService.analisarAsync(pdfBytes = arquivo.bytes, idChat)
        return ResponseEntity.accepted().build()
    }
}