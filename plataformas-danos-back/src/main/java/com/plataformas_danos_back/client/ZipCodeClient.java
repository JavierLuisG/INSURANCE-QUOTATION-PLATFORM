package com.plataformas_danos_back.client;

import com.plataformas_danos_back.model.dto.ZipCodeDto;

public interface ZipCodeClient {
    ZipCodeDto getByZipCode(String zipCode);
}
