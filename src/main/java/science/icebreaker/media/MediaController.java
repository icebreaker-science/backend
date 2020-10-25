package science.icebreaker.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/internal/media")
public class MediaController {

    private final String redirectHeader;
    private final String redirectURI;

    public MediaController(
        @Value("${icebreaker.files.redirectHeader}") String redirectHeader,
        @Value("${icebreaker.files.redirectURI}") String redirectURI
    ) {
        this.redirectHeader = redirectHeader;
        this.redirectURI = redirectURI;
    }

    @ApiOperation(value = "Redirects internal media requests. only for internal use", hidden = true)
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedia(@PathVariable Integer id) {
        //optional todo: check media entities first for existence
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(
            redirectHeader,
            this.redirectURI + id
        );

        return ResponseEntity.ok()
          .headers(responseHeaders)
          .build();
    }

}
