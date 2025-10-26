package com.codavert.dto.document;

import java.util.List;

public class SRSRequestDto {
    public static class RequirementDto {
        private String id;
        private String title;
        private String description;
        private String priority; // LOW | MEDIUM | HIGH

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }

    private Long projectId;
    private List<RequirementDto> requirements;
    private String template;
    private String templateKey;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public List<RequirementDto> getRequirements() { return requirements; }
    public void setRequirements(List<RequirementDto> requirements) { this.requirements = requirements; }
    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }
    public String getTemplateKey() { return templateKey; }
    public void setTemplateKey(String templateKey) { this.templateKey = templateKey; }
}


