package ru.coursework.MinorsHSEFeedback.extraFunctions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.DisplayName;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomStringTest {

    private final RandomString randomString = new RandomString();
    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuv";

    @Test
    @DisplayName("Генерация строки заданной длины")
    void testRandomAlphanumericString_Length() {
        int length = 10;
        String result = randomString.randomAlphanumericString(length);
        assertEquals(length, result.length());
    }

    @Test
    @DisplayName("Генерация строки нулевой длины")
    void testRandomAlphanumericString_ZeroLength() {
        String result = randomString.randomAlphanumericString(0);
        assertTrue(result.isEmpty());
    }

    @RepeatedTest(10)
    @DisplayName("Повторяющаяся проверка длины строки")
    void testRandomAlphanumericString_RepeatedLengthCheck() {
        int length = 15;
        String result = randomString.randomAlphanumericString(length);
        assertEquals(length, result.length());
    }

    @Test
    @DisplayName("Все символы строки принадлежат алфавитно-цифровому набору")
    void testRandomAlphanumericString_ValidCharacters() {
        int length = 100;
        String result = randomString.randomAlphanumericString(length);

        for (char c : result.toCharArray()) {
            assertTrue(ALPHANUMERIC_CHARS.indexOf(c) >= 0,
                    "Символ '" + c + "' не содержится в допустимом наборе символов");
        }
    }

    @Test
    @DisplayName("Генерация уникальных строк")
    void testRandomAlphanumericString_Uniqueness() {
        int length = 8;
        int numberOfStrings = 100;
        Set<String> generatedStrings = new HashSet<>();

        for (int i = 0; i < numberOfStrings; i++) {
            String result = randomString.randomAlphanumericString(length);
            generatedStrings.add(result);
        }

        assertEquals(numberOfStrings, generatedStrings.size(),
                "Не все сгенерированные строки уникальны");
    }

    @Test
    @DisplayName("Отрицательная длина строки")
    void testRandomAlphanumericString_NegativeLength() {
        assertThrows(NegativeArraySizeException.class,
                () -> randomString.randomAlphanumericString(-1));
    }
}