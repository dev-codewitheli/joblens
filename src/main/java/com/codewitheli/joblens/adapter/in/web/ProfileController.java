package com.codewitheli.joblens.adapter.in.web;

import com.codewitheli.joblens.adapter.in.web.dto.ResumeRequest;
import com.codewitheli.joblens.application.port.in.ExtractProfileUseCase;
import com.codewitheli.joblens.application.port.in.ManageProfileUseCase;
import com.codewitheli.joblens.domain.model.SkillProfile;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the read-only <em>sample</em> profile (to pre-fill the UI) and extracts a
 * profile from pasted résumé text. No visitor profile is ever stored — each one
 * lives in the browser and is sent with each analyze request.
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ManageProfileUseCase manageProfile;
    private final ExtractProfileUseCase extractProfile;

    public ProfileController(ManageProfileUseCase manageProfile, ExtractProfileUseCase extractProfile) {
        this.manageProfile = manageProfile;
        this.extractProfile = extractProfile;
    }

    @GetMapping
    public SkillProfile get() {
        return manageProfile.getProfile();
    }

    /** Autofill: derive a skills-only profile from pasted résumé text (nothing stored). */
    @PostMapping("/extract")
    public SkillProfile extract(@Valid @RequestBody ResumeRequest request) {
        return extractProfile.fromResume(request.resumeText());
    }
}
