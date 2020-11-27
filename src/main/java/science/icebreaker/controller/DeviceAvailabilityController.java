package science.icebreaker.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import science.icebreaker.data.request.AddDeviceAvailabilityRequest;
import science.icebreaker.data.request.ContactRequest;
import science.icebreaker.data.request.UpdateDeviceAvailabilityRequest;
import science.icebreaker.data.response.GetDeviceAvailabilityResponse;
import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.DeviceAvailability;
import science.icebreaker.exception.AccountNotFoundException;
import science.icebreaker.exception.CaptchaInvalidException;
import science.icebreaker.exception.DeviceAvailabilityCreationException;
import science.icebreaker.exception.DeviceAvailabilityNotFoundException;
import science.icebreaker.exception.MailException;
import science.icebreaker.service.CaptchaService;
import science.icebreaker.validation.controllerValidators.HasFiltersConstraint;
import science.icebreaker.service.MailService;
import science.icebreaker.service.DeviceAvailabilityService;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@RestController
@RequestMapping("/device-availability")
public class DeviceAvailabilityController {

    private DeviceAvailabilityService service;
    private final MailService mailService;
    private final CaptchaService captchaService;

    public DeviceAvailabilityController(
            DeviceAvailabilityService service,
            MailService mailService,
            CaptchaService captchaService) {
        this.service = service;
        this.mailService = mailService;
        this.captchaService = captchaService;
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
            (Account) principal.getPrincipal()
        );
    }

    @GetMapping("/")
    @ApiParam(name = "device")
    @HasFiltersConstraint
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of availability entries"),
        @ApiResponse(code = 400, message = "Filter params provided not valid"),
        @ApiResponse(code = 401, message = "The provided ownerID does not match that of the request sender")
    })
    public ResponseEntity<?> getDeviceAvailability(
        @RequestParam(name = "device", required = false) Integer deviceId,
        @RequestParam(required = false) Integer ownerId
        ) {
        // Temporary workaround to disallow users from accessing other users' device availabilities
        // todo: when the access rights are well defined and implemented, correct this impl.
        boolean ownDevices = false;
        if (ownerId != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            //No role or id is different than the ownerId provided
            if (!(authentication.getPrincipal() instanceof Account)
                || (((Account) authentication.getPrincipal()).getId() != ownerId)) {
                return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
            } else {
                ownDevices = true;
            }
        }
        return new ResponseEntity<>(
                service.getDeviceAvailability(
                        deviceId,
                        ownerId,
                        ownDevices
                )
                .stream()
                .map(GetDeviceAvailabilityResponse::fromEntity)
                .collect(Collectors.toList()),
                HttpStatus.OK
        );
    }

    @PostMapping("/{id}/contact")
    @ApiOperation("Contact the owner of a device.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully sent"),
            @ApiResponse(code = 400, message = "Device availability does not exist")})
    public void sendContactRequest(
            @PathVariable int id,
            @RequestBody @Valid ContactRequest contactRequest,
            @ApiIgnore @Nullable UsernamePasswordAuthenticationToken principal
    ) throws AccountNotFoundException, DeviceAvailabilityNotFoundException, MailException, CaptchaInvalidException {
        if (principal == null || !principal.isAuthenticated()) {
            captchaService.verifyCaptcha(contactRequest.getCaptcha());
        }

        DeviceAvailability deviceAvailability = service.getDeviceAvailability(id);
        mailService.sendContactRequestMail(contactRequest, deviceAvailability.getAccount());
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete device availability")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Entry deleted"),
        @ApiResponse(code = 404, message = "Owned entry not found")
    })
    public void deleteDeviceAvailability(@PathVariable Integer id, Principal principal) {
        Account account = (Account) ((Authentication) principal).getPrincipal();
        service.deleteDeviceAvailability(id, account);
    }

    @PutMapping("/{id}")
    @ApiOperation("Update device availability")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Entry updated"),
        @ApiResponse(code = 404, message = "Owned entry not found")
    })
    public void updateDeviceAvailability(
        @PathVariable Integer id,
        @RequestBody @Valid UpdateDeviceAvailabilityRequest updateData,
        Principal principal
    ) {
        Account account = (Account) ((Authentication) principal).getPrincipal();
        service.updateDeviceAvailability(
            id,
            account,
            updateData.getComment(),
            updateData.getGermanPostalCode(),
            updateData.getInstitution(),
            updateData.getResearchGroup(),
            updateData.isDisabled()
        );
    }
}
