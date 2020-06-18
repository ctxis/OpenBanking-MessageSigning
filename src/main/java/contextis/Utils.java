package contextis;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Utils {

    /**
     * Util function to cut down on code for new JLabel
     *
     * @param name: label name
     * @param x: x position
     * @param y: y position
     * @param toolTip: optional - add a tooltip to the label
     * @return JLabel
     */
    public JLabel createLabel(String name, int x, int y, String toolTip) {
        JLabel label = new JLabel(name);
        label.setSize(80, 30);
        label.setLocation(x, y);
        if (!toolTip.isEmpty()) {
            label.setToolTipText(toolTip);
        }
        return label;
    }

    /**
     * Util function to cut down on code for new JTextFields
     *
     * @param width: text field width
     * @param x: x position
     * @param y: y position
     * @return new JTextField
     */
    public JTextField createTextField(int width, int x, int y) {
        JTextField textField = new JTextField();
        textField.setSize(width, 25);
        textField.setLocation(x, y);
        return textField;
    }

    /**
     * Util function for file browsers
     *
     * @param saveFile: true/false
     * @param defaultName: filename
     * @param tabbedPane: provide the tabbed pane where the browser should be shown
     * @return file
     */
    public File getFileFromDialog(boolean saveFile, String defaultName, JTabbedPane tabbedPane){
        JFileChooser fc = new JFileChooser();

        if(defaultName != "") {
            fc.setSelectedFile(new File(defaultName));
        }

        int returnVal;

        if(saveFile) {
            returnVal = fc.showSaveDialog(tabbedPane);
        }
        else {
            returnVal = fc.showOpenDialog(tabbedPane);
        }

        if(returnVal == JFileChooser.APPROVE_OPTION){
            File f = fc.getSelectedFile();
            if(!saveFile) {
                return f; //Not saving over file, so just return it
            }

            try{
                if(f.exists()){
                    f.delete();
                }
                f.createNewFile();

                return f;
            } catch(IOException exc){
                System.out.println(exc.getMessage());
            }
        }
        return null;
    }

    /**
     * Import file contents
     *
     * @param filename: absolute path and filename
     * @return byte[]
     * @throws IOException
     */
    public byte[] readFileBytes(String filename) throws IOException {
        Path path = Paths.get(filename);
        return Files.readAllBytes(path);
    }

    /**
     * Load private certificate from file
     * openssl pkcs8 -topk8 -nocrypt -in signing.key -outform der -out priv-key.der
     *
     * @return Private Key - requires the key in DER format
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public PrivateKey getPrivateKey(String filename) {
        PKCS8EncodedKeySpec keySpec = null;
        KeyFactory keyFactory = null;
        PrivateKey privateKey = null;

        try {
            keySpec = new PKCS8EncodedKeySpec(readFileBytes(filename));
            keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return privateKey;
    }

    /**
     * Load Public Key - requires the key in der format
     * PEM certificate to PEM public key: openssl x509 -pubkey -noout -in cert.pem  > pubkey.pem
     * PEM public key to DER public key: openssl rsa -pubin -inform PEM -in pubkey.pem -outform DER -out pub-key.der
     *
     * @return Public Key Object
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public PublicKey getPublicKey(String filename) {
        X509EncodedKeySpec publicSpec = null;
        KeyFactory keyFactory = null;
        PublicKey publicKey = null;

        try {
            publicSpec = new X509EncodedKeySpec(readFileBytes(filename));
            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(publicSpec);

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return publicKey;
    }
}
