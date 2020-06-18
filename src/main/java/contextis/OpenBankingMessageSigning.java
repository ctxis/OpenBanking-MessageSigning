package contextis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwx.HeaderParameterNames;
import org.jose4j.lang.JoseException;

import java.security.PrivateKey;
import java.security.PublicKey;

public class OpenBankingMessageSigning {

    public String doSign(String payload, String alg, PrivateKey privateKey, String kid, String[] critValues, String iss,
                         String tan, String cty, String typ) {
        JsonWebSignature signerJws = new JsonWebSignature();

        // signing settings
        signerJws.setPayload(payload);
        signerJws.setAlgorithmHeaderValue(alg);
        signerJws.setKey(privateKey);
        signerJws.setKeyIdHeaderValue(kid);

        long iat = System.currentTimeMillis() / 1000l;

        // set headers
        signerJws.setHeader(HeaderParameterNames.BASE64URL_ENCODE_PAYLOAD, false);
        signerJws.setHeader("http://openbanking.org.uk/iat", iat);
        signerJws.setHeader("http://openbanking.org.uk/iss", iss);
        signerJws.setHeader("http://openbanking.org.uk/tan", tan);
        signerJws.setCriticalHeaderNames(critValues);

        // set optional headers
        if(!cty.isEmpty()) {
            signerJws.setHeader("cty", cty);
        }
        if(!typ.isEmpty()) {
            signerJws.setHeader("typ", typ);
        }

        String detachedJwt = "";
        try {
            detachedJwt = signerJws.getDetachedContentCompactSerialization();
        } catch (JoseException e) {
            e.printStackTrace();
        }
        return detachedJwt;
    }

    public boolean doVerify(String detachedJwt, String payload, PublicKey publicKey, String[] critValues, String alg) {
        JsonWebSignature verifierJws = new JsonWebSignature();
        verifierJws.setAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, alg));
        boolean isValid = false;

        try {
            verifierJws.setCompactSerialization(detachedJwt);
            verifierJws.setPayload(payload);
            verifierJws.setKey(publicKey);
            verifierJws.setKnownCriticalHeaders(critValues);
            isValid = verifierJws.verifySignature();

        } catch (JoseException exc) {
            System.out.println(exc.getMessage());
        }

        return isValid;
    }
}
