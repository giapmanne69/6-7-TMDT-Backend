package ptit.tmdt.lop6nhom7.baodientu.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptit.tmdt.lop6nhom7.baodientu.entity.User;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(@NotBlank(message = "Email khong duoc bo trong") @Email String email);
  
  boolean existsByEmail(@NotBlank(message = "Email khong duoc bo trong") @Email String email);
}
