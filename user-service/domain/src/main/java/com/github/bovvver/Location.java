package com.github.bovvver;

record Location(double latitude, double longitude) {

    Location {
        if(latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees.");
        }
        if(longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees.");
        }
    }

    static Location of(double latitude, double longitude) {
        return new Location(latitude, longitude);
    }
}
