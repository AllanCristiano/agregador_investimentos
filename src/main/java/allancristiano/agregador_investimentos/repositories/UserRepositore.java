package allancristiano.agregador_investimentos.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import allancristiano.agregador_investimentos.entities.User;

@Repository
public interface UserRepositore extends JpaRepository<User, UUID> {
    
}
