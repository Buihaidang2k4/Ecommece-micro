package com.myshop.commons.exception;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@AutoConfiguration
public class MessageConfig {

    @Bean
    @ConditionalOnMissingBean(MessageSource.class)
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // commons: messages*; services add messages-auth / messages-core / messages-payment when present
        messageSource.setBasenames(
                "classpath:messages",
                "classpath:messages-auth",
                "classpath:messages-core",
                "classpath:messages-payment"
        );
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setDefaultLocale(Locale.forLanguageTag("vi"));
        MessageHandlerUtils.setMessageSource(messageSource);
        return messageSource;
    }

    @Bean
    @ConditionalOnMissingBean(LocaleResolver.class)
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.forLanguageTag("vi"));
        resolver.setSupportedLocales(List.of(Locale.forLanguageTag("vi"), Locale.ENGLISH));
        return resolver;
    }
}
