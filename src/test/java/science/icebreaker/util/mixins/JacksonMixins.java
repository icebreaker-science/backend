package science.icebreaker.util.mixins;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JacksonMixins {
    public static interface IgnoreIdMixIn {
        @JsonIgnore
        abstract Integer getId();
    }
}
