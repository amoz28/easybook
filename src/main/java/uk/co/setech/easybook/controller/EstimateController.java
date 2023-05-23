package uk.co.setech.easybook.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.setech.easybook.dto.EstimateDto;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.service.EstimateService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/estimate")
public class EstimateController {

    private final EstimateService estimateService;

    @PostMapping
    public ResponseEntity<EstimateDto> createEstimate(@RequestBody EstimateDto estimateDto) {
        return ResponseEntity.ok(estimateService.createEstimate(estimateDto));
    }

    @PutMapping
    public ResponseEntity<EstimateDto> updateEstimate(@RequestBody EstimateDto estimateDto) {
        return ResponseEntity.ok(estimateService.updateEstimate(estimateDto));
    }

    @GetMapping
    public ResponseEntity<List<EstimateDto>> getAllEstimate() {
        return ResponseEntity.ok(estimateService.getAllEstimate());
    }

    @GetMapping("/getEstimateById")
    public ResponseEntity<EstimateDto> getEstimate(@RequestParam String estimateId) {
        return ResponseEntity.ok(estimateService.getEstimateById(estimateId));
    }

    @DeleteMapping
    public ResponseEntity<GeneralResponse> deleteEstimate(@RequestParam String estimateId) {
        return ResponseEntity.ok(estimateService.deleteEstimateById(estimateId));
    }
}
