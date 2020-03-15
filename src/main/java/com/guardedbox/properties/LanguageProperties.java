package com.guardedbox.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Properties starting by i18n.
 *
 * @author s3curitybug@gmail.com
 *
 */
@ConfigurationProperties(prefix = "i18n")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class LanguageProperties {

    /** Property: i18n.default-langlanguage. */
    private final String defaultLanguage;

}
