package app.ms_user.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IUserService {

    ResponseEntity<String> singUp(Map<String, String> requestMap);

    ResponseEntity<String> login(Map<String, String> requestMap);

    ResponseEntity<String> forgotPassword(String email);

    ResponseEntity<String> delete(String email);

    ResponseEntity<String> update(String email);

    ResponseEntity<String> getAll();

    ResponseEntity<String> getUser(String email);

    ResponseEntity<String> changeRol(String email);
}
