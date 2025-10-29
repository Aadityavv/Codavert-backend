package com.codavert.controller;

import com.codavert.dto.SRSDto;
import com.codavert.entity.SRS;
import com.codavert.service.SRSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/srs")
public class SRSController {

    @Autowired
    private SRSService srsService;

    @GetMapping
    public ResponseEntity<Page<SRS>> getSRSs(@RequestParam Long userId, Pageable pageable) {
        return ResponseEntity.ok(srsService.getSRSsByUserId(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSRSById(@PathVariable Long id) {
        return srsService.getSRSById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SRS> createSRS(@RequestBody SRSDto dto) {
        return ResponseEntity.ok(srsService.createSRS(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SRS> updateSRS(@PathVariable Long id, @RequestBody SRSDto dto) {
        return ResponseEntity.ok(srsService.updateSRS(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSRS(@PathVariable Long id) {
        srsService.deleteSRS(id);
        return ResponseEntity.ok("SRS deleted successfully");
    }

    @GetMapping("/next-number")
    public ResponseEntity<String> getNextSrsNumber(@RequestParam Long userId) {
        return ResponseEntity.ok(srsService.getNextSrsNumber(userId));
    }
}

