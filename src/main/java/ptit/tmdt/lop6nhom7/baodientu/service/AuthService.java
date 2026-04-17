package ptit.tmdt.lop6nhom7.baodientu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ptit.tmdt.lop6nhom7.baodientu.dto.LoginReq;
import ptit.tmdt.lop6nhom7.baodientu.dto.LoginRes;
import ptit.tmdt.lop6nhom7.baodientu.dto.RegisterReq;
import ptit.tmdt.lop6nhom7.baodientu.entity.User;
import ptit.tmdt.lop6nhom7.baodientu.enums.UserRole;
import ptit.tmdt.lop6nhom7.baodientu.enums.UserStatus;
import ptit.tmdt.lop6nhom7.baodientu.exception.ConflictException;
import ptit.tmdt.lop6nhom7.baodientu.exception.ForbiddenException;
import ptit.tmdt.lop6nhom7.baodientu.exception.UnauthorizedException;
import ptit.tmdt.lop6nhom7.baodientu.repository.UserRepo;
import ptit.tmdt.lop6nhom7.baodientu.security.JwtService;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
  private final UserRepo userRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final ObjectMapper objectMapper;
  
  public @Nullable LoginRes login(@Valid LoginReq request) {
    // Find user
    User u = userRepo.findByEmail(request.getEmail())
        .orElseThrow(() -> new UnauthorizedException("Sai ten dang nhap hoac mat khau"));
    // Check status
    if (u.getStatus() == UserStatus.LOCKED) {
      throw new ForbiddenException("Nguoi dung bi khoa tai khoan");
    }
    // Verify password
    if (!passwordEncoder.matches(request.getPassword(), u.getPasswordHash())) {
      throw new UnauthorizedException("Sai ten dang nhap hoac mat khau");
    }
    // Generate JWT
    String jwtToken = jwtService.generateToken(u);
    // return
    return new LoginRes(jwtToken, u.getRole(), u.getFullName(), u.getVipExpiryDate(), u.getFreeArticlesLeft());
  }
  
  public void register(RegisterReq request) {
    //
    if (!request.getPassword().equals(request.getConfirmation())) {
      throw new ConflictException("Passwords do not match");
    }
    // Check duplicate email
    if (userRepo.existsByEmail(request.getEmail())) {
      throw new ConflictException("Email already exists");
    }
    //
    User newUser = new User();
    newUser.setEmail(request.getEmail());
    newUser.setFullName(request.getName());
    newUser.setStatus(UserStatus.ACTIVE);
    newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    newUser.setRole(UserRole.MEMBER);
    newUser.setFreeArticlesLeft(3);
    newUser.setVipExpiryDate(Instant.now());
    newUser.setCreatedAt(Instant.now());
    userRepo.save(newUser);
  }
}
