package org.develop.rest.auth.services.authentication;

import org.develop.rest.auth.dto.JwtAuthResponse;
import org.develop.rest.auth.dto.UserSignInRequest;
import org.develop.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}