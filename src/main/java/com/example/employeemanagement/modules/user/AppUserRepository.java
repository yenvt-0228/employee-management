package com.example.employeemanagement.modules.user;

import com.example.employeemanagement.common.repository.BaseRepository;

import java.util.Optional;

public interface AppUserRepository extends BaseRepository<AppUser> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
