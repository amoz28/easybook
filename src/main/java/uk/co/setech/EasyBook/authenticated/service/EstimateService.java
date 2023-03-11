package uk.co.setech.EasyBook.authenticated.service;

import uk.co.setech.EasyBook.auth.GeneralResponse;
import uk.co.setech.EasyBook.authenticated.dto.EstimateDto;

import java.util.List;

public interface EstimateService {
    EstimateDto createEstimate(EstimateDto estimateDto);

    EstimateDto updateEstimate(EstimateDto estimateDto);

    List<EstimateDto> getAllEstimate();

    EstimateDto getEstimateById(String estimateId);

    GeneralResponse deleteEstimateById(String email);

}
