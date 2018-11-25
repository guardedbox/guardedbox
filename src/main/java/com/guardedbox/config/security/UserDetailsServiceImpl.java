package com.guardedbox.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.guardedbox.constants.Roles;
import com.guardedbox.constants.SessionAttributes;
import com.guardedbox.dto.AccountWithPasswordDto;
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

        // Check if the account exists.
        AccountWithPasswordDto accountWithPasswordDto = accountsService.getAccountWithPassword(username);
        UsernameNotFoundException usernameNotFoundException = null;

        if (accountWithPasswordDto == null) {
            usernameNotFoundException = new UsernameNotFoundException("Email is not registered");
        } else {

            // Check if the login code is correct.
            String loginCode = (String) session.getAttribute(SessionAttributes.LOGIN_CODE);
            String loginCodeEmail = (String) session.getAttribute(SessionAttributes.LOGIN_CODE_EMAIL);
            Long loginCodeExpiration = (Long) session.getAttribute(SessionAttributes.LOGIN_CODE_EXPIRATION);
            String loginReqCode = (String) session.getAttribute(SessionAttributes.LOGIN_REQUEST_CODE);

            if (loginCode == null) {
                usernameNotFoundException = new UsernameNotFoundException("No login code has been generated");
            } else if (loginCodeEmail == null || !loginCodeEmail.equals(username)) {
                usernameNotFoundException = new UsernameNotFoundException("Login email does not match login code email");
            } else if (loginCodeExpiration == null || loginCodeExpiration < currentTime) {
                usernameNotFoundException = new UsernameNotFoundException("Login code is expired");
            } else if (!loginCode.equals(loginReqCode)) {
                usernameNotFoundException = new UsernameNotFoundException("Introduced login code is incorrect");
            }

        }

        // Remove login code session attributes.
        session.removeAttribute(SessionAttributes.LOGIN_CODE);
        session.removeAttribute(SessionAttributes.LOGIN_CODE_EMAIL);
        session.removeAttribute(SessionAttributes.LOGIN_CODE_EXPIRATION);
        session.removeAttribute(SessionAttributes.LOGIN_REQUEST_CODE);

        // Fix execution time.
        executionTimeService.fix(currentTime, LOAD_USER_BY_USERNAME_EXECUTION_TIME);

        // Unsuccessful result.
        if (usernameNotFoundException != null)
            throw usernameNotFoundException;

        // Successful result.
        session.setAttribute(SessionAttributes.ACCOUNT_ID, accountWithPasswordDto.getAccountId());
        session.setAttribute(SessionAttributes.ACCOUNT_EMAIL, accountWithPasswordDto.getEmail());

        return new User(username, accountWithPasswordDto.getPassword(), Arrays.asList(Roles.ROLE_ACCOUNT));

    }

}
