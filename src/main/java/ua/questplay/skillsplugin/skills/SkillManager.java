package ua.questplay.skillsplugin.skills;

import java.util.HashMap;

public class SkillManager {
    private final HashMap<SkillType, SkillData> skills = new HashMap<>();

    public void registerSkill(SkillType type, SkillData skill) {
        skills.put(type, skill);
    }

    public SkillData getSkill(SkillType type) {
        return skills.get(type);
    }
}
