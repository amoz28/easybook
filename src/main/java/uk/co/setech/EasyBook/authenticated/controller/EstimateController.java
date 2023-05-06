package uk.co.setech.EasyBook.authenticated.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.setech.EasyBook.auth.GeneralResponse;
import uk.co.setech.EasyBook.authenticated.dto.EstimateDto;
import uk.co.setech.EasyBook.authenticated.dto.InvoiceDto;
import uk.co.setech.EasyBook.authenticated.service.EstimateService;
import uk.co.setech.EasyBook.authenticated.service.InvoiceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/estimate")
public class EstimateController {

    private final EstimateService estimateService;

    @PostMapping
    public ResponseEntity<EstimateDto> createEstimate(@RequestBody EstimateDto estamateDto){
        return ResponseEntity.ok(estimateService.createEstimate(estamateDto));
    }

    @PutMapping
    public ResponseEntity<EstimateDto> updateEstimate(@RequestBody EstimateDto estimateDto){
        return ResponseEntity.ok(estimateService.updateEstimate(estimateDto));
    }

    @GetMapping
    public ResponseEntity<List<EstimateDto>> getAllEstimate(){
        return ResponseEntity.ok(estimateService.getAllEstimate());
    }

    @GetMapping("/getEstimateById")
    public ResponseEntity<EstimateDto> getEstimate(@RequestParam String estimateId){
        return ResponseEntity.ok(estimateService.getEstimateById(estimateId));
    }

    @DeleteMapping
    public ResponseEntity<GeneralResponse> deleteEstimate(@RequestParam String estamateId){
        return ResponseEntity.ok(estimateService.deleteEstimateById(estamateId));
    }
}
