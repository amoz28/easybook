package uk.co.setech.EasyBook.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.co.setech.EasyBook.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationOtpRepository
        extends JpaRepository<ConfirmationOtp, Long> {

    Optional<ConfirmationOtp> findByOtpAndUser(String otp, User user);

    Optional<ConfirmationOtp> findByUser(User user);

    Optional<ConfirmationOtp> findByConfirmedAtGreaterThan(LocalDateTime confirmedAt);
    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationOtp c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.otp = ?1")
    int updateConfirmedAt(String token,
                          LocalDateTime confirmedAt);
}