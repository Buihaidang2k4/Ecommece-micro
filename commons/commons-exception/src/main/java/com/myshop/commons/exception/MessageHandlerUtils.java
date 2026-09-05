package com.myshop.commons.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public final class MessageHandlerUtils {

    private static MessageSource messageSource;

    private MessageHandlerUtils() {
    }

    public static void setMessageSource(MessageSource source) {
        messageSource = source;
    }

    public static String getMessage(String key) {
        return getMessage(key, (Object[]) null);
    }

    public static String getMessage(String key, Object... args) {
        if (messageSource == null) {
            return key;
        }
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }

    public static String getMessage(String key, Locale locale, Object... args) {
        if (messageSource == null) {
            return key;
        }
        Locale resolved = locale != null ? locale : LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, resolved);
    }
}
