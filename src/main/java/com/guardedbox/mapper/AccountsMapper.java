package com.guardedbox.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardedbox.dto.AccountWithEntropyExpanderDto;
import com.guardedbox.dto.AccountWithPublicKeyDto;
import com.guardedbox.dto.AccountWithSecretsDto;
import com.guardedbox.dto.NewAccountDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.AccountFullEntity;
import com.guardedbox.entity.AccountWithEntropyExpanderEntity;
import com.guardedbox.entity.AccountWithPublicKeyEntity;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Mapper: Account.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class AccountsMapper {

    /** ObjectMapper. */
    private final ObjectMapper objectMapper;

    /**
     * Constructor with Attributes.
     * 
     * @param objectMapper ObjectMapper.
     */
    public AccountsMapper(
            @Autowired ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Maps an Account Entity to DTO.
     * 
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountWithEntropyExpanderDto toDtoWithEntropyExpander(
            AccountWithEntropyExpanderEntity accountEntity) {

        if (accountEntity == null)
            return null;

        AccountWithEntropyExpanderDto accountDto = new AccountWithEntropyExpanderDto();

        accountDto.setAccountId(accountEntity.getAccountId());
        accountDto.setEmail(accountEntity.getEmail());
        accountDto.setEntropyExpander(accountEntity.getEntropyExpander());

        return accountDto;

    }

    /**
     * Maps an Account Entity to DTO.
     * 
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountWithPublicKeyDto toDtoWithPublicKey(
            AccountWithPublicKeyEntity accountEntity) {

        if (accountEntity == null)
            return null;

        AccountWithPublicKeyDto accountDto = new AccountWithPublicKeyDto();

        accountDto.setAccountId(accountEntity.getAccountId());
        accountDto.setEmail(accountEntity.getEmail());
        accountDto.setPublicKey(accountEntity.getPublicKey());

        return accountDto;

    }

    /**
     * Maps an Account Entity to DTO.
     * 
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountWithSecretsDto toDtoWithSecrets(
            AccountEntity accountEntity) {

        if (accountEntity == null)
            return null;

        AccountWithSecretsDto accountDto = new AccountWithSecretsDto();

        accountDto.setAccountId(accountEntity.getAccountId());
        accountDto.setEmail(accountEntity.getEmail());
        accountDto.setSecrets(new LinkedList<>());

        return accountDto;

    }

    /**
     * Maps an Account DTO to Entity.
     * 
     * @param accountDto The Account DTO.
     * @return The Account Entity.
     */
    public AccountFullEntity fromDto(
            NewAccountDto accountDto) {

        if (accountDto == null)
            return null;

        AccountFullEntity accountEntity = new AccountFullEntity();

        accountEntity.setEmail(accountDto.getEmail());
        accountEntity.setEntropyExpander(accountDto.getEntropyExpander());
        accountEntity.setPublicKey(accountDto.getPublicKey());
        accountEntity.setSecurityQuestions(compactSecurityQuestions(accountDto.getSecurityQuestions()));
        accountEntity.setEncryptedPrivateKey(accountDto.getEncryptedPrivateKey());
        accountEntity.setPublicKeyFromSecurityAnswers(accountDto.getPublicKeyFromSecurityAnswers());

        return accountEntity;

    }

    /**
     * Transforms a List of security questions into a String representing them.
     * 
     * @param securityQuestionsList The List of security questions.
     * @return The String representing the List of security questions.
     */
    public String compactSecurityQuestions(
            List<String> securityQuestionsList) {

        try {
            return objectMapper.writeValueAsString(securityQuestionsList);
        } catch (JsonProcessingException e) {
            return null;
        }

    }

    /**
     * Transforms a String representing security questions into a List of them.
     * 
     * @param securityQuestionsString The String representing security questions.
     * @return The List of security questions.
     */
    public List<String> parseSecurityQuestions(
            String securityQuestionsString) {

        try {
            return objectMapper.readValue(securityQuestionsString, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            return null;
        }

    }

}
