package com.ceos24.mashup4backend.domain.user.repository;

import com.ceos24.mashup4backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
