package com.codavert.dto;

import com.codavert.entity.JobPosting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingDto {
    private Long id;
    private String title;
    private JobPosting.JobType type;
    private String location;
    private String experience;
    private String salary;
    private String description;
    private List<String> requirements;
    private List<String> responsibilities;
    private JobPosting.PostingStatus status;
    private String createdAt;
    private String updatedAt;
}

