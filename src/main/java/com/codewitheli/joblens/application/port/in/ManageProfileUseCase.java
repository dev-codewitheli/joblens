package com.codewitheli.joblens.application.port.in;

import com.codewitheli.joblens.domain.model.SkillProfile;

/**
 * Inbound port: read the read-only sample profile used to pre-fill the UI.
 */
public interface ManageProfileUseCase {

    SkillProfile getProfile();
}
