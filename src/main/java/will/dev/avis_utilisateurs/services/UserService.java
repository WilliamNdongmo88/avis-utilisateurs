package will.dev.avis_utilisateurs.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import will.dev.avis_utilisateurs.entities.Role;
import will.dev.avis_utilisateurs.entities.User;
import will.dev.avis_utilisateurs.entities.Validation;
import will.dev.avis_utilisateurs.enums.TypeDeRole;
import will.dev.avis_utilisateurs.repository.UserRepository;
import will.dev.avis_utilisateurs.repository.ValidationRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ValidationService validationService;
    private final ValidationRepository validationRepository;

    public void create(User user) {
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new RuntimeException("Email invalide");
        }

        Optional<User> optionalUser = this.userRepository.findByEmail(user.getEmail());
        if (!optionalUser.isEmpty()) {
            throw new RuntimeException("Email déjà existant");
        }

        String mdpCrypte = this.bCryptPasswordEncoder.encode(user.getPassword());
        user.setMdp(mdpCrypte);

        user = this.userRepository.save(user);
        this.validationService.enregistrer(user);
    }

    public void activation(Map<String, String> activation) {
        Validation validation = this.validationService.lireCode(activation.get("code"));
        if (Instant.now().isAfter(validation.getExpiration())) {
            throw new RuntimeException("Votre code a expiré");
        }
        User userActiver = this.userRepository.findById(validation.getUser().getId()).orElseThrow(() -> new RuntimeException("Utilisateur inconnu"));
        userActiver.setActif(true);
        Instant creation = Instant.now();
        validation.setActivation(creation);
        validation.setValidationDay(LocalDate.now(ZoneId.systemDefault()));
        this.validationRepository.save(validation);
        this.userRepository.save(userActiver);
    }

    public ResponseEntity<Map<String, Object>> getAccountCreateToDay(){
        LocalDate today = LocalDate.now();
        System.out.println("Date du jour : " + today);
        List<User> users = this.userRepository.findByCreateDay(today);
        Map<String, Object> response = new HashMap<>();
        response.put("date", today.toString());
        response.put("totalAccountsCreated", users.size());
        response.put("users", users);

        return ResponseEntity.ok(response);
    }
}