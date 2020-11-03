package science.icebreaker.media;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import science.icebreaker.config.FileStorageConfigurationProperties;

@RestController
@RequestMapping("/internal/media")
public class MediaController {

    private FileStorageConfigurationProperties fileStorageProps;

    public MediaController(
        FileStorageConfigurationProperties fileStorageProps
    ) {
        this.fileStorageProps = fileStorageProps;
    }

    @ApiOperation(value = "Redirects internal media requests. only for internal use", hidden = true)
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedia(@PathVariable Integer id) {
        //optional todo: check media entities first for existence
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(
            fileStorageProps.getRedirectHeader(),
            fileStorageProps.getRedirectURI() + id
        );

        return ResponseEntity.ok()
          .headers(responseHeaders)
          .build();
    }

}
