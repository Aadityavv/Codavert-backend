package com.codavert.dto.document;

import java.util.List;

public class MOURequestDto {
    private Long projectId;
    private Long clientId;
    private String terms;
    private List<String> deliverables;
    // Optional: raw template content or built-in template key (e.g., "mou-basic")
    private String template;
    private String templateKey;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getTerms() { return terms; }
    public void setTerms(String terms) { this.terms = terms; }

    public List<String> getDeliverables() { return deliverables; }
    public void setDeliverables(List<String> deliverables) { this.deliverables = deliverables; }
    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }
    public String getTemplateKey() { return templateKey; }
    public void setTemplateKey(String templateKey) { this.templateKey = templateKey; }
}


