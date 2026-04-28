package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.entity.DataInconsistency;

public interface InconsistencyNotificationService {

    void notifyCritical(DataInconsistency inconsistency);

    void notifyBatchThresholdExceeded(int total, int inconsistentCount, String dataType);
}
