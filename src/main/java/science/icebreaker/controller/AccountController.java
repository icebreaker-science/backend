package science.icebreaker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountProfile;
import science.icebreaker.service.AccountService;
import science.icebreaker.service.CaptchaService;
import science.icebreaker.data.request.ForgotPasswordRequest;
import science.icebreaker.data.request.RegistrationRequest;
import science.icebreaker.data.request.ResetPasswordRequest;
import science.icebreaker.exception.AccountCreationException;
import science.icebreaker.exception.AccountNotFoundException;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService service;
    private final CaptchaService captchaService;

    public AccountController(AccountService service, CaptchaService captchaService) {
        this.service = service;
        this.captchaService = captchaService;
    }


    @PostMapping("/register")
    public int register(@RequestBody RegistrationRequest registrationRequest)
    throws AccountCreationException {
        return service.createAccount(registrationRequest);
    }


    @PostMapping("/login")
    public String login(@RequestBody Account account) {
        return service.login(account);
    }


    @GetMapping("/my-profile")
    public AccountProfile getMyProfile(Principal principal) throws AccountNotFoundException {
        Account account = (Account) ((Authentication) principal).getPrincipal();
        return service.getAccountProfile(account.getId());
    }

    @PostMapping("/validate-email")
    public ResponseEntity<Object> confirmAccount(
        @RequestParam(name = "key") String confirmationToken) {
        service.confirmAccount(confirmationToken);

        Map<String, String> body = new HashMap<>();
        body.put("message", "Account verified");

        return ResponseEntity.ok(body);
    }

    @PostMapping("/resend-confirmation-email")
    public ResponseEntity<Object> resendConfirmationEmail(@RequestParam String email) {
        service.resendConfirmationEmail(email);

        Map<String, String> body = new HashMap<>();
        body.put("message", "Account confirmation email sent");

        return ResponseEntity.ok(body);
    }

    @PutMapping("/forgot-password")
    @ApiOperation("Sends a reset password email to the registered email")
    @ApiResponse(code = 200, message = "Request successfully sent or account does not exist")
    public void forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        captchaService.verifyCaptcha(request.getCaptcha());

        // ignore if account is not found
        // as this should not be exposed at the controller level
        try {
            this.service.sendPasswordResetRequest(request.getEmail());
        } catch (AccountNotFoundException exception) { }
    }

    @PostMapping("/reset-password")
    @ApiOperation("Resets the password of the account associated with the given token")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Password changed"),
        @ApiResponse(code = 404, message = "Token not found")
    })
    public void resetPassword(
        @RequestBody @Valid ResetPasswordRequest resetPasswordRequest
    ) {
        this.service.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getPassword());
    }
}
