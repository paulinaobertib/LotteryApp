package app.ms_user.service.impl;

import app.ms_user.domain.User;
import app.ms_user.security.CustomerDetailService;
import app.ms_user.security.jwt.JwtUtil;
import app.ms_user.util.error.Constant;
import app.ms_user.repository.UserRepository;
import app.ms_user.service.IUserService;
import app.ms_user.util.ConstantUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Slf4j //para agregar los logs
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    // clase que tiene todos los usuarios con permisos
    // dice quien puede entrar a cual endpoint
    private final AuthenticationManager authenticationManager;

    private final CustomerDetailService customerDetailService;

    private final JwtUtil jwtUtil;

    private boolean validateSignUp(Map<String, String> requestMap){
        if (requestMap.containsKey("name") && requestMap.containsKey("email") && requestMap.containsKey("password")) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        if (requestMap.containsKey("number")) {
            user.setNumber(requestMap.get("number"));
        } else {
            user.setNumber(requestMap.get("null"));
        }
        user.setPassword(requestMap.get("password"));
        user.setStatus("true");
        // por defecto, el usuario nuevo va a tener el rol de usuario
        user.setRol("user");
        return user;
    }

    @Override
    public ResponseEntity<String> singUp(Map<String, String> requestMap) {
        log.info("Registro del usuario");
        try {
            if (validateSignUp(requestMap)) {
                Optional<User> user = userRepository.findByEmail(requestMap.get("email"));
                if (user.isEmpty()) {
                    userRepository.save(getUserFromMap(requestMap));
                    return ConstantUtils.getResponseEntity("Usuario registrado con exito", HttpStatus.CREATED);
                } else {
                    return ConstantUtils.getResponseEntity("Ya existe un usuario con ese email", HttpStatus.BAD_REQUEST);
                }
            } else {
                return ConstantUtils.getResponseEntity(Constant.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  ConstantUtils.getResponseEntity(Constant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Dentro de login");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            // autenticando al usuario
            // viendo si dice quien dice ser
            if (authentication.isAuthenticated()) {
                // el status tiene que estar en true, que significa que esta activo
                if (customerDetailService.getUserDetail().get().getStatus().equalsIgnoreCase("true")){
                    // le pasamos el token al usuario
                    return new ResponseEntity<String>(
                            "{\"token\":\"" +
                                    jwtUtil.generateToken(customerDetailService.getUserDetail().get().getEmail(),
                                    customerDetailService.getUserDetail().get().getRol()) + "\")}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"mensaje\":" + "La cuenta ha sido eliminada "+"\"}", HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
        // si no logra hacer el try, tira este return
        return new ResponseEntity<String>("{\"mensaje\":" + "Credenciales incorrectas "+"\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> forgotPassword(String email) {
        return null;
    }

    @Override
    public ResponseEntity<String> delete(String email) {
        return null;
    }

    @Override
    public ResponseEntity<String> update(String email) {
        return null;
    }

    @Override
    public ResponseEntity<String> getAll() {
        return null;
    }

    @Override
    public ResponseEntity<String> getUser(String email) {
        return null;
    }

    @Override
    public ResponseEntity<String> changeRol(String email) {
        return null;
    }
}
