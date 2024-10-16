package calculator.model;

import calculator.common.CustomDelimiter;
import calculator.common.ExceptionMessage;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CustomInput extends UserInput {
    public CustomInput(String userInput) {
        super(userInput);
    }

    @Override
    protected void parseInputForCalculate(String userInput) {
        checkCustomDelimFormat(userInput);

        int delimiterStartIdx = userInput.indexOf(CustomDelimiter.START_FORMAT.getValue()) + 2;
        int delimiterEndIdx = userInput.indexOf(CustomDelimiter.END_FORMAT.getValue());

        extractCustomDelimiterAndAdd(userInput, delimiterStartIdx, delimiterEndIdx);

        String[] splitValues = splitCalculatePartByDelimiters(extractCalculatePart(userInput, delimiterEndIdx + 2));
        checkValueToCalculateIsNumber(splitValues);

        inputNumbers = Arrays.stream(splitValues)
                .map(value -> value.isEmpty() ? ZERO_VALUE : value) // 빈 문자열을 "0"으로 치환
                .mapToLong(Long::parseLong) // Long으로 변환
                .toArray();
    }

    @Override
    protected String[] splitCalculatePartByDelimiters(String calculatePart) {
        try {
            String regex = String.join(REGEX_DELIMITER, delimiters.stream().map(Pattern::quote).toArray(String[]::new));
            return calculatePart.split(regex);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException(ExceptionMessage.CALCULATE_PART_IS_INVALID.getValue());
        }
    }

    /***
     * 커스텀 구분자포맷이 지켜졌는지 확인합니다.
     * @param userInput : 사용자의 입력
     */
    private void checkCustomDelimFormat(String userInput) {
        if (!userInput.contains(CustomDelimiter.END_FORMAT.getValue())) {
            throw new IllegalArgumentException(ExceptionMessage.CUSTOM_DELIMITER_FORMAT_NOT_MATCH.getValue());
        }
    }

    /***
     * 사용자의 입력으로부터 커스텀 구분자를 추출합니다.
     * @param userInput : 사용자의 입력
     * @param startIdx : 커스텀 구분자의 추출 시작 인덱스
     * @param endIdx : 커스텀 구분자의 추출 마지막 인덱스
     */
    private void extractCustomDelimiterAndAdd(String userInput, int startIdx, int endIdx) {
        delimiters.add(userInput.substring(startIdx, endIdx));
    }

    /***
     * 사용자의 입력으로부터 계산할 문자열을 추출합니다.
     * @param userInput : 사용자의 입력
     * @param startIdx : 추출 시작 인덱스
     * @return : 추출된 계산 문자열
     */
    private String extractCalculatePart(String userInput, int startIdx) {
        return userInput.substring(startIdx);
    }
}
