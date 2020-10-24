package science.icebreaker.media;

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

    private final String redirectHeader = "X-Accel-Redirect";

    private MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @ApiOperation(value = "Redirects internal media queries. only for internal use", hidden = true)
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedia(@PathVariable Integer id) {
        //optional todo: check media entities first for existence
        String fileLocation = this.mediaService.getMediaLocation(id.toString()).toString();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(
            redirectHeader,
            fileLocation
        );

        return ResponseEntity.ok()
          .headers(responseHeaders)
          .build();
    }

}
