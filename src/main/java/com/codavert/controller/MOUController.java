package com.codavert.controller;

import com.codavert.dto.MOUDto;
import com.codavert.entity.MOU;
import com.codavert.service.MOUService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/mous")
public class MOUController {

    @Autowired
    private MOUService mouService;

    @GetMapping
    public ResponseEntity<Page<MOU>> getMOUs(@RequestParam Long userId, Pageable pageable) {
        return ResponseEntity.ok(mouService.getMOUsByUserId(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMOUById(@PathVariable Long id) {
        return mouService.getMOUById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MOU> createMOU(@RequestBody MOUDto dto) {
        return ResponseEntity.ok(mouService.createMOU(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MOU> updateMOU(@PathVariable Long id, @RequestBody MOUDto dto) {
        return ResponseEntity.ok(mouService.updateMOU(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMOU(@PathVariable Long id) {
        mouService.deleteMOU(id);
        return ResponseEntity.ok("MOU deleted successfully");
    }

    @GetMapping("/next-number")
    public ResponseEntity<String> getNextMouNumber(@RequestParam Long userId) {
        return ResponseEntity.ok(mouService.getNextMouNumber(userId));
    }
}

