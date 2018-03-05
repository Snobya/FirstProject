package com.avant.auth;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {

	@Query ("{'mail' : ?0, 'pass' : ?1}")
	User findByCredAndPass (String cred, String pass);
	
}
