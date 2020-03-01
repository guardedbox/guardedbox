package com.guardedbox.constants;

import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/**
 * Security Parameters.
 *
 * @author s3curitybug@gmail.com
 *
 */
public final class SecurityParameters {

    /** Signature Algorithm. */
    public static final String SIGNATURE_ALGORITHM = "Ed25519";

    /** Signature Algorithm Identifier. */
    public static final AlgorithmIdentifier SIGNATURE_ALGORITHM_ID = new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519);

    /** Bcrypt Rounds. */
    public static final int BCRYPT_ROUNDS = 10;

    /** Registration Token Length (number of alphanumeric characters). */
    public static final int REGISTRATION_TOKEN_LENGTH = 86;

    /** Challenge Length (number of bytes). */
    public static final int CHALLENGE_LENGTH = 64;

    /** One Time Password Length (number of alphanumeric characters). */
    public static final int OTP_LENGTH = 10;

}
