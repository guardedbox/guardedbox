package com.guardedbox.service.transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.CreateAccountDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.InvitationPendingActionEntity;
import com.guardedbox.entity.RegistrationEntity;
import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.entity.projection.AccountLoginPublicKeyProjection;
import com.guardedbox.entity.projection.AccountLoginSaltProjection;
import com.guardedbox.entity.projection.AccountPublicKeysProjection;
import com.guardedbox.entity.projection.AccountPublicKeysSaltsProjection;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.properties.CryptographyProperties;
import com.guardedbox.repository.AccountsRepository;
import com.guardedbox.repository.InvitationPendingActionsRepository;
import com.guardedbox.repository.RegistrationsRepository;
import com.guardedbox.service.HiddenDerivationService;
import com.guardedbox.service.MessagesService;

import lombok.RequiredArgsConstructor;

/**
 * Service: Account.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AccountsService {

    /** CryptographyProperties. */
    private final CryptographyProperties cryptographyProperties;

    /** AccountsRepository. */
    private final AccountsRepository accountsRepository;

    /** RegistrationsRepository. */
    private final RegistrationsRepository registrationsRepository;

    /** InvitationPendingActionsRepository. */
    private final InvitationPendingActionsRepository invitationPendingActionsRepository;

    /** AccountsMapper. */
    private final AccountsMapper accountsMapper;

    /** RegistrationsService. */
    private final RegistrationsService registrationsService;

    /** HiddenDerivationService. */
    private final HiddenDerivationService hiddenDerivationService;

    /** MessagesService. */
    private final MessagesService messagesService;

    /**
     * @param email Account.email.
     * @return The AccountDto corresponding to the introduced email. Checks if it exists.
     */
    public AccountDto getAndCheckAccountByEmail(
            String email) {

        return accountsMapper.toDto(findAndCheckAccountByEmail(email));

    }

    /**
     * @param email Account.email.
     * @return The AccountDto corresponding to the introduced email. Checks if it exists.
     */
    public AccountDto getAndCheckAccountLoginSaltByEmail(
            String email) {

        AccountDto account = accountsMapper.toDto(accountsRepository.findLoginSaltByEmail(email));

        if (account == null) {
            return new AccountDto()
                    .setEmail(email)
                    .setLoginSalt(hiddenDerivationService.deriveBase64(email, cryptographyProperties.getCryptographyLength()));
        }

        return account;

    }

    /**
     * @param email Account.email.
     * @return The AccountDto corresponding to the introduced email. Checks if it exists.
     */
    public AccountDto getAndCheckAccountLoginPublicKeyByEmail(
            String email) {

        return accountsMapper.toDto(findAndCheckAccountByEmail(email, AccountLoginPublicKeyProjection.class));

    }

    /**
     * @param accountId Account.accountId.
     * @return The AccountDto corresponding to the introduced accountId. Checks if it exists.
     */
    public AccountDto getAndCheckAccountPublicKeysSaltsByAccountId(
            UUID accountId) {

        AccountPublicKeysSaltsProjection account = accountsRepository.findPublicKeysSaltsByAccountId(accountId);

        if (account == null) {
            throw new ServiceException(String.format("Account %s does not exist", accountId));
        }

        return accountsMapper.toDto(account);

    }

    /**
     * @param email Account.email.
     * @return The AccountDto corresponding to the introduced email. Checks if it exists.
     */
    public AccountDto getAndCheckAccountPublicKeysByEmail(
            String email) {

        return accountsMapper.toDto(findAndCheckAccountByEmail(email, AccountPublicKeysProjection.class));

    }

    /**
     * Creates an Account.
     *
     * @param createAccountDto CreateAccountDto with the new Account data.
     * @return AccountDto with the new Account data.
     */
    public AccountDto createAccount(
            CreateAccountDto createAccountDto) {

        long currentTime = System.currentTimeMillis();

        // Get the registration, checking if it exists, is valid and is not expired.
        RegistrationEntity registrationEntity = registrationsService.findAndCheckRegistrationByToken(createAccountDto.getRegistrationToken());
        String email = registrationEntity.getEmail();
        createAccountDto.setEmail(email);

        // Check if the email is already registered.
        if (accountsRepository.existsByEmail(email)) {
            throw new ServiceException(
                    String.format("Account was not created since email %s is already registered", email))
                            .setErrorCode("accounts.email-already-registered").addAdditionalData("email", email);
        }

        // Check if the any of the salts is repeated or already exist.
        List<String> salts = Arrays.asList(createAccountDto.getLoginSalt(), createAccountDto.getEncryptionSalt(), createAccountDto.getSigningSalt());
        if (new HashSet<>(salts).size() != salts.size()) {
            throw new ServiceException("Account was not created since one of the received salts is repeated");
        }
        if (accountsRepository.existsByLoginSaltInOrEncryptionSaltInOrSigningSaltIn(salts, salts, salts)) {
            throw new ServiceException("Account was not created since one of the received salts already exists");
        }

        // Create the new account.
        AccountEntity accountEntity = accountsRepository.save(
                accountsMapper.fromDto(createAccountDto)
                        .setCreationTime(new Timestamp(currentTime)));

        // Mark the registration as consumed.
        registrationsRepository.save(registrationEntity.setConsumed(true));

        // Set all the registrations associated to the email as invalid and set their account.
        List<RegistrationEntity> registrations = registrationsRepository.findByEmail(email);
        for (RegistrationEntity registration : registrations) {
            registrationsRepository.save(registration.setValid(false).setAccount(accountEntity));
        }

        // Send the registration completed email.
        messagesService.sendRegistrationCompleteMessage(email);

        // Send the invitation completed emails.
        Set<String> invitersEmails = new HashSet<>();
        for (RegistrationEntity registration : registrationsRepository.findByEmail(email)) {
            if (!StringUtils.isEmpty(registration.getFromEmail()))
                invitersEmails.add(registration.getFromEmail());
        }
        for (InvitationPendingActionEntity invitationPendingAction : invitationPendingActionsRepository.findByReceiverEmail(email)) {
            invitersEmails.add(invitationPendingAction.getFromAccount(AccountBaseProjection.class).getEmail());
        }
        for (String inviterEmail : invitersEmails) {
            messagesService.sendInvitationCompleteMessage(inviterEmail, email);
        }

        return accountsMapper.toDto(accountEntity);

    }

    /**
     * Deletes an Account.
     *
     * @param accountId Account.accountId of the Account to be deleted.
     */
    public void deleteAccount(
            UUID accountId) {

        accountsRepository.deleteById(accountId);

    }

    /**
     * Finds an AccountEntity by email and checks if it exists.
     *
     * @param email The email.
     * @return The AccountEntity.
     */
    protected AccountEntity findAndCheckAccountByEmail(
            String email) {

        AccountEntity account = accountsRepository.findByEmail(email);

        if (account == null) {
            throw new ServiceException(
                    String.format("Email %s is not registered", email))
                            .setErrorCode("accounts.email-not-registered").addAdditionalData("email", email);
        }

        return account;

    }

    /**
     * Finds an Account Projection by email and checks if it exists.
     *
     * @param <T> The projection type.
     * @param email The email.
     * @param type The class of the projection.
     * @return The Account Projection.
     */
    protected <T extends AccountBaseProjection> T findAndCheckAccountByEmail(
            String email,
            Class<T> type) {

        AccountBaseProjection account = null;

        if (AccountLoginSaltProjection.class.equals(type)) {

            account = accountsRepository.findLoginSaltByEmail(email);

        } else if (AccountLoginPublicKeyProjection.class.equals(type)) {

            account = accountsRepository.findLoginPublicKeyByEmail(email);

        } else if (AccountPublicKeysSaltsProjection.class.equals(type)) {

            account = accountsRepository.findPublicKeysSaltsByEmail(email);

        } else if (AccountPublicKeysProjection.class.equals(type)) {

            account = accountsRepository.findPublicKeysByEmail(email);

        } else if (AccountBaseProjection.class.equals(type)) {

            account = accountsRepository.findBaseByEmail(email);

        } else {

            throw new IllegalArgumentException("Type must extend AccountBaseProjection");

        }

        if (account == null) {
            throw new ServiceException(
                    String.format("Email %s is not registered", email))
                            .setErrorCode("accounts.email-not-registered").addAdditionalData("email", email);
        }

        return type.cast(account);

    }

}
