package com.joje.untitled.model.entity.auth;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TB_TOKEN")
public class TokenEntity {

    @Id
    private String refreshToken;

    private String accessToken;
    private Long userNo;
    private LocalDateTime createDate;

}
