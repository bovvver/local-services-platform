package com.github.bovvver.vo;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^((?!\\.)[\\w-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$");

    public Email {
        if(!EMAIL_REGEX.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }
}
