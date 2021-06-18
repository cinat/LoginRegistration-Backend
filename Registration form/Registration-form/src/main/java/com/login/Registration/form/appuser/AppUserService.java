package com.login.Registration.form.appuser;

import com.login.Registration.form.registration.token.ConfirmationToken;
import com.login.Registration.form.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private static final String USER_NOT_FOUND_MSG="user with email %s not found";
   private final AppUserRepository appUserRepository;
   private final BCryptPasswordEncoder bCryptPasswordEncoder;
   private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findUserByEmail(email).orElseThrow(()->new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,email)));
    }
    public String singUpUser(AppUser appUser)
    {
       boolean userExists= appUserRepository.findUserByEmail(appUser.getEmail()).isPresent();
       if(userExists)
       {
           throw new IllegalStateException("Email is already taken");
       }
       String encodedPassword=
       bCryptPasswordEncoder.encode(appUser.getPassword());
       appUser.setPassword(encodedPassword);
       appUserRepository.save(appUser);
       String token = UUID.randomUUID().toString();
       //TODO: send confirmation token
        ConfirmationToken confirmationToken = new ConfirmationToken( token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser

        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        //TODO : send email
        return token;
    }
    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
}
