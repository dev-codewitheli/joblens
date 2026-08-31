package com.codewitheli.joblens.application.port.out;

import com.codewitheli.joblens.domain.model.SkillProfile;

/**
 * Outbound port for reading and writing the user's skills-only profile.
 *
 * <p>The MVP is single-user, so it exposes one "active" profile.
 */
public interface ProfileRepository {

    SkillProfile getActive();

    SkillProfile save(SkillProfile profile);
}
