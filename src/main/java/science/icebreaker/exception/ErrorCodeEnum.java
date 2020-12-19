package science.icebreaker.exception;

public enum ErrorCodeEnum {

    ERR_ACC_001("ERR_ACC_001"),
    ERR_ACC_002("ERR_ACC_002"),
    ERR_ACC_003("ERR_ACC_003"),
    ERR_ACC_004("ERR_ACC_004"),
    ERR_ACC_005("ERR_ACC_005"),
    ERR_ACC_006("ERR_ACC_006"),
    ERR_ACC_007("ERR_ACC_007"),
    ERR_FILTER_001("ERR_FILTER_001"),
    ERR_DEVICE_001("ERR_DEVICE_001"),
    ERR_DEVICE_002("ERR_DEVICE_002"),
    ERR_DEVICE_003("ERR_DEVICE_003"),
    ERR_WIKI_001("ERR_WIKI_001"),
    ERR_WIKI_002("ERR_WIKI_002"),
    ERR_WIKI_003("ERR_WIKI_003"),
    ERR_DEVICE_AVAIL_001("ERR_DEVICE_AVAIL_001"),
    ERR_STRG_001("ERR_STRG_001"),
    ERR_STRG_002("ERR_STRG_002"),
    ERR_CAPTCHA_001("ERR_CAPTCHA_001"),
    ERR_CAPTCHA_002("ERR_CAPTCHA_002"),
    ERR_RST_PASS_001("ERR_RST_PASS_001");

    public final String value;

    ErrorCodeEnum(String value) {
        this.value = value;
    }
}
