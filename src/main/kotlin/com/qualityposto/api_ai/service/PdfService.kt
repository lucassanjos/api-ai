package com.qualityposto.api_ai.service

import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Service

@Service
class PdfService {
    fun extrairPagina(pdfBytes: ByteArray): List<String> {
        Loader.loadPDF(pdfBytes).use { pdf ->
            val stripper = PDFTextStripper()
            return (1..pdf.numberOfPages).map { page ->
                stripper.startPage = page
                stripper.endPage = page
                stripper.getText(pdf)
            }
        }
    }
}
