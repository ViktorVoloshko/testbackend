package com.provedcode.sponsor.service;

import com.provedcode.config.PageProperties;
import com.provedcode.sponsor.model.entity.Sponsor;
import com.provedcode.sponsor.repository.SponsorRepository;
import com.provedcode.user.model.entity.UserInfo;
import com.provedcode.user.repo.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
@AllArgsConstructor
public class SponsorService {
    PageProperties pageProperties;
    SponsorRepository sponsorRepository;
    UserInfoRepository userInfoRepository;

    public Page<Sponsor> getAllSponsors(Optional<Integer> page, Optional<Integer> size) {
        if (page.orElse(pageProperties.defaultPageNum()) < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "'page' query parameter must be greater than or equal to 0");
        }
        if (size.orElse(pageProperties.defaultPageSize()) <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "'size' query parameter must be greater than or equal to 1");
        }
        return sponsorRepository.findAll(PageRequest.of(page.orElse(pageProperties.defaultPageNum()),
                                                        size.orElse(pageProperties.defaultPageSize())));
    }

    public Sponsor getSponsorById(long id, Authentication authentication) {
        Sponsor sponsor = sponsorRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, String.format("sponsor with id = %d not found", id)));
        UserInfo user = userInfoRepository.findByLogin(authentication.getName()).orElseThrow();

        if (!sponsor.getId().equals(user.getSponsor().getId()))
            throw new ResponseStatusException(FORBIDDEN, "The user cannot view someone else's profile");
        return sponsor;
    }
}