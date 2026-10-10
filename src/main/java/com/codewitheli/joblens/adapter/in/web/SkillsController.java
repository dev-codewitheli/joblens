package com.codewitheli.joblens.adapter.in.web;

import com.codewitheli.joblens.application.skills.TechDictionary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Skill autocomplete backed by the shared {@link TechDictionary}. */
@RestController
@RequestMapping("/api/skills")
public class SkillsController {

    private final TechDictionary dictionary;

    public SkillsController(TechDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @GetMapping
    public List<String> suggest(@RequestParam(name = "q", required = false, defaultValue = "") String query) {
        return dictionary.suggest(query, 10);
    }
}
