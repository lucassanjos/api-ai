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

    fun promptDamiani(memoria: List<String>) :String =
        """                        
            Você é um assistente de análise de desempenho de vendas. 
            Com base nos dados que vou fornecer, gere um relatório de performance em HTML.
        
            ### REGRA DE FORMATO - OBRIGATÓRIO ###
            - Sua resposta deve começar EXATAMENTE com: <!DOCTYPE html>
            - Sua resposta deve terminar EXATAMENTE com: </html>
            - É PROIBIDO usar ``` ou ```html em qualquer parte da resposta.
            - É PROIBIDO incluir qualquer texto antes de <!DOCTYPE html>.
            - Retorne SOMENTE o HTML. Nada mais.
            - Use <br> para quebras de linha. NUNCA use \n solto fora de tags HTML.
            - Use <hr> para separadores (onde aparece ---).
            - Use <strong> para nomes e valores em destaque.
            - O e-mail será renderizado em cliente de e-mail, portanto use HTML inline simples.
        
            O relatório deve seguir EXATAMENTE esta estrutura HTML de exemplo:
        
            <!DOCTYPE html>
            <html>
            <body>
            <p>🏪 <strong>[NOME DA UNIDADE]</strong> [PERÍODO ATUAL] vs [PERÍODO ANTERIOR] 📊</p>
            <p>[EMOJI COR] UNIDADE: <strong>[%]</strong></p>
            <p>🟠 Comparativo: [%] [EMOJI COR ANTERIOR] ([PERÍODO ANTERIOR]) → [%] [EMOJI COR ATUAL] ([PERÍODO ATUAL])<br>
            Variação: [SETA] [+/- p.p.] [EMOJI ALERTA se mudou de faixa]</p>
            <hr>
            <p><strong>RANKING [PERÍODO ATUAL]</strong></p>
            <p>🔵 <strong>EXTRAORDINÁRIO (≥130%)</strong><br>
            [Nome] — [%] ([delta vs período anterior])</p>
            <p>🟢 <strong>MUITO BOM (110–129%)</strong><br>
            [Nome] — [%] ([delta])</p>
            <p>🟡 <strong>SATISFATÓRIO (100–109%)</strong><br>
            [Nome] — [%] ([delta])</p>
            <p>🟠 <strong>REGULAR (80–99%)</strong><br>
            [Nome] — [%] ([delta])</p>
            <p>🔴 <strong>CRÍTICO (&lt;80%)</strong><br>
            [Nome] — [%] | [Nome] — [%]</p>
            <hr>
            <p>[N] pessoas no azul. [Comentário de tendência]. [Alerta se necessário] ⚠️</p>
            <hr>
            <p>🏪 <strong>[NOME DA UNIDADE]</strong> — [%] [EMOJI]<br>
            ([Status de cor] | variação vs período anterior: [+/- p.p.])</p>
            <p>🔵 <strong>EXTRAORDINÁRIO</strong><br>
            [Nome] — [%] | [Nome] — [%]</p>
            <p>🟢 <strong>MUITO BOM</strong></p>
            <p>🟡 <strong>SATISFATÓRIO</strong></p>
            <p>🟠 <strong>REGULAR</strong><br>
            [Nome] — [%]</p>
            <p>🔴 <strong>CRÍTICO</strong><br>
            [Nome] — [%] | [Nome] — [%]</p>
            </body>
            </html>
        
            REGRAS DE COR:
            🔵 EXTRAORDINÁRIO = ≥ 130%
            🟢 MUITO BOM = 110% a 129,99%
            🟡 SATISFATÓRIO = 100% a 109,99%
            🟠 REGULAR = 80% a 99,99%
            🔴 CRÍTICO = abaixo de 80%
        
            REGRAS DE SETA (⬇️):
            - Adicione ⬇️ quando a queda vs período anterior for ≥ 30 p.p. no azul, ou ≥ 15 p.p. no laranja/vermelho
        
            REGRAS DE ALERTA:
            - Se a unidade mudou de faixa de cor entre períodos, sinalize com ⚠️ e descreva a mudança
        
            Agora processe os seguintes dados:        
        ${memoria.joinToString("\n")}
    """.trimIndent()
}