package com.egov.authservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class MainRestController {

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    TokenRepository tokenRepository;

    @PostMapping("signup")
    public ResponseEntity<String> signup(@RequestBody Credential credential)
    {
        credentialRepository.save(credential);
        return ResponseEntity.ok("Credential saved successfully!");
    }

    @PostMapping("login")
    public ResponseEntity<String> signup(@RequestBody CredentialLoginView credentialLoginView)
    {
        Credential credential = credentialRepository.findByPhone(credentialLoginView.getPhone());
        if(credential.getPassword().equals(credentialLoginView.getPassword())){
            int tokenVal = new Random().nextInt(1000000000);

            Token token = new Token();
            token.setToken(tokenVal);
            token.setPhone(credentialLoginView.getPhone());
            token.setStatus("ACTIVE");
            token.setCreattedAt(Instant.now());
            token.setExpiry(600);
            tokenRepository.save(token);

            return ResponseEntity.ok().header(Integer.toString(tokenVal)).body("Login Successfully!");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password!");

    }

    @GetMapping("validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") Integer tokenVal)
    {
        Optional<Token> token = tokenRepository.findById(tokenVal);
        if(token.isPresent()){
            if(token.get().getStatus().equals("ACTIVE")){
                if(Instant.now().getEpochSecond() - token.get().getCreattedAt().getEpochSecond() > token.get().getExpiry()){
                    token.get().setStatus("INACTIVE");
                    tokenRepository.save(token.get());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Expired!");
                }
                return ResponseEntity.ok(token.get().getPhone());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is Inactive!");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is InValid!");

    }

    @PostMapping("logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") Integer tokenVal) {
        Optional<Token> token = tokenRepository.findById(tokenVal);
        if(token.isPresent()) {
            token.get().setStatus("INACTIVE");
            tokenRepository.save(token.get());
            return ResponseEntity.ok("Logout Successfully!");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is InValid!");



    }


}
