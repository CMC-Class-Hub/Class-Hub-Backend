package com.cmc.classhub.message.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 문자 템플릿에서 사용 가능한 변수 목록
 * - key: 템플릿에서 {key} 형태로 사용되는 한글 변수명
 */
@Getter
@RequiredArgsConstructor
public enum MessageTemplateVariable {

    STUDENT_NAME("수강생명"),
    CLASS_NAME("클래스명"),
    DATE("날짜"),
    TIME("시간"),
    LOCATION("장소"),
    MATERIALS("준비물"),
    PARKING("주차"),
    CLASS_LINK("클래스링크");

    private final String key;

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\{([^{}]+)}");
    private static final Set<String> VALID_KEYS = Arrays.stream(values())
            .map(MessageTemplateVariable::getKey)
            .collect(Collectors.toSet());

    /**
     * 템플릿 본문에서 사용된 변수 중 허용되지 않은 변수 목록 반환
     * 
     * @return 허용되지 않은 변수 목록 (비어있으면 모두 유효)
     */
    public static List<String> findInvalidVariables(String templateBody) {
        if (templateBody == null)
            return List.of();

        Matcher matcher = TOKEN_PATTERN.matcher(templateBody);
        return matcher.results()
                .map(r -> r.group(1).trim())
                .filter(key -> !VALID_KEYS.contains(key))
                .distinct()
                .toList();
    }

    /**
     * 템플릿 본문의 모든 변수가 유효한지 검증
     * 
     * @throws IllegalArgumentException 허용되지 않은 변수가 있으면 예외
     */
    public static void validateTemplate(String templateBody) {
        List<String> invalidVars = findInvalidVariables(templateBody);
        if (!invalidVars.isEmpty()) {
            throw new IllegalArgumentException(
                    "허용되지 않은 템플릿 변수: " + invalidVars +
                            ". 사용 가능한 변수: " + VALID_KEYS);
        }
    }
}
