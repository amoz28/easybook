package uk.co.setech.easybook.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.setech.easybook.dto.VATDto;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.service.impl.VATService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vat")
public class VATController {

    private final VATService vatService;

    @PostMapping
    public ResponseEntity<VATDto> createVAT(
            @RequestBody VATDto vatDto
    ) {
        return ResponseEntity.ok(vatService.createVAT(vatDto));
    }

    @PutMapping
    public ResponseEntity<VATDto> updateVAT(
            @RequestBody VATDto vatDto
    ) {
        return ResponseEntity.ok(vatService.updateVAT(vatDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VATDto> getVAT(@PathVariable Long id) {
        return ResponseEntity.ok(vatService.getVAT(id));
    }

    @GetMapping
    public ResponseEntity<List<VATDto>> getAllVAT() {
        return ResponseEntity.ok(vatService.getAllVAT());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteVAT(@PathVariable Long id) {
        vatService.deleteVAT(id);
        return ResponseEntity.ok(GeneralResponse.builder().message("VAT Deleted Successfully").build());
    }
}
