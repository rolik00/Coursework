package ru.coursework.MinorsHSEFeedback.enums;

public enum Errors {
    IS_EXIST_ERROR("Пользователь с такой почтой уже зарегистрирован"),
    IS_NOT_HSE_ERROR("Не является корпоративной почтой ВШЭ"),
    UNRELIABLE_PASSWORD_ERROR("Пароль должен быть длиной не менее 8 символов, содержать минимум 1 заглавную букву, 1 строчную и 1 специальный символ (@#$%^&+=_)"),
    PASSWORD_NOT_MATCH_ERROR("Пароли не совпадают"),
    PASSWORD_MATCH_ERROR("Текущий и новый пароли совпадают"),
    UNCORRECT_PASSWORD_ERROR("Неправильно введен текущий пароль"),

    COUNT_MORE_FOUR("Превышен лимит количества отзывов для пользователя");

    private String title;
    Errors(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
