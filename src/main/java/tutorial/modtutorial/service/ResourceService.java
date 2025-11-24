package tutorial.modtutorial.service;

import tutorial.modtutorial.domain.dto.resource.response.ProblemResourceDTO;


public interface ResourceService {
    // ======================== USER ZONE ========================
    ProblemResourceDTO get(String resourceId, String resourceType);
}
