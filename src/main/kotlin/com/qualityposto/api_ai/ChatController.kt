package com.qualityposto.api_ai

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


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

    @PostMapping("/excel")
    fun analyzeExcel(
        @RequestParam("arquivo") arquivo: MultipartFile,
        @RequestParam("instrucao") instrucao: String
    ): String? {

        val excelData = StringBuilder()

        arquivo.inputStream.use { inputStream ->
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)

            for (row in sheet) {
                for (cell in row) {
                    excelData.append(cell.toString()).append(" | ")
                }
                excelData.append("\n")
            }
            workbook.close()
        }

        return chatClient.prompt()
            .user { u ->
                u.text("""
                Instrução do Usuário: ${instrucao}
                
                Dados do Excel:
                ${excelData}
            """.trimIndent())
            }
            .call()
            .content()
    }
}