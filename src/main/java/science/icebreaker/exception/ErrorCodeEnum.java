package science.icebreaker.exception;

public enum ErrorCodeEnum {

    ERR_ACC_001("ERR_ACC_001"),
    ERR_ACC_002("ERR_ACC_002"),
    ERR_ACC_003("ERR_ACC_003"),
    ERR_ACC_004("ERR_ACC_004"),
    ERR_FILTER_001("ERR_FILTER_001"),
    ERR_DEVICE_001("ERR_DEVICE_001"),
    ERR_DEVICE_002("ERR_DEVICE_002"),
    ERR_WIKI_001("ERR_WIKI_001");

    public final String value;

    ErrorCodeEnum(String value) {
        this.value = value;
    }
}
