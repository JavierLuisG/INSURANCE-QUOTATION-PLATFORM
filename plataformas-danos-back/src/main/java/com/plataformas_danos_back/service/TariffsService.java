package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.TariffCatDto;
import com.plataformas_danos_back.model.dto.TariffElectronicEquipmentDto;
import com.plataformas_danos_back.model.dto.TariffFireDto;

import java.util.List;

public interface TariffsService {
    List<TariffFireDto> getTariffsFire();
    TariffCatDto getTariffCat(String zona);
    List<TariffElectronicEquipmentDto> getTariffsElectronicEquipment();
}
