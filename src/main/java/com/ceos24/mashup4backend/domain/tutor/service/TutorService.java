package com.ceos24.mashup4backend.domain.tutor.service;

import com.ceos24.mashup4backend.domain.tutor.entity.Tutor;
import com.ceos24.mashup4backend.domain.tutor.repository.TutorRepository;
import com.ceos24.mashup4backend.global.exception.BusinessException;
import com.ceos24.mashup4backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TutorService {

    private final TutorRepository tutorRepository;

    public Tutor getTutor(Long tutorId) {
        return tutorRepository.findById(tutorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TUTOR_NOT_FOUND));
    }
}
