package science.icebreaker.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "icebreaker.files")
public class FileStorageConfigurationProperties {
    /**
     * The directory on the file system
     * in which the files are stores
     */
    private String directory;

    /**
     * A list of file mime types allowed
     */
    //@Value("#{'${icebreaker.files.allowedTypes}'.split(',')}") List<String> allowedTypes,
    private List<String> allowedTypes;

    /**
     * The name of the header used for internal
     * redirection of file get requests
     */
    private String redirectHeader;

    /**
     * The URI of the resouce used to server files
     */
    private String redirectURI;



    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public List<String> getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(List<String> allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public String getRedirectHeader() {
        return redirectHeader;
    }

    public void setRedirectHeader(String redirectHeader) {
        this.redirectHeader = redirectHeader;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(String redirectURI) {
        this.redirectURI = redirectURI;
    }

}
