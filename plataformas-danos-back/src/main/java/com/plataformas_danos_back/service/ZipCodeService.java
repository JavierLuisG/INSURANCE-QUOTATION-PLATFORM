package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.ZipCodeDto;

public interface ZipCodeService {
    ZipCodeDto getByZipCode(String zipCode);
}
