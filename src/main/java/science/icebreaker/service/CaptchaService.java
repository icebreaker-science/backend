package science.icebreaker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import science.icebreaker.config.properties.CaptchaConfigurationProperties;
import science.icebreaker.data.captcha.HCaptchaResponse;
import science.icebreaker.exception.CaptchaInvalidException;
import science.icebreaker.exception.ErrorCodeEnum;

@Service
public class CaptchaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaService.class);

    private final CaptchaConfigurationProperties captchaProperties;
    private final WebClient captchaWebClient;

    public CaptchaService(CaptchaConfigurationProperties captchaProperties, WebClient captchaWebClient) {
        this.captchaProperties = captchaProperties;
        this.captchaWebClient = captchaWebClient;
    }

    /**
     * Verifies that a captcha is valid. Otherwise throws a {@link CaptchaInvalidException CaptchaInvalidException}.
     * @param token hCaptcha token
     * @throws CaptchaInvalidException if captcha is not valid or validation failed.
     */
    public void verifyCaptcha(@Nullable String token) throws CaptchaInvalidException {
        if (token == null) {
            throw new CaptchaInvalidException(ErrorCodeEnum.ERR_CAPTCHA_001, HttpStatus.BAD_REQUEST);
        }
        HCaptchaResponse hCaptchaResponse = getValidationResponse(token);

        if (!hCaptchaResponse.isSuccess()) {
            if (hCaptchaResponse.hasServerError()) {
                LOGGER.error("Error while validating captcha: {}, host: {}, timestamp: {}",
                        hCaptchaResponse.getErrorCodes(),
                        hCaptchaResponse.getHostname(),
                        hCaptchaResponse.getChallengeTs());
                throw new CaptchaInvalidException(ErrorCodeEnum.ERR_CAPTCHA_002, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            throw new CaptchaInvalidException(ErrorCodeEnum.ERR_CAPTCHA_001, HttpStatus.BAD_REQUEST);
        }
    }

    // use extra method for mocking
    public HCaptchaResponse getValidationResponse(String token) throws CaptchaInvalidException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", captchaProperties.getSecret());
        map.add("sitekey", captchaProperties.getSiteKey());
        map.add("response", token);

        HCaptchaResponse hCaptchaResponse =  captchaWebClient
                .post()
                .body(BodyInserters.fromFormData(map))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    LOGGER.error("Error while validating captcha. Code: {}", clientResponse.statusCode());
                    throw new CaptchaInvalidException(ErrorCodeEnum.ERR_CAPTCHA_002, HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .bodyToMono(HCaptchaResponse.class)
                .block();
        return hCaptchaResponse != null ? hCaptchaResponse : new HCaptchaResponse();
    }
}
