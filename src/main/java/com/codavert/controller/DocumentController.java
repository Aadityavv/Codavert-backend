package com.codavert.controller;

import com.codavert.dto.document.MOURequestDto;
import com.codavert.dto.document.SRSRequestDto;
import com.codavert.entity.Project;
import com.codavert.entity.ProjectDocument;
import com.codavert.repository.ProjectRepository;
import com.codavert.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/documents")
@Tag(name = "Document Generation", description = "Generate PDF documents like MOU and SRS")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ProjectRepository projectRepository;

    @Operation(summary = "Generate Memorandum of Understanding (MOU)")
    @PostMapping(value = "/generate/mou", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateMOU(@RequestBody MOURequestDto request) {
        Project project = projectRepository.findById(request.getProjectId())
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + request.getProjectId()));

        String content = documentService.generateMOUContent(project);
        // Apply template override if provided
        if (request.getTemplate() != null && !request.getTemplate().isBlank()) {
            content = request.getTemplate();
        } else if (request.getTemplateKey() != null && !request.getTemplateKey().isBlank()) {
            content = applyBuiltInTemplate(request.getTemplateKey(), content);
        }
        if (request.getTerms() != null && !request.getTerms().isBlank()) {
            content += "\nTERMS AND CONDITIONS\n---------------------\n" + request.getTerms() + "\n";
        }
        if (request.getDeliverables() != null && !request.getDeliverables().isEmpty()) {
            content += "\nDELIVERABLES\n------------\n" + String.join("\n", request.getDeliverables()) + "\n";
        }

        byte[] pdf = renderPdf(content);
        String filename = String.format("MOU-%d-%d.pdf", request.getProjectId(), request.getClientId());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @Operation(summary = "Generate Software Requirements Specification (SRS)")
    @PostMapping(value = "/generate/srs", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateSRS(@RequestBody SRSRequestDto request) {
        Project project = projectRepository.findById(request.getProjectId())
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + request.getProjectId()));

        String content = documentService.generateSRSContent(project);
        if (request.getTemplate() != null && !request.getTemplate().isBlank()) {
            content = request.getTemplate();
        } else if (request.getTemplateKey() != null && !request.getTemplateKey().isBlank()) {
            content = applyBuiltInTemplate(request.getTemplateKey(), content);
        }
        if (request.getRequirements() != null && !request.getRequirements().isEmpty()) {
            StringBuilder sb = new StringBuilder(content);
            sb.append("\nADDITIONAL REQUIREMENTS\n-----------------------\n");
            for (SRSRequestDto.RequirementDto req : request.getRequirements()) {
                sb.append(String.format("[%s] (%s) %s\n%s\n\n", req.getId(), req.getPriority(), req.getTitle(), req.getDescription()));
            }
            content = sb.toString();
        }

        byte[] pdf = renderPdf(content);
        String filename = String.format("SRS-%d.pdf", request.getProjectId());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    private byte[] renderPdf(String content) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);
        for (String line : content.split("\n")) {
            doc.add(new Paragraph(line));
        }
        doc.close();
        return baos.toByteArray();
    }

    // Listing endpoints for Resource Library
    @GetMapping
    public ResponseEntity<List<ProjectDocument>> getDocumentsByUser(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(documentService.getDocumentsByUserId(userId));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ProjectDocument>> getDocumentsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(documentService.getDocumentsByProjectId(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDocument> getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private String applyBuiltInTemplate(String key, String fallback) {
        switch (key) {
            case "mou-basic":
                return "MEMORANDUM OF UNDERSTANDING\n============================\n\n" + fallback;
            case "srs-classic":
                return "SOFTWARE REQUIREMENTS SPECIFICATION\n===================================\n\n" + fallback;
            default:
                return fallback;
        }
    }
}


