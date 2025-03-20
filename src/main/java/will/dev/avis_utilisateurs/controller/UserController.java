package will.dev.avis_utilisateurs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import will.dev.avis_utilisateurs.entities.User;
import will.dev.avis_utilisateurs.services.UserService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;

    //Register
    @PostMapping("inscription")
    public void inscription(@RequestBody User user){
        this.userService.create(user);
    }

    //Activation
    @PostMapping("activation")
    public void activation(@RequestBody Map<String, String> activation){
        this.userService.activation(activation);
    }

    //Get Account create today
    @GetMapping("account_create_today")
    public ResponseEntity<Map<String, Object>> getAccountCreateToDay(){
        return this.userService.getAccountCreateToDay();
    }
}
