package science.icebreaker.device_availability;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import science.icebreaker.account.Account;
import science.icebreaker.device_availability.ControllerValidators.HasFiltersConstraint;
import science.icebreaker.exception.DeviceAvailabilityCreationException;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@RestController
@RequestMapping("/device-availability")
public class DeviceAvailabilityController {

    private DeviceAvailabilityService service;

    public DeviceAvailabilityController(DeviceAvailabilityService service) {
        this.service = service;
    }

    @PostMapping("/")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Availability entry added"),
        @ApiResponse(code = 422, message = "Device with specified ID not found")
    })
    public void addDeviceAvailability(
        @RequestBody @Valid AddDeviceAvailabilityRequest addDeviceAvailabilityRequest,
        @ApiIgnore UsernamePasswordAuthenticationToken principal
    ) throws DeviceAvailabilityCreationException {
        service.addDeviceAvailability(
            addDeviceAvailabilityRequest.getDeviceId(),
            addDeviceAvailabilityRequest.getComment(),
            addDeviceAvailabilityRequest.getGermanPostalCode(),
            addDeviceAvailabilityRequest.getInstitution(),
            addDeviceAvailabilityRequest.getResearchGroup(),
            (Account)principal.getPrincipal()
        );
    }

    @GetMapping("/")
    @ApiParam(name="device")
    @HasFiltersConstraint
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of availability entries"),
        @ApiResponse(code = 400, message = "Filter params provided not valid"),
        @ApiResponse(code = 401, message = "The provided ownerID does not match that of the request sender")
    })
    public ResponseEntity<?> getDeviceAvailability(
        @RequestParam(name="device", required=false) Integer deviceId,
        @RequestParam(required=false) Integer ownerId
        ) {
        /**
         * Temporary workaround to disallow users from accessing other users' device availabilities
         * TODO: when the access rights are well defined and implemented, correct this impl.
         */
        if (ownerId != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            //No role or id is different than the ownerId provided
            if (!(authentication.getPrincipal() instanceof Account) ||
                    (((Account) authentication.getPrincipal()).getId() != ownerId))
                return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(
                service.getDeviceAvailability(
                        deviceId,
                        ownerId
                )
                        .stream()
                        .map(GetDeviceAvailabilityResponse::fromEntity)
                        .collect(Collectors.toList()),
                HttpStatus.OK
        );
    }
}
