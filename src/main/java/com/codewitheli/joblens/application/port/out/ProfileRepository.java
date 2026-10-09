package com.codewitheli.joblens.application.port.out;

import com.codewitheli.joblens.domain.model.SkillProfile;

/**
 * Outbound port for the read-only sample profile used to pre-fill the UI.
 *
 * <p>Visitor profiles are not stored server-side — they travel with each analyze
 * request — so this port only needs to supply the sample.
 */
public interface ProfileRepository {

    SkillProfile getActive();
}
