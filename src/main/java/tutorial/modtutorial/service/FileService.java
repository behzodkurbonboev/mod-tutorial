package tutorial.modtutorial.service;

import org.springframework.web.multipart.MultipartFile;


public interface FileService {
    // ======================= ADMIN ZONE =======================
    boolean deleteById(String id);


    // ===================== MODERATOR ZONE =====================
    String save(MultipartFile image);
    void deleteByUrl(String url);


    // ======================== USER ZONE ========================
    byte[] getById(String id);
}
