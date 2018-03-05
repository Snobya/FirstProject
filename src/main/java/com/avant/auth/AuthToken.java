package com.avant.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AuthToken {

	@Id
	private String token;
	@DBRef
	private User user;
	private long timeout;

	public AuthToken(User user) {
		this.user = user;
		this.token = UUID.randomUUID().toString();
		this.timeout = System.currentTimeMillis() + 7_200_000L;
	}
}