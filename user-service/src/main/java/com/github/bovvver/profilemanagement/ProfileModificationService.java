package com.github.bovvver.profilemanagement;

import com.github.bovvver.infrastructure.UserNotFoundException;
import com.github.bovvver.shared.CurrentUser;
import com.github.bovvver.vo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ProfileModificationService {

    private final CurrentUser currentUser;
    private final ProviderProfileReadRepository providerProfileReadRepository;
    private final ProviderProfileRepository providerProfileRepository;

    @Transactional
    ProfileUpdateResponse updateProfile(final ProfileUpdateRequest request) {
        UserId userId = currentUser.getId();

        ProviderProfileEntity profileEntity = providerProfileReadRepository.findByUserId(userId.value())
                .orElseThrow(() -> new UserNotFoundException("Profile not found for userId=" + userId.value()));

        ProviderProfile profile = ProviderProfileMapper.toDomain(profileEntity);
        profile.update(
                Bio.of(request.bio()),
                City.of(request.city()),
                Country.of(request.country()),
                request.categories()
        );
        providerProfileRepository.save(profile);

        return new ProfileUpdateResponse(
                profile.getUserId().value(),
                profile.getBio() == null ? null : profile.getBio().value(),
                profile.getCity() == null ? null : profile.getCity().value(),
                profile.getCountry() == null ? null : profile.getCountry().code(),
                profile.getCategories()
        );
    }
}
