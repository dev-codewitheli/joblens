package com.codewitheli.joblens.application.port.in;

import com.codewitheli.joblens.domain.model.SkillProfile;

/**
 * Inbound port: read and update the skills-only profile.
 */
public interface ManageProfileUseCase {

    SkillProfile getProfile();

    SkillProfile updateProfile(SkillProfile profile);
}
