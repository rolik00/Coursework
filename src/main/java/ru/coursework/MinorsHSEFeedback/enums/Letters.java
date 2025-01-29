package ru.coursework.MinorsHSEFeedback.enums;

public enum Letters {
    BEGIN("Здравствуйте, "),
    END("С уважением,\nКоманда MinorsHSEFeedbacks"),
    REGISTRATION("!\n\nСообщаем вам, что вы успешно зарегистрировали на портале MinorsHSEFeedbacks\n\n"),
    UPDATE_PASSWORD("!\n\nМы подтверждаем, что ваш пароль на портале MinorsHSEFeedbacks был успешно обновлен.\nЕсли вы не запрашивали изменение пароля, пожалуйста, немедленно свяжитесь с нашей службой поддержки.\n\n"),
    RESET_PASSWORD_1("!\n\nМы получили запрос на восстановление пароля для вашей учетной записи на портале MinorsHSEFeedbacks.\n\nПожалуйста, перейдите по следующей ссылке, чтобы сбросить ваш пароль:\n"),
    RESET_PASSWORD_2("\n\nЕсли вы не запрашивали восстановление пароля, пожалуйста, проигнорируйте это письмо. Ваш пароль останется неизменным.\n\n");

    private String title;
    Letters(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
