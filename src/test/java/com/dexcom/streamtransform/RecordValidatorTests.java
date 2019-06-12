package com.dexcom.streamtransform;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecordValidatorTests {

    @BeforeClass
    @AfterClass
    public static void setup(){
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            System.out.println("WINDOWS OS MODE - Cleanup state store.");
            try {
                File f = new File("/tmp/kafka-streams/");
                if (f.exists() && f.isDirectory()){
                    FileUtils.deleteDirectory(f);
                }
            } catch (Exception e){
                System.out.println("Cleanup Failed:"+e.getLocalizedMessage());
            }
        }

    }
    @Autowired
    private RecordValidator recordValidator;

    @Test
    public void validate_invalidJSON() {

        byte [] inputData = new String("{\"account\": ").getBytes();

        StreamRecord streamRecord = recordValidator.validate(inputData);
        assert  !streamRecord.isValid();
        assert  streamRecord.getValidationFailureReason() != null;
    }

    @Test
    public void validate_account_01() throws  Exception{

        String file = getClass().getClassLoader().getResource("account_01.json").getFile();
        byte [] inputData = Files.readAllBytes(new File(file).toPath());
//        System.out.println(new String(inputData));
        StreamRecord streamRecord = recordValidator.validate(inputData);
        System.out.println(streamRecord.getValidationFailureReason());
        assert  streamRecord.isValid();
        assert  streamRecord.getValidationFailureReason() == null;
    }
}
