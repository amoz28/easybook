package uk.co.setech.easybook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.easybook.model.User;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}
