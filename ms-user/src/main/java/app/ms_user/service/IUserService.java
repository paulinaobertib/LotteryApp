package app.ms_user.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IUserService {

    ResponseEntity<String> singUp(Map<String, String> requestMap);
}
