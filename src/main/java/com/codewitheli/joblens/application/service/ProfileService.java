package com.codewitheli.joblens.application.service;

import com.codewitheli.joblens.application.port.in.ManageProfileUseCase;
import com.codewitheli.joblens.application.port.out.ProfileRepository;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.springframework.stereotype.Service;

/**
 * Reads and updates the skills-only profile.
 */
@Service
public class ProfileService implements ManageProfileUseCase {

    private final ProfileRepository profiles;

    public ProfileService(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    @Override
    public SkillProfile getProfile() {
        return profiles.getActive();
    }

    @Override
    public SkillProfile updateProfile(SkillProfile profile) {
        return profiles.save(profile);
    }
}
