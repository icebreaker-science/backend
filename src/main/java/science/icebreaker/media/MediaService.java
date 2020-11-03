package science.icebreaker.media;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.nio.file.Files;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import science.icebreaker.config.FileStorageConfigurationProperties;
import science.icebreaker.exception.ErrorCodeEnum;
import science.icebreaker.exception.StorageException;

@Service
public class MediaService {
    private MediaRepository repository;
    private FileStorageConfigurationProperties fileStorageProps;

    public MediaService(
        FileStorageConfigurationProperties fileStorageProps,
        MediaRepository repository
    ) {
        this.fileStorageProps = fileStorageProps;
        this.repository = repository;
    }

    /**
     * Returns the directory of a given media by its identifier
     * @param fileName the filename/identifier of the media
     * @return The Path to the media file
     */
    public Path getMediaLocation(String fileName) {
        return Paths
            .get(this.fileStorageProps.getDirectory()
                + File.separator
                + StringUtils.cleanPath(fileName)
            );
    }

    /**
     * Stores a media file on disk
     * @param file the file to store
     * @param name the filename/identifier to store it under
     */
    private void saveFile(MultipartFile file, String name) {
        try {
            Path fileLocation = getMediaLocation(name);
            Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException()
                .withErrorCode(ErrorCodeEnum.ERR_STRG_001);
        }
    }

    /**
     * Adds a media object to the database and stores the corresponding
     * file on disk
     * @param file the file to store
     * @param fileAllowedTypes the allowed types for the media
     * @return the added media entity
     */
    public Media addMedia(MultipartFile file, List<String> fileAllowedTypes) {
        String mimeType = file.getContentType();

        // Global allowed types or optional selected allowedTypes
        if (!this.fileStorageProps.getAllowedTypes().contains(mimeType)
            || (fileAllowedTypes != null && fileAllowedTypes.contains(mimeType))) {
            throw new StorageException()
                .withErrorCode(ErrorCodeEnum.ERR_STRG_002);
        }

        Media media = this.repository.save(new Media(mimeType, file.getName()));
        saveFile(file, media.getId().toString());
        return media;
    }

    /**
     * Adds a media object to the database and stores the corresponding
     * file on disk
     * @param file the file to store
     * @return the added media entity
     */
    public Media addMedia(MultipartFile file) {
        return this.addMedia(file, null);
    }
}
