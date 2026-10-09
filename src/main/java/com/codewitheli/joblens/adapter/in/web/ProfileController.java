package com.codewitheli.joblens.adapter.in.web;

import com.codewitheli.joblens.application.port.in.ManageProfileUseCase;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the read-only <em>sample</em> skills-only profile used to pre-fill the
 * UI. There is no update endpoint: each visitor's profile lives in their own
 * browser and is sent with each analyze request, so the server stores no one's
 * profile.
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ManageProfileUseCase manageProfile;

    public ProfileController(ManageProfileUseCase manageProfile) {
        this.manageProfile = manageProfile;
    }

    @GetMapping
    public SkillProfile get() {
        return manageProfile.getProfile();
    }
}
