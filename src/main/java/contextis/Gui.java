package contextis;

import javax.swing.*;

public class Gui {

    private Utils utils;

    // buttons
    private JButton saveConfig;
    private JButton loadConfig;
    private JButton loadPriv;
    private JButton loadPub;
    private JButton signPayload;
    private JButton copyOutput;

    // encryption params
    private JTextField textKid;
    private JTextField textPublicKey;
    private JTextField textPrivateKey;
    private JTextField textAlg;

    private JTextField textIss;
    private JTextField textTan;
    private JTextField textCrit;
    private JTextField textTyp;
    private JTextField textCty;

    // signing fields
    private JTextArea textPayload;
    private JTextArea textOutput;

    // verify signing
    private JLabel isValid;
    private JTextArea textDecodedJwt;
    private JTextArea textSignature;

    // tabs
    private JTabbedPane tabs;

    public Gui(Utils utils) {
        this.utils = utils;
        initGui();
    }

    /**
     * Initialise GUI
     * - set size
     * - set title
     * - create tabs
     */
    public void initGui() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Open Banking Message Signing");
        dialog.setSize(970,650);
        dialog.add(createTabs());
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /**
     * create Tabs pane for the GUI
     *
     * @return JTabbedPane
     */
    public JTabbedPane createTabs() {
        tabs = new JTabbedPane();
        tabs.add("Signing", createSigningPanel());
        tabs.add("Verify", verifySigning());
        tabs.add("Settings", createSettingsPanel());
        return tabs;
    }

    /**
     * JPanel for Verify Tab
     *
     * @return JPanel
     */
    public JPanel verifySigning() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblValid = new JLabel("Valid signature: ");
        lblValid.setSize(170, 20);
        lblValid.setLocation(20, 20);
        panel.add(lblValid);

        this.isValid = new JLabel();
        this.isValid.setSize(450, 20);
        this.isValid.setLocation(190, 20);
        panel.add(this.isValid);

        this.textDecodedJwt = new JTextArea();
        this.textDecodedJwt.setSize(900, 300);
        this.textDecodedJwt.setLocation(20, 60);
        this.textDecodedJwt.setLineWrap(true);
        panel.add(this.textDecodedJwt);

        this.textSignature = new JTextArea();
        this.textSignature.setSize(900, 150);
        this.textSignature.setLocation(20, 400);
        this.textSignature.setLineWrap(true);
        panel.add(this.textSignature);

        return panel;
    }

    /**
     * JPanel for Signing Tab
     *
     * @return JPanel
     */
    public JPanel createSigningPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        this.signPayload = new JButton("Sign");
        this.signPayload.setSize(100, 20);
        this.signPayload.setLocation(20, 20);
        panel.add(signPayload);

        this.copyOutput = new JButton("Copy");
        this.copyOutput.setSize(100, 20);
        this.copyOutput.setLocation(490, 20);
        panel.add(copyOutput);

        this.textPayload = new JTextArea();
        this.textPayload.setSize(450, 500);
        this.textPayload.setLocation(20, 60);
        JScrollPane payloadScrollPane = new JScrollPane(this.textPayload);
        payloadScrollPane.setBounds(20, 60, 450, 500);
        panel.add(payloadScrollPane);

        this.textOutput = new JTextArea();
        this.textOutput.setSize(450, 500);
        this.textOutput.setLocation(490, 60);
        this.textOutput.setLineWrap(true);
        panel.add(textOutput);

        return panel;
    }

    /**
     * JPanel for Settings Tab
     *
     * @return JPanel
     */
    public JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        int textLength = 350;
        int labelRow1 = 20;
        int textRow1 = 110;
        int labelRow2 = 490;
        int textRow2 = 580;

        this.saveConfig = new JButton("Save");
        this.saveConfig.setSize(100, 20);
        this.saveConfig.setLocation(20, 20);
        panel.add(saveConfig);

        this.loadConfig = new JButton("Load");
        this.loadConfig.setSize(100, 20);
        this.loadConfig.setLocation(130, 20);
        panel.add(loadConfig);

        panel.add(this.utils.createLabel("ALG", labelRow1, 110, "Open Banking Specification: PS256 or RS256"));
        this.textAlg = this.utils.createTextField(textLength,  textRow1, 110);
        this.textAlg.setText("PS256");
        panel.add(this.textAlg);

        panel.add(this.utils.createLabel("KID", labelRow2, 110, "Certificate Signing Key"));
        this.textKid = this.utils.createTextField(textLength,  textRow2, 110);
        panel.add(this.textKid);

        panel.add(this.utils.createLabel("ISS", labelRow1, 140, "http://openbanking.org.uk/iss - This must be a string that identifies the PSP."));
        this.textIss = this.utils.createTextField(textLength,  textRow1, 140);
        panel.add(this.textIss);

        panel.add(this.utils.createLabel("TAN", labelRow1, 170, "This must be a string that consists of a domain name that is registered to and identifies the Trust Anchor"));
        this.textTan = this.utils.createTextField(textLength,  textRow1, 170);
        this.textTan.setText("openbanking.org.uk");
        panel.add(this.textTan);

        panel.add(this.utils.createLabel("Crit", labelRow2, 170, "String array consisting of the set values"));
        this.textCrit = this.utils.createTextField(textLength,  textRow2, 170);
        this.textCrit.setText("b64,http://openbanking.org.uk/iat,http://openbanking.org.uk/iss,http://openbanking.org.uk/tan");
        panel.add(this.textCrit);

        panel.add(this.utils.createLabel("Typ*", labelRow1, 200, "Optional - if required set to 'JOSE'"));
        this.textTyp = this.utils.createTextField(textLength,  textRow1, 200);
        panel.add(this.textTyp);

        panel.add(this.utils.createLabel("Cty*", labelRow2, 200, "Optional - if required set to 'application/json'"));
        this.textCty = this.utils.createTextField(textLength,  textRow2, 200);
        panel.add(this.textCty);


        panel.add(this.utils.createLabel("Private Key", labelRow1, 250, "Enter the full path to the private key file (in DER format)."));
        this.textPrivateKey = this.utils.createTextField(textLength,  textRow1, 250);
        panel.add(this.textPrivateKey);

        panel.add(this.utils.createLabel("Public Key*", labelRow1, 280, "Optional: Enter the full path to the public *key* file (in DER format)."));
        this.textPublicKey = this.utils.createTextField(textLength,  textRow1, 280);
        panel.add(this.textPublicKey);

        this.loadPriv = new JButton("Choose File");
        this.loadPriv.setSize(150, 20);
        this.loadPriv.setLocation(labelRow2, 250);
        panel.add(loadPriv);

        this.loadPub = new JButton("Choose File");
        this.loadPub.setSize(150, 20);
        this.loadPub.setLocation(labelRow2, 280);
        panel.add(loadPub);

        return panel;
    }

    public JButton getSaveConfig() {
        return saveConfig;
    }

    public JButton getLoadConfig() {
        return loadConfig;
    }

    public JButton getLoadPriv() {
        return loadPriv;
    }

    public JButton getLoadPub() {
        return loadPub;
    }

    public JTextField getTextKid() {
        return textKid;
    }

    public JTextField getTextPublicKey() {
        return textPublicKey;
    }

    public JTextField getTextPrivateKey() {
        return textPrivateKey;
    }

    public JTextField getTextAlg() {
        return textAlg;
    }

    public JTextField getTextIss() {
        return textIss;
    }

    public JTextField getTextTan() {
        return textTan;
    }

    public JTextField getTextCrit() {
        return textCrit;
    }

    public JTextField getTextTyp() {
        return textTyp;
    }

    public JTextField getTextCty() {
        return textCty;
    }

    public JTabbedPane getTabs() {
        return tabs;
    }

    public JButton getSignPayload() {
        return signPayload;
    }

    public JTextArea getTextPayload() {
        return textPayload;
    }

    public JTextArea getTextOutput() {
        return textOutput;
    }

    public JButton getCopyOutput() {
        return copyOutput;
    }

    public JLabel getIsValid() {
        return isValid;
    }

    public JTextArea getTextDecodedJwt() {
        return textDecodedJwt;
    }

    public JTextArea getTextSignature() {
        return textSignature;
    }
}
