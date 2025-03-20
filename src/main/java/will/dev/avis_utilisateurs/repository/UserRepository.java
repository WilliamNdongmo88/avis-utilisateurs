package will.dev.avis_utilisateurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import will.dev.avis_utilisateurs.entities.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByCreateDay(LocalDate createDay);
}
