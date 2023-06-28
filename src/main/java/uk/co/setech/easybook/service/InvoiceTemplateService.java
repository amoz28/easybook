package uk.co.setech.easybook.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.dto.InvoiceTemplateDto;
import uk.co.setech.easybook.exception.CustomException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class InvoiceTemplateService {

    @Value("${invoice.template.path}")
    private String invoiceTemplatePath;

    public List<InvoiceTemplateDto> getAllInvoiceTemplates() throws CustomException {
        File templateFolder = new File(invoiceTemplatePath);
        if (!templateFolder.exists() || !templateFolder.isDirectory()) {
            log.error("template folder does not exist {}", invoiceTemplatePath);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving invoice templates");
        }

        return Stream.of(templateFolder.listFiles())
                .map(file -> {
                    try(var fileReader = Files.newBufferedReader(Paths.get(file.toURI()), StandardCharsets.UTF_8)) {
                        String[] fileNameParams = file.getName().split("\\.");
                        return InvoiceTemplateDto.builder()
                                .content(fileReader.lines().collect(Collectors.joining()))
                                .name(fileNameParams[0])
                                .id(fileNameParams[1])
                                .build();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }
}
