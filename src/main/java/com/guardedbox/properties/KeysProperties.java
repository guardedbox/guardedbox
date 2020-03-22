package com.guardedbox.properties;

import java.util.HashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Properties starting by keys.
 *
 * @author s3curitybug@gmail.com
 *
 */
@ConfigurationProperties(prefix = "keys")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class KeysProperties {

    /** Property Map: keys.hidden-derivation. */
    private final HashMap<Integer, String> hiddenDerivation;

}
