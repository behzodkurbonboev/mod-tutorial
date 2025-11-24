package tutorial.modtutorial.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tutorial.modtutorial.constant.FilesUrls;
import tutorial.modtutorial.service.FileService;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


@Service
public class FileServiceImpl implements FileService {
    private static final String BUCKET_NAME = "bucked-de263e57-d68c-444c-8eaa-32634db544f7-name";

    private final MinioClient client;

    public FileServiceImpl(MinioClient client) {
        this.client = client;
    }


    // ======================= ADMIN ZONE =======================
    @Override
    public boolean deleteById(String id) {
        try {
            client.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket(BUCKET_NAME)
                            .object(id)
                            .build());

            return true;
        } catch (Throwable th) {
            throw new EntityNotFoundException("File with id = '" + id + "' not found");
        }
    }


    // ===================== MODERATOR ZONE =====================
    @Override
    public String save(MultipartFile file) {
        try (InputStream stream = new BufferedInputStream(file.getInputStream())) {
            if (!isBucketExists()) {
                client.makeBucket(
                        MakeBucketArgs
                                .builder()
                                .bucket(BUCKET_NAME)
                                .build()
                );
            }

            String fileName = UUID.randomUUID().toString();
            client.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(BUCKET_NAME)
                            .object(fileName)
                            .stream(stream, -1, 80000000)
                            .build()
            );

            return FilesUrls.FILES_GET_URL.concat(fileName);
        } catch (Throwable th) {
            throw new IllegalArgumentException("There was an error during file storing");
        }
    }

    @Override
    public void deleteByUrl(String url) {
        String fileId = url.replace(FilesUrls.FILES_GET_URL, "");
        deleteById(fileId);
    }


    // ======================== USER ZONE ========================
    @Override
    public byte[] getById(String id) {
        try (
                InputStream stream = client.getObject(
                        GetObjectArgs
                                .builder()
                                .bucket(BUCKET_NAME)
                                .object(id)
                                .build());

                ByteArrayOutputStream result = new ByteArrayOutputStream()
        ) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = stream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toByteArray();
        } catch (Throwable th) {
            throw new EntityNotFoundException("File with id = '" + id + "' not found");
        }
    }

    private boolean isBucketExists() throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
        return client.bucketExists(
                BucketExistsArgs
                        .builder()
                        .bucket(BUCKET_NAME)
                        .build()
        );
    }
}
