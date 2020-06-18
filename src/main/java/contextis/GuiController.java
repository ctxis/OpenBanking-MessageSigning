package contextis;

import com.google.gson.*;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwx.HeaderParameterNames;
import org.jose4j.lang.JoseException;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;

public class GuiController {

    private Utils utils;
    private File currentSaveFile;
    private Gui gui;
    private OpenBankingMessageSigning openBankingMessageSigning;

    public void initialiseMessageSigning() {
        this.openBankingMessageSigning = new OpenBankingMessageSigning();
        this.utils = new Utils();
        this.gui = new Gui(this.utils);

        initButtons();
    }

    /**
     * Initialise button events
     */
    public void initButtons() {
        this.gui.getSaveConfig().addActionListener(this::saveConfig);
        this.gui.getLoadConfig().addActionListener(this::loadConfig);
        this.gui.getLoadPub().addActionListener(this::loadCert);
        this.gui.getLoadPriv().addActionListener(this::loadCert);
        this.gui.getSignPayload().addActionListener(this::signPayload);
        this.gui.getCopyOutput().addActionListener(this::copyOutput);
    }

    /**
     * File browser to select a location to save the config file.
     *
     * @param e "Settings" tab - "Save" button
     */
    public void saveConfig(ActionEvent e){
        File f = utils.getFileFromDialog(true, (currentSaveFile != null ? currentSaveFile.getPath() : "open-banking-msg-sign-config.json"), this.gui.getTabs());

        if(f != null) {
            currentSaveFile = f;

            try {
                FileWriter fw = new FileWriter(f);
                fw.write(getConfigData());
                fw.flush();
                fw.close();

            } catch(IOException exc) {
                System.out.println(exc.getMessage());
            }
        }
    }

    /**
     * This function grabs the data from the "Settings" tab input fields and creates a JSON object.
     *
     * @return settings data as JSON object
     */
    private String getConfigData() {
        JsonObject data = new JsonObject();
        data.addProperty("alg", this.gui.getTextAlg().getText());
        data.addProperty("kid", this.gui.getTextKid().getText());
        data.addProperty("private-key", this.gui.getTextPrivateKey().getText());
        data.addProperty("public-key", this.gui.getTextPublicKey().getText());
        data.addProperty("iss", this.gui.getTextIss().getText());
        data.addProperty("tan", this.gui.getTextTan().getText());
        data.addProperty("crit", this.gui.getTextCrit().getText());
        data.addProperty("typ", this.gui.getTextTyp().getText());
        data.addProperty("cty", this.gui.getTextCty().getText());
        return data.toString();
    }

    /**
     * File browser for finding and loading the json config for this tool.
     *
     * @param e "Settings" tab - "Load" button
     */
    public void loadConfig(ActionEvent e){
        File file;
        try {
            if((file = utils.getFileFromDialog(false, (currentSaveFile != null ? currentSaveFile.getPath() : ""), this.gui.getTabs())) != null){
                currentSaveFile = file;

                if(file.exists() && file.isFile() && file.canRead()){
                    byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
                    String config = new String(encoded, StandardCharsets.UTF_8);

                    JsonObject configObject = new JsonParser().parse(config).getAsJsonObject();
                    setConfigData(configObject);
                }
            }
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
    }

    /**
     * This will take the data provided by the loadConfig function and set the loaded values in the settings tab.
     *
     * @param configData: data from the json config file
     */
    private void setConfigData(JsonObject configData) {
        this.gui.getTextAlg().setText(configData.get("alg").getAsString());
        this.gui.getTextKid().setText(configData.get("kid").getAsString());
        this.gui.getTextPrivateKey().setText(configData.get("private-key").getAsString());
        this.gui.getTextPublicKey().setText(configData.get("public-key").getAsString());
        this.gui.getTextIss().setText(configData.get("iss").getAsString());
        this.gui.getTextTan().setText(configData.get("tan").getAsString());
        this.gui.getTextCrit().setText(configData.get("crit").getAsString());
        this.gui.getTextTyp().setText(configData.get("typ").getAsString());
        this.gui.getTextCty().setText(configData.get("cty").getAsString());
    }

    /**
     * This is the file browser for selecting the private and public keys. It will fill in the absolute path to
     * the Open Banking signing keys. Both public and private key need to be in DER format.
     *
     * @param e "Settings" tab - "Choose File" buttons
     */
    public void loadCert(ActionEvent e){
        File file;
        if((file = utils.getFileFromDialog(false, (""), this.gui.getTabs())) != null){
            if(file.exists() && file.isFile() && file.canRead()){
                String absolutePath = file.getAbsolutePath();

                if(e.getSource() == this.gui.getLoadPriv()) {
                    this.gui.getTextPrivateKey().setText(absolutePath);
                } else if(e.getSource() == this.gui.getLoadPub()) {
                    this.gui.getTextPublicKey().setText(absolutePath);
                }
            }
        }
    }

    /**
     * Upon clicking onto the "Sign" button in the "Signing" tab, this function is executed. This will sign the text
     * entered into the left text field within the "Signing" tab and show the resulting detached JWS in the right
     * text field.
     *
     * The signing function requires the parameters from the "Settings" tab.
     *
     * @param e "Sign" button
     */
    public void signPayload(ActionEvent e) {
        String payload = this.gui.getTextPayload().getText();
        String alg = this.gui.getTextAlg().getText();
        String privKeyFileName = this.gui.getTextPrivateKey().getText();
        String pubKeyFileName = this.gui.getTextPublicKey().getText();
        String kid = this.gui.getTextKid().getText();
        String iss = this.gui.getTextIss().getText();
        String tan = this.gui.getTextTan().getText();
        String crit = this.gui.getTextCrit().getText();

        // optional parameters
        String cty = this.gui.getTextCty().getText();
        String typ = this.gui.getTextTyp().getText();

        if(!payload.isEmpty() && !alg.isEmpty() && !privKeyFileName.isEmpty() && !kid.isEmpty() && !iss.isEmpty() && !tan.isEmpty() && !crit.isEmpty()) {

            String[] critValues = crit.split(",");
            PrivateKey privKey = this.utils.getPrivateKey(privKeyFileName);

            //generate detached JWS
            String detachedJwt = openBankingMessageSigning.doSign(payload, alg, privKey, kid, critValues, iss, tan, cty, typ);
            this.gui.getTextOutput().setText(detachedJwt);

            if(!pubKeyFileName.isEmpty()) {
                //verify detached JWS
                boolean isValid = openBankingMessageSigning.doVerify(detachedJwt, payload, this.utils.getPublicKey(pubKeyFileName), critValues, alg);
                this.gui.getIsValid().setText(Boolean.toString(isValid));
            } else {
                this.gui.getIsValid().setText("Please provide a public key to verify the signature.");
            }

            //decoded jwt
            java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
            String[] parts = detachedJwt.split("\\."); // split out the "parts" (header, payload and signature)

            String headerJson = new String(decoder.decode(parts[0]));
            //String payloadJson = new String(decoder.decode(parts[1]));
            String signatureJson = parts[2];

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(headerJson);
            String prettyJsonString = gson.toJson(je);

            this.gui.getTextSignature().setText(signatureJson);
            this.gui.getTextDecodedJwt().setText(prettyJsonString);
        }
    }

    /**
     * Upon click on the "Copy" button in the "Signing" tab, the detached JWS is saved into the clipboard.
     *
     * @param e "Copy" button
     */
    public void copyOutput(ActionEvent e) {
        StringSelection stringSelection = new StringSelection(this.gui.getTextOutput().getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
