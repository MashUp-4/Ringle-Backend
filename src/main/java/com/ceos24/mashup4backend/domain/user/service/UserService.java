package com.ceos24.mashup4backend.domain.user.service;

import com.ceos24.mashup4backend.domain.user.entity.User;
import com.ceos24.mashup4backend.domain.user.repository.UserRepository;
import com.ceos24.mashup4backend.global.exception.BusinessException;
import com.ceos24.mashup4backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
