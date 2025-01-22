package ru.coursework.MinorsHSEFeedback.enums;

public enum Errors {
    IS_EXIST_ERROR("Пользователь с такой почтой уже зарегистрирован"),
    IS_NOT_HSE_ERROR("Не является корпоративной почтой ВШЭ"),
    UNRELIABLE_PASSWORD_ERROR("Пароль должен быть длиной не менее 8 символов, содержать минимум 1 заглавную букву, 1 строчную и 1 специальный символ (@#$%^&+=_)"),
    PASSWORD_NOT_MATCH_ERROR("Пароли не совпадают"),
    UNCORRECT_PASSWORD_ERROR("Неправильно введен пароль");

    private String title;
    Errors(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
