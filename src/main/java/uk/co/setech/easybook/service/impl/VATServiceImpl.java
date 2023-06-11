package uk.co.setech.easybook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.dto.VATDto;
import uk.co.setech.easybook.exception.CustomException;
import uk.co.setech.easybook.model.VAT;
import uk.co.setech.easybook.repository.VATRepository;
import uk.co.setech.easybook.service.VATService;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VATServiceImpl implements VATService {
    private final VATRepository vatRepository;
    private final Supplier<CustomException> VAT_DOES_NOT_EXIST = () -> new CustomException(HttpStatus.NOT_FOUND, "VAT Does Not Exist");

    @Override
    public VATDto createVAT(VATDto vatDto) {
        VAT vat = toVAT(vatDto, new VAT());
        vatRepository.save(vat);
        return toVATDto(vat);
    }

    @Override
    public VATDto updateVAT(VATDto vatDto) {
        var vat = vatRepository.findById(vatDto.getId()).orElseThrow(VAT_DOES_NOT_EXIST);
        toVAT(vatDto, vat);
        vatRepository.save(vat);
        return toVATDto(vat);
    }

    @Override
    public VATDto getVAT(Long id) {
        var vat = vatRepository.findById(id).orElseThrow(VAT_DOES_NOT_EXIST);
        return toVATDto(vat);
    }

    @Override
    public List<VATDto> getAllVAT() {
        return vatRepository.findAll()
                .stream()
                .map(this::toVATDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteVAT(Long id) {
        var vat = vatRepository.findById(id).orElseThrow(VAT_DOES_NOT_EXIST);
        vatRepository.delete(vat);
    }

    private VAT toVAT(VATDto dto, VAT vat) {
        BeanUtils.copyProperties(dto, vat);
        return vat;
    }

    private VATDto toVATDto(VAT vat) {
        var dto = new VATDto();
        BeanUtils.copyProperties(vat, dto);
        return dto;
    }
}
