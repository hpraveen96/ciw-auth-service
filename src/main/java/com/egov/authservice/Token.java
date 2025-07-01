package com.egov.authservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Document(collection = "tokens")
public class Token {
    @Id
    Integer token;
    String phone;
    String status;//ACTIVE or INACTIVE
    Instant creattedAt;
    Integer expiry;
}
