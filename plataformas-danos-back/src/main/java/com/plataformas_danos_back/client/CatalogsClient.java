package com.plataformas_danos_back.client;

import com.plataformas_danos_back.model.dto.AgentDto;
import com.plataformas_danos_back.model.dto.BusinessLineDto;
import com.plataformas_danos_back.model.dto.GuaranteeDto;
import com.plataformas_danos_back.model.dto.RiskClassificationDto;
import com.plataformas_danos_back.model.dto.SubscriberDto;

import java.util.List;

public interface CatalogsClient {
    List<SubscriberDto> getSubscribers();
    List<AgentDto> getAgents();
    List<BusinessLineDto> getBusinessLines();
    List<RiskClassificationDto> getRiskClassifications();
    List<GuaranteeDto> getGuarantees();
}
