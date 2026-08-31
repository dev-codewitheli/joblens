package com.codewitheli.joblens.adapter.in.web;

import com.codewitheli.joblens.adapter.in.web.dto.ProfileUpdateRequest;
import com.codewitheli.joblens.application.port.in.ManageProfileUseCase;
import com.codewitheli.joblens.domain.model.SkillProfile;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Read and update the skills-only profile (no personal data). */
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

    @PutMapping
    public SkillProfile update(@Valid @RequestBody ProfileUpdateRequest request) {
        return manageProfile.updateProfile(request.toDomain());
    }
}
