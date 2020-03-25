package com.guardedbox.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.guardedbox.constants.Header;
import com.guardedbox.properties.LanguageProperties;

import lombok.RequiredArgsConstructor;

/**
 * Language Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@RequiredArgsConstructor
public class LanguageService {

    /** LanguageProperties. */
    private final LanguageProperties languageProperties;

    /** Current Request. */
    private final HttpServletRequest request;

    /**
     * @return The default language.
     */
    public String getDefaultLanguage() {

        return languageProperties.getDefaultLanguage();

    }

    /**
     * @return The app language, based on the App-Language header of the current request, or the default language by default.
     */
    public String getAppLanguage() {

        String appLanguage = request.getHeader(Header.APP_LANGUAGE.getHeaderName());

        if (StringUtils.isEmpty(appLanguage))
            return getDefaultLanguage();

        return appLanguage;

    }

}
