import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Service
class OpenAiService(
    webClientBuilder : WebClient.Builder,
    @Value("\${openai.api.key}")
    private val apiKey: String,
) {

    private val webClient: WebClient = webClientBuilder.baseUrl("https://api.openai.com/v1")
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
        .build()

    fun uploadTxt(resource: ByteArrayResource): String {

        val body = LinkedMultiValueMap<String, Any>().apply {
            add("purpose", "assistants")
            add("file", resource)
        }

        val response = webClient.post()
            .uri("/files")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(body))
            .retrieve()
            .bodyToMono(Map::class.java)
            .block()

        return response?.get("id") as String
    }
}
