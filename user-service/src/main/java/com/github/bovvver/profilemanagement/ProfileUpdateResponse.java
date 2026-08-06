package com.github.bovvver.profilemanagement;

import com.github.bovvver.vo.ServiceCategory;

import java.util.Set;
import java.util.UUID;

record ProfileUpdateResponse(
        UUID userId,
        String bio,
        String city,
        String country,
        Set<ServiceCategory> categories
) {
}
