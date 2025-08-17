package com.marketplace.market_place.api.main.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiSearchLLM {

    private final WebClient openAiWebClient;

    @Value("${openai.model:gpt-4o-mini}") private String model;
    @Value("${openai.api.key:}") private String apiKey; // <-- 키가 비었는지 체크

    // 1) 사용자 자연어 -> 검색 조건(JSON)
    public Map<String, Object> extractFilters(String prompt, Double lat, Double lon) {
        // 키가 없으면 임시 fallback: keywords = [prompt] 로 간단 동작
        if (apiKey == null || apiKey.isBlank()) {
            return Map.of("keywords", List.of(prompt));
        }

        Map<String, Object> schema = Map.of(
                "name", "SearchFilters",
                "schema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "keywords",   Map.of("type","array","items",Map.of("type","string")),
                                "cuisine",    Map.of("type","array","items",Map.of("type","string")),
                                "vibe",       Map.of("type","array","items",Map.of("type","string")),
                                "budget",     Map.of("type","string","enum", List.of("cheap","mid","high")),
                                "nearby",     Map.of("type","boolean"),
                                "radius",     Map.of("type","integer","minimum",300,"maximum",5000),
                                "marketHint", Map.of("type","string")
                        ),
                        "required", List.of("keywords")
                )
        );

        var body = Map.of(
                "model", model,
                "input", List.of(Map.of(
                        "role","user",
                        "content", "사용자 요청: " + prompt +
                                (lat!=null&&lon!=null ? ("\n현재위치: lat="+lat+", lon="+lon) : "")
                )),
                "response_format", Map.of(
                        "type","json_schema",
                        "json_schema", schema
                ),
                "temperature", 0.2
        );

        Map<String,Object> resp = openAiWebClient.post()
                .uri("/responses")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {})
                .block();

        String json = findOutputText(resp);
        try {
            return new ObjectMapper().readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("LLM JSON 파싱 실패: "+json, e);
        }
    }

    // 2) 상단 요약 문장
    public String summarize(String prompt, int total, Double lat, Double lon) {
        // 키가 없으면 서버에서 간단 요약 생성
        if (apiKey == null || apiKey.isBlank()) {
            String loc = (lat!=null && lon!=null) ? "현재 위치 기준으로 " : "";
            return "요청하신 조건에 맞춰 " + loc + "총 " + total + "곳을 추천드립니다.";
        }

        var body = Map.of(
                "model", model,
                "input", List.of(Map.of(
                        "role","user",
                        "content", """
                    다음 검색 요청 결과를 한 문단으로 간결하게 요약해줘.
                    - 요청: """ + prompt + """
                    - 결과 개수: """ + total + """
                    - 위치 기반: """ + ((lat!=null&&lon!=null) ? "예" : "아니오") + """
                    말투는 친절하고 존댓말로.
                    """
                )),
                "temperature", 0.3,
                "max_output_tokens", 120
        );

        Map<String,Object> resp = openAiWebClient.post()
                .uri("/responses")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {})
                .block();

        return findOutputText(resp);
    }

    @SuppressWarnings("unchecked")
    private String findOutputText(Map<String,Object> resp) {
        Object outText = resp.get("output_text");
        if (outText instanceof String s && !s.isBlank()) return s.trim();
        try {
            var output = (List<Map<String,Object>>) resp.get("output");
            if (output!=null && !output.isEmpty()) {
                var content = (List<Map<String,Object>>) output.get(0).get("content");
                if (content!=null && !content.isEmpty()) {
                    Object text = content.get(0).get("text");
                    if (text instanceof String ts) return ts.trim();
                }
            }
        } catch (Exception ignored) {}
        return "";
    }
}