package com.user.user_service.repository;
import com.user.user_service.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Méthodes de base fournies par MongoRepository
}