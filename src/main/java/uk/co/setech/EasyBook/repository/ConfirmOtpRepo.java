package uk.co.setech.EasyBook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.co.setech.EasyBook.model.ConfirmOtp;
import uk.co.setech.EasyBook.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmOtpRepo
        extends JpaRepository<ConfirmOtp, Long> {

    Optional<ConfirmOtp> findByOtpAndUser(String otp, User user);

    Optional<ConfirmOtp> findByUser(User user);

    Optional<ConfirmOtp> findByConfirmedAtGreaterThan(LocalDateTime confirmedAt);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmOtp c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.otp = ?1")
    int updateConfirmedAt(String token,
                          LocalDateTime confirmedAt);
}
