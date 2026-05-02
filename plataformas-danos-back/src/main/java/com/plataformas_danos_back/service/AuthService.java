package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.LoginRequest;
import com.plataformas_danos_back.model.dto.LoginResponse;
import com.plataformas_danos_back.model.dto.MessageResponse;
import com.plataformas_danos_back.model.dto.RegisterRequest;

public interface AuthService {

    MessageResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
