package com.guardedbox.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.guardedbox.constants.Roles;
import com.guardedbox.constants.SessionAttributes;
import com.guardedbox.dto.AccountWithPublicKeyDto;
import com.guardedbox.service.ExecutionTimeService;
import com.guardedbox.service.transactional.AccountsService;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * User Details Service.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Configuration
public class UserDetailsServiceImpl
        implements UserDetailsService {

    /** Execution time of the loadUserByUsername method (ms). */
    private static final long LOAD_USER_BY_USERNAME_EXECUTION_TIME = 500L;

    /** AccountsService. */
    private final AccountsService accountsService;

    /** ExecutionTimeService. */
    private final ExecutionTimeService executionTimeService;

    /** Current Session. */
    private final HttpSession session;

    /**
     * Constructor with Attributes.
     * 
     * @param accountsService AccountsService.
     * @param executionTimeService ExecutionTimeService.
     * @param request Current Request.
     * @param session Current Session.
     */
    public UserDetailsServiceImpl(
            @Autowired AccountsService accountsService,
            @Autowired ExecutionTimeService executionTimeService,
            @Autowired HttpServletRequest request,
            @Autowired HttpSession session) {
        this.accountsService = accountsService;
        this.executionTimeService = executionTimeService;
        this.session = session;
    }

    /**
     * This method is invoked right after the Authentication Filter when a login request is received.
     * 
     * @return UserDetails Object with the authentication details of the introduced username.
     * 
     */
    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        long currentTime = System.currentTimeMillis();
        String error = null;

        // Check if the account exists.
        AccountWithPublicKeyDto accountWithPublicKeyDto = accountsService.getAccountWithPublicKey(username);
        if (accountWithPublicKeyDto == null) {
            error = "Email is not registered";
        }

        if (error == null) {

            // Check if the login challenge is correct.
            String loginChallenge = (String) session.getAttribute(SessionAttributes.LOGIN_CHALLENGE);
            String loginChallengeEmail = (String) session.getAttribute(SessionAttributes.LOGIN_CHALLENGE_EMAIL);
            Long loginChallengeExpiration = (Long) session.getAttribute(SessionAttributes.LOGIN_CHALLENGE_EXPIRATION);

            if (loginChallenge == null) {
                error = "No login challenge has been generated";
            } else if (loginChallengeEmail == null || !loginChallengeEmail.equals(username)) {
                error = "Login email does not match login challenge email";
            } else if (loginChallengeExpiration == null || loginChallengeExpiration < currentTime) {
                error = "Login challenge is expired";
            }

        }

        if (error == null) {

            // Check if the login code is correct.
            String loginCode = (String) session.getAttribute(SessionAttributes.LOGIN_CODE);
            String loginCodeEmail = (String) session.getAttribute(SessionAttributes.LOGIN_CODE_EMAIL);
            Long loginCodeExpiration = (Long) session.getAttribute(SessionAttributes.LOGIN_CODE_EXPIRATION);
            String loginReqCode = (String) session.getAttribute(SessionAttributes.LOGIN_REQUEST_CODE);

            if (loginCode == null) {
                error = "No login code has been generated";
            } else if (loginCodeEmail == null || !loginCodeEmail.equals(username)) {
                error = "Login email does not match login code email";
            } else if (loginCodeExpiration == null || loginCodeExpiration < currentTime) {
                error = "Login code is expired";
            } else if (!loginCode.equals(loginReqCode)) {
                error = "Introduced login code is incorrect";
            }

        }

        // Fix execution time.
        executionTimeService.fix(currentTime, LOAD_USER_BY_USERNAME_EXECUTION_TIME);

        // Unsuccessful result.
        if (error != null)
            throw new UsernameNotFoundException(error);

        // Successful result.
        session.setAttribute(SessionAttributes.ACCOUNT_ID, accountWithPublicKeyDto.getAccountId());
        session.setAttribute(SessionAttributes.ACCOUNT_EMAIL, accountWithPublicKeyDto.getEmail());

        return new User(username, accountWithPublicKeyDto.getPublicKey(), Arrays.asList(Roles.ROLE_ACCOUNT));

    }

}
