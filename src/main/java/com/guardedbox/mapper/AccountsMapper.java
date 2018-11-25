package com.guardedbox.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardedbox.dto.AccountWithPasswordDto;
import com.guardedbox.dto.NewAccountDto;
import com.guardedbox.entity.AccountFullEntity;
import com.guardedbox.entity.AccountWithPasswordEntity;

import java.io.IOException;
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
    public AccountWithPasswordDto toDtoWithPassword(
            AccountWithPasswordEntity accountEntity) {

        if (accountEntity == null)
            return null;

        AccountWithPasswordDto accountDto = new AccountWithPasswordDto();

        accountDto.setAccountId(accountEntity.getAccountId());
        accountDto.setEmail(accountEntity.getEmail());
        accountDto.setPassword(accountEntity.getPassword());

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
        accountEntity.setPassword(accountDto.getPassword());
        accountEntity.setSecurityQuestions(compactSecurityQuestions(accountDto.getSecurityQuestions()));
        accountEntity.setSecurityAnswers(accountDto.getSecurityAnswers());
        accountEntity.setPublicKey(accountDto.getPublicKey());
        accountEntity.setEncryptedPrivateKey(accountDto.getEncryptedPrivateKey());

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
