package com.provedcode.sponsor.service;

import com.provedcode.config.PageProperties;
import com.provedcode.sponsor.model.entity.Sponsor;
import com.provedcode.sponsor.repository.SponsorRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@AllArgsConstructor
public class SponsorService {
    PageProperties pageProperties;
    SponsorRepository sponsorRepository;

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

    public Sponsor getSponsorById(long id) {
        return sponsorRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, String.format("sponsor with id = %d not found", id)));
    }
}