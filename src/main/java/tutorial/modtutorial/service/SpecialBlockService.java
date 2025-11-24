package tutorial.modtutorial.service;

import tutorial.modtutorial.domain.dto.general.response.Slice;
import tutorial.modtutorial.domain.dto.test.request.SpecialBlockDTO;
import tutorial.modtutorial.domain.dto.test.request.SpecialBlockFilter;
import tutorial.modtutorial.domain.dto.test.response.GroupedByDate;
import tutorial.modtutorial.domain.dto.test.response.SpecialBlockProtectedDTO;
import tutorial.modtutorial.domain.dto.test.response.SpecialBlockPublicDTO;
import tutorial.modtutorial.domain.dto.test.response.SpecialTestProtectedDTO;
import tutorial.modtutorial.domain.dto.test.response.SpecialTestPublicDTO;

import java.util.List;
import java.util.Map;


public interface SpecialBlockService {
    // ===================== MODERATOR ZONE =====================
    List<SpecialBlockProtectedDTO> getSpecialBlocksProtected();
    List<SpecialTestProtectedDTO> getSpecialBlock(String blockId);
    void createSpecialBlock(SpecialBlockDTO dto);
    void updateSpecialBlock(String blockId, SpecialBlockDTO dto);
    void changeVisibility(String blockId);
    void transferSpecialBlock(String blockId, String userId);
    default void deleteSpecialBlock(String blockId) {
        throw new RuntimeException("You cannot delete SpecialBlock");
    }


    // ======================== USER ZONE ========================
    Slice<SpecialBlockPublicDTO> getSpecialBlocksPublicSliced(SpecialBlockFilter filter);
    Slice<SpecialBlockPublicDTO> getSpecialBlocksCreatedByUserSliced(SpecialBlockFilter filter);
    Slice<GroupedByDate<SpecialBlockPublicDTO>> getSpecialBlocksPublic(SpecialBlockFilter filter);
    Slice<GroupedByDate<SpecialBlockPublicDTO>> getSpecialBlocksCreatedByUser(SpecialBlockFilter filter);
    List<SpecialTestPublicDTO> startSpecialBlock(String blockId);
    Map<Integer, String> finishSpecialBlock(String blockId, Map<Integer, Integer> key);
    List<SpecialTestPublicDTO> analiseSpecialBlock(String blockId);
}
