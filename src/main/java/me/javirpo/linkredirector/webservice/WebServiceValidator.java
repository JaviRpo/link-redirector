package me.javirpo.linkredirector.webservice;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebServiceValidator {
    public static void required(String value, String field) {
        is(!StringUtils.isEmpty(value), field);
    }

    public static void required(Object value, String field) {
        is(value != null, field);
    }

    public static void is(boolean value, String field) {
        if (!value) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " is requered");
        }
    }
}
