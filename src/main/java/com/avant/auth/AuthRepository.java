package com.avant.auth;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AuthRepository extends MongoRepository<AuthToken, String> {
	
	@Query ("{'timeout' : {$lt : ?0}}")
	List<AuthToken> findTimeouted (Long now);
	
}
