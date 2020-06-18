import contextis.OpenBankingMessageSigning;
import contextis.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class MessageSigningTest {
    private OpenBankingMessageSigning messageSigning = new OpenBankingMessageSigning();
    private Utils utils = new Utils();

    @Test
    public void detachedJWSTest(){
        String payload = "TestPayloadJUnitTest";
        String alg = "RS256";
        String privateKey = getClass().getClassLoader().getResource("certs/MockupPrivateKey.pkcs8.der").getFile().replace("%20", " ");
        PrivateKey privKey = this.utils.getPrivateKey(privateKey);
        String publicKey = getClass().getClassLoader().getResource("certs/MockupPublicKey.der").getFile().replace("%20", " ");
        PublicKey pubKey = this.utils.getPublicKey(publicKey);
        String kid = "90210ABAD";
        String iss = "C=UK, ST=England, L=London, O=Acme Ltd.";
        String tan = "openbanking.org.uk";
        String[] crit = {"http://openbanking.org.uk/iat", "http://openbanking.org.uk/iss", "http://openbanking.org.uk/tan"};
        String cty = "json";
        String typ = "JOSE";

        String signedObj = messageSigning.doSign(payload, alg, privKey, kid, crit, iss, tan, cty, typ);
        boolean isSignedCorrect = messageSigning.doVerify(signedObj, payload, pubKey, crit, alg);

        Assert.assertNotEquals("Verify if detached JWS is not empty", "", signedObj);
        Assert.assertTrue("Verify if generated signature validates correctly.", isSignedCorrect);
    }

}
