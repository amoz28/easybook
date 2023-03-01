package uk.co.setech.EasyBook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.EasyBook.user.User;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}
