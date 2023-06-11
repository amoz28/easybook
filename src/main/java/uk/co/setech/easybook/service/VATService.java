package uk.co.setech.easybook.service;

import uk.co.setech.easybook.dto.VATDto;

import java.util.List;

public interface VATService {
    VATDto createVAT(VATDto vatDto);

    VATDto updateVAT(VATDto vatDto);

    VATDto getVAT(Long id);

    List<VATDto> getAllVAT();

    void deleteVAT(Long id);
}
