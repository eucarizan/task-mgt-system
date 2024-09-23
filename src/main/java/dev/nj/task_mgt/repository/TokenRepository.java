package dev.nj.task_mgt.repository;

import dev.nj.task_mgt.entities.AccessToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<AccessToken, Long> {
    Optional<AccessToken> findByToken(String token);
}
