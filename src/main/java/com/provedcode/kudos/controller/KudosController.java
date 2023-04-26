package com.provedcode.kudos.controller;

import com.provedcode.kudos.model.response.KudosAmountWithSponsor;
import com.provedcode.kudos.service.KudosService;
import com.provedcode.kudos.model.response.KudosAmount;
import com.provedcode.util.annotations.doc.controller.kudos.GetAmountOfKudosApiDoc;
import com.provedcode.util.annotations.doc.controller.kudos.GetKudosForSponsorApiDoc;
import com.provedcode.util.annotations.doc.controller.kudos.PostAddKudosToProofApiDoc;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v3/")
public class KudosController {
    KudosService kudosService;

    @GetKudosForSponsorApiDoc
    @PreAuthorize("hasRole('SPONSOR')")
    @GetMapping("/sponsors/{sponsor-id}/kudos")
    KudosAmount getKudosForSponsor(@PathVariable("sponsor-id") long id, Authentication authentication) {
        return kudosService.getKudosForSponsor(id, authentication);
    }

    @PostAddKudosToProofApiDoc
    @PreAuthorize("hasRole('SPONSOR')")
    @PostMapping("/proofs/{proof-id}/kudos/{amount}")
    void addKudosToProof(@PathVariable("proof-id") long id,
                         @PathVariable("amount") Long amount,
                         Authentication authentication) {
        kudosService.addKudosToProof(id, amount, authentication);
    }

    @GetAmountOfKudosApiDoc
    @PreAuthorize("hasRole('TALENT')")
    @GetMapping("/proofs/{proof-id}/kudos")
    KudosAmountWithSponsor getProofKudos(@PathVariable("proof-id") long id, Authentication authentication) {
        return kudosService.getProofKudos(id, authentication);
    }
}