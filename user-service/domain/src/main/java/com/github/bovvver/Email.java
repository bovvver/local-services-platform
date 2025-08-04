package com.github.bovvver;

import java.util.regex.Pattern;

record Email(String value) {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^((?!\\.)[\\w-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$");

    Email {
        if(!EMAIL_REGEX.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }
}
