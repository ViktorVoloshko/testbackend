package com.provedcode.kudos.service;

import com.provedcode.kudos.model.entity.Kudos;
import com.provedcode.kudos.model.response.KudosAmount;
import com.provedcode.kudos.repository.KudosRepository;
import com.provedcode.talent.model.ProofStatus;
import com.provedcode.talent.model.entity.Talent;
import com.provedcode.talent.model.entity.TalentProof;
import com.provedcode.talent.repo.TalentProofRepository;
import com.provedcode.talent.repo.TalentRepository;
import com.provedcode.user.model.entity.UserInfo;
import com.provedcode.user.repo.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
@AllArgsConstructor
@Transactional
public class KudosService {
    KudosRepository kudosRepository;
    TalentRepository talentRepository;
    TalentProofRepository talentProofRepository;
    UserInfoRepository userInfoRepository;

    @Transactional(readOnly = true)
    public KudosAmount getAmountKudosProof(long id, Authentication authentication) {
        TalentProof talentProof = talentProofRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "proof with id = %s not found".formatted(id)));

        if (!talentProof.getStatus().equals(ProofStatus.PUBLISHED)) {
            throw new ResponseStatusException(FORBIDDEN);
        }
        long count = kudosRepository.countByProof_Id(id);
        if (authentication == null) {
            return new KudosAmount(count, false);
        }

        Optional<UserInfo> userInfo = userInfoRepository.findByLogin(authentication.getName());

        if (userInfo.isPresent()) {
            Talent talent = userInfo.get().getTalent();
            if (talent.getCudoses().stream().anyMatch(i -> i.getProof().equals(talentProof))) {
                return new KudosAmount(count, true);
            }
        }
        return new KudosAmount(count, false);
    }

    public void addKudosToProof(long id, Authentication authentication) {
        UserInfo userInfo = userInfoRepository.findByLogin(authentication.getName())
                                              .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                                                                                             "Talent with id = %s not found".formatted(
                                                                                                     id)));

        Talent talent = userInfo.getTalent();

        TalentProof talentProof = talentProofRepository.findById(id)
                                                       .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                                                                                                      "Proof with id = %s not found".formatted(
                                                                                                              id)));

        if (talent.getId().equals(talentProof.getTalent().getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Talent can’t give `kudos` to himself");
        }
        if (kudosRepository.existsByTalentAndProof_Id(talent, id)) {
            throw new ResponseStatusException(CONFLICT, "Talent can give only one `kudos` for one proof");
        }
        if (!talentProof.getStatus().equals(ProofStatus.PUBLISHED)) {
            throw new ResponseStatusException(FORBIDDEN);
        }

        kudosRepository.save(Kudos.builder()
                                  .proof(talentProof)
                                  .talent(talent)
                                  .build());
    }

    public void deleteKudosFromProof(long id, Authentication authentication) {
        UserInfo userInfo = userInfoRepository.findByLogin(authentication.getName())
                                              .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                                                                                             "Talent with id = %s not found".formatted(
                                                                                                     id)));
        Talent talent = userInfo.getTalent();

        TalentProof talentProof = talentProofRepository.findById(id)
                                                       .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                                                                                                      "Proof with id = %s not found".formatted(
                                                                                                              id)));

        if (!talentProof.getStatus().equals(ProofStatus.PUBLISHED)) {
            throw new ResponseStatusException(FORBIDDEN);
        }
        if (!kudosRepository.existsByTalent(talent)) {
            throw new ResponseStatusException(CONFLICT, "kudos don`t exist");
        }

        Kudos kudos = talent.getCudoses().stream().filter(i -> i.getProof().getId().equals(id)).findFirst()
                            .orElseThrow();
        talentProof.getKudos().remove(kudos);
        talent.getCudoses().remove(kudos);

        talentRepository.save(talent);
    }
}