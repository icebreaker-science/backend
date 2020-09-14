package science.icebreaker.device_availability;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import science.icebreaker.account.Account;
import science.icebreaker.account.AccountNotFoundException;
import science.icebreaker.device_availability.ControllerValidators.HasFiltersConstraint;
import science.icebreaker.device_availability.Exceptions.DeviceAvailabilityCreationException;
import science.icebreaker.device_availability.Exceptions.DeviceAvailabilityNotFoundException;
import science.icebreaker.device_availability.Exceptions.InvalidFiltersException;
import science.icebreaker.mail.MailException;
import science.icebreaker.mail.MailService;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/device-availability")
public class DeviceAvailabilityController {

    private DeviceAvailabilityService service;
    private final MailService mailService;

    public DeviceAvailabilityController(DeviceAvailabilityService service, MailService mailService) {
        this.service = service;
        this.mailService = mailService;
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
            if(ownerId != null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                //No role or id is different than the ownerId provided
                if(!(authentication.getPrincipal() instanceof Account) ||
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

    /**
     * Handles the custom validation error {@link InvalidFiltersException}
     * @param ex
     * @return The response object
     */
    @ExceptionHandler
    public ResponseEntity<String> handleException(InvalidFiltersException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/{id}/contact")
    @ApiOperation("Contact the owner of a device.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully sent"),
            @ApiResponse(code = 400, message = "Device availability does not exist")})
    public void sendContactRequest(
            @PathVariable int id,
            @RequestBody @Valid ContactRequest contactRequest
    ) throws AccountNotFoundException, DeviceAvailabilityNotFoundException, MailException {
        DeviceAvailability deviceAvailability = service.getDeviceAvailability(id);
        mailService.sendContactRequestMail(contactRequest, deviceAvailability.getAccount());
    }
}
