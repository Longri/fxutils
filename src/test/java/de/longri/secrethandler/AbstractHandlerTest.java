package de.longri.secrethandler;

import de.longri.gdx_utils.ObjectMap;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractHandlerTest {

    @Test
    void testInstancing() throws GeneralSecurityException, IOException {


        AbstractHandler handler = new AbstractHandler() {

            @Override
            public File getSecretFile() {
                return new File("./Test/secret/secretfile.ini");
            }

            @Override
            public ObjectMap<String, String> getSecretAndDescription() {
                ObjectMap map = new ObjectMap();
                map.put("passKey", "passValue");
                return map;
            }
        };
        // Backup original System.in
        InputStream originalIn = System.in;

        // Provide the values which will be read inside handler.generate method
        String simulatedInput = "MyPassKey";
        InputStream simulatedIn = new ByteArrayInputStream(simulatedInput.getBytes());

        // Set the System.in
        System.setIn(simulatedIn);

        // Call generate which will use our simulated input
        handler.generate();

        // Restore original System.in
        System.setIn(originalIn);

        handler.load();

        assertEquals(simulatedInput, System.getProperty("passKey"));
    }

}