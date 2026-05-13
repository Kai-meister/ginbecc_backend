package gov.kh.mcr.inspectorate.service;


import gov.kh.mcr.inspectorate.dto.request.ChangePasswordRequest;
import gov.kh.mcr.inspectorate.dto.request.LoginRequest;
import gov.kh.mcr.inspectorate.dto.response.LoginResponse;
import gov.kh.mcr.inspectorate.dto.response.ResetPasswordResponse;

public interface AuthService {


    LoginResponse login(LoginRequest request);


    LoginResponse refreshToken(String refreshToken);


    void changePassword(Integer userId, ChangePasswordRequest request);

    ResetPasswordResponse resetPassword(Integer userId);

    void logout(String token);
}