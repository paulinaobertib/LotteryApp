package app.ms_user.service.impl;

import app.ms_user.domain.User;
import app.ms_user.util.error.Constant;
import app.ms_user.repository.UserRepository;
import app.ms_user.service.IUserService;
import app.ms_user.util.ConstantUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Slf4j //para agregar los logs
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

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
        user.setStatus("false");
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
}
