package com.qualityposto.api_ai.controller

import com.qualityposto.api_ai.service.ChatService
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ChatController (
    chatClientBuilder: ChatClient.Builder,
    chatMemory: ChatMemory,
    private val chatService: ChatService,
    service: ChatService
) {
//    private val chatClient =
//        chatClientBuilder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build()

//    @GetMapping("/ai")
//    fun chatOpenAi(userInput: String): String? {
//        return chatClient.prompt()
//            .user(userInput)
//            .call()
//            .content()
//    }

    @GetMapping("/ai/memory")
    fun chatOpenAiMemory(
        @RequestParam userInput: String,
        @RequestParam chatId: String
    ): String? {
        return chatService.chatOpenAiMemory(userInput, chatId)
    }

    @PostMapping("/excel/analise")
    fun chatOpenAiMemoryAnaliseExcel(
        @RequestParam("arquivo") arquivo: MultipartFile,
        @RequestParam("instrucao") instrucao: String
    ): String? {
        val arquivoExcel = StringBuilder()

        arquivo.inputStream.use { inputStream ->
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)

            for (row in sheet) {
                for (cell in row) {
                    arquivoExcel.append(cell.toString()).append(" | ")
                }
                arquivoExcel.append("\n")
            }
            workbook.close()
        }

        return chatService.chatOpenAiMemoryAnaliseExcel(instrucao, arquivoExcel)
    }

//    @PostMapping("/excel/analise/memory")
//    fun chatOpenAiMemoryAnaliseExcelMemory(
//        @RequestParam("arquivo") arquivo: MultipartFile,
//        @RequestParam("instrucao") instrucao: String,
//        @RequestParam("chatId") chatId: String
//    ): String? {
//        val excelData = StringBuilder()
//
//        arquivo.inputStream.use { inputStream ->
//            val workbook = WorkbookFactory.create(inputStream)
//            val sheet = workbook.getSheetAt(0)
//
//            for (row in sheet) {
//                for (cell in row) {
//                    excelData.append(cell.toString()).append(" | ")
//                }
//                excelData.append("\n")
//            }
//            workbook.close()
//        }
//
//        return chatClient.prompt()
//            .user { u ->
//                u.text("""
//                Instrução do Usuário: ${instrucao}
//
//                Dados do Excel:
//                ${excelData}
//            """.trimIndent())
//            }
//            .advisors { it.param(ChatMemory.CONVERSATION_ID, chatId) }
//            .call()
//            .content()
//    }
}