package de.longri.secrethandler;

import de.longri.gdx_utils.ObjectMap;
import de.longri.utils.ComputerOverview;
import de.longri.utils.Crypto;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Scanner;

public abstract class AbstractHandler {

    private ComputerOverview computerOverview;

    public abstract File getSecretFile();

    public abstract ObjectMap<String, String> getSecretAndDescription();

    public void generate() throws IOException, GeneralSecurityException {
        ObjectMap<String, String> map = getSecretAndDescription();

        File secretFile = getSecretFile();
        secretFile.delete();
        secretFile.getParentFile().mkdirs();
        secretFile.createNewFile();

        Scanner scanner = new Scanner(System.in);
        for (ObjectMap.Entry<String, String> entry : map) {
            String key = entry.key;
            String value = entry.value;
            System.out.println(value);
            String input = scanner.nextLine();

            put(secretFile, key, input);
        }
    }

    private void put(File secretFile, String key, String input) throws GeneralSecurityException, IOException {
        // encrypt entry with serial
        if (computerOverview == null) {
            computerOverview = new ComputerOverview();
        }
        String serial = computerOverview.serialNumber;

        String encryptedValue = Crypto.encrypt(input, serial);
        Files.writeString(secretFile.toPath(), key + "=" + encryptedValue + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void load() throws IOException, GeneralSecurityException {
        // decrypt entry with serial
        if (computerOverview == null) {
            computerOverview = new ComputerOverview();
        }
        String serial = computerOverview.serialNumber;


        Properties prop = new Properties();

        // load a properties file
        prop.load(new FileInputStream(getSecretFile()));

        // get the properties and set as system properties
        for (String name : prop.stringPropertyNames()) {
            String value = prop.getProperty(name);

            String decodeSecret = Crypto.decrypt(value, serial);
            System.setProperty(name, decodeSecret);
        }
    }
}
