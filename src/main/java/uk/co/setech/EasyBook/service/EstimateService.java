package uk.co.setech.EasyBook.service;

import uk.co.setech.EasyBook.dto.GeneralResponse;
import uk.co.setech.EasyBook.dto.EstimateDto;

import java.util.List;

public interface EstimateService {
    EstimateDto createEstimate(EstimateDto estimateDto);

    EstimateDto updateEstimate(EstimateDto estimateDto);

    List<EstimateDto> getAllEstimate();

    EstimateDto getEstimateById(String estimateId);

    GeneralResponse deleteEstimateById(String email);

}
