package com.guardedbox.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.guardedbox.dto.CaptchaVerificationResponseDto;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

/**
 * Captcha Verification Service.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class CaptchaVerificationService {

    /** Logger. */
    private final Logger logger;

    /** Property: captcha.verification-url. */
    private final String captchaVerificationUrl;

    /** Property: captcha.secret-key. */
    private final String captchaSecretKey;

    /** RestTemplate. */
    private final RestTemplate restTemplate;

    /** Current Request. */
    private final HttpServletRequest request;

    /**
     * Constructor with Attributes.
     * 
     * @param captchaVerificationUrl Property: captcha.verification-url.
     * @param captchaSecretKey Property: captcha.secret-key.
     * @param restTemplate RestTemplate.
     * @param request Current Request.
     */
    public CaptchaVerificationService(
            @Value("${captcha.verification-url}") String captchaVerificationUrl,
            @Value("${captcha.secret-key}") String captchaSecretKey,
            @Autowired RestTemplate restTemplate,
            @Autowired HttpServletRequest request) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.captchaVerificationUrl = captchaVerificationUrl;
        this.captchaSecretKey = captchaSecretKey;
        this.restTemplate = restTemplate;
        this.request = request;
    }

    /**
     * Verifies a captcha response.
     * 
     * @param captchaResponse The captcha response.
     * @return Boolean indicating if the captcha response is valid.
     */
    public boolean verifyCaptchaResponse(
            String captchaResponse) {

        ResponseEntity<CaptchaVerificationResponseDto> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(
                    String.format(captchaVerificationUrl,
                            URLEncoder.encode(captchaSecretKey, StandardCharsets.UTF_8.toString()),
                            URLEncoder.encode(captchaResponse, StandardCharsets.UTF_8.toString()),
                            URLEncoder.encode(request.getRemoteAddr(), StandardCharsets.UTF_8.toString())),
                    null, CaptchaVerificationResponseDto.class);
        } catch (RestClientException | UnsupportedEncodingException e) {
            logger.error("Error during the captcha verification request", e);
            return false;
        }

        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            logger.error(String.format("The captcha verification request returned status code %s", responseEntity.getStatusCode()));
            return false;
        }
        if (!responseEntity.hasBody() || responseEntity.getBody().getSuccess() == null) {
            logger.error("The captcha verification response is malformed");
            return false;
        }

        return responseEntity.getBody().getSuccess();

    }

}
