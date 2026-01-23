package com.qualityposto.api_ai

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
class ChatController (
    chatClientBuilder: ChatClient.Builder,
    chatMemory: ChatMemory
) {
    private val chatClient =
        chatClientBuilder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build()

    @GetMapping("/ai")
    fun conversaOpenAi(userInput: String): String? {
        return chatClient.prompt()
            .user(userInput)
            .call()
            .content()
    }

    @GetMapping("/ai/memory")
    fun conversaOpenAiMemory(
        @RequestParam userInput: String,
        @RequestParam chatId: String
    ): String? {
        return chatClient.prompt()
            .user(userInput)
            .advisors { it.param(ChatMemory.CONVERSATION_ID, chatId) }
            .call()
            .content()
    }

    @PostMapping("/excel/analise")
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

    @PostMapping("/excel/analise/memory")
    fun conversaOpenAiMemoryAnaliseExcel(
        @RequestParam("arquivo") arquivo: MultipartFile,
        @RequestParam("instrucao") instrucao: String,
        @RequestParam("chatId") chatId: String
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
            .advisors { it.param(ChatMemory.CONVERSATION_ID, chatId) }
            .call()
            .content()
    }
}