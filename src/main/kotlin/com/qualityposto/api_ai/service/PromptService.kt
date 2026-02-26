package com.qualityposto.api_ai.service

import org.springframework.stereotype.Service

@Service
class PromptService {
    fun promptResumoPagina(pagina: String): String =
        """
        Estou te enviando página por página...
        Não é necessário realizar analise, apenas extraia as informações de forma objetiva.

        $pagina
        """.trimIndent()

    fun promptFinal(memoria: List<String>): String =
        """
        Você é um analista financeiro especializado em ERP para postos de combustíveis.
        Analise os dados fornecidos e gere uma conclusão executiva objetiva.
        
        A análise deve:
        
        - Identificar filiais, período, tipo de relatório e principais indicadores detectados.        
        - Comparar mês a mês (valores e variação percentual).        
        - Avaliar acumulado do período.        
        - Identificar crescimento, retração, tendências e sazonalidade.        
        - Destacar anomalias, pontos de atenção e oportunidades.        
        - Se houver múltiplas filiais, comparar desempenho entre elas.        
        - Caso falte informação, indicar explicitamente.
        
        Formato obrigatório: retornar exclusivamente HTML válido estruturado para e-mail, com resumo executivo consolidado em no máximo 10 linhas.

        ${memoria.joinToString("\n")}
        """.trimIndent()
}