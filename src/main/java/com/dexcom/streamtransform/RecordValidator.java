package com.dexcom.streamtransform;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class RecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(RecordValidator.class);

    @Value("${stream-connector-config.inputRecordSchema}")
    private String inputRecordSchema;

    private Schema schema;
    @PostConstruct
    public void init() throws  Exception{

        try{
            ClassPathResource cpr = new ClassPathResource("schemas/"+inputRecordSchema+".json");
            JSONObject rawSchema = new JSONObject(new JSONTokener(cpr.getInputStream()));
            schema = SchemaLoader.load(rawSchema);
        }catch (Exception ex){
            logger.warn("Unable to load schema for validation");
            throw ex;
        }
    }

    public StreamRecord validate(byte[] data){
            String valueString = new String(data);
            String jsonString = null;
            try{
                JSONObject jsonObject = new JSONObject(new JSONTokener(valueString));
                schema.validate(jsonObject);
                jsonString = jsonObject.toString();
                logger.info("Record Validation Successful: "+jsonString);
            }catch (ValidationException e){
                logger.warn("Record Validation Failed: "+valueString,e);
                return  new StreamRecord(data,null,false,e.getLocalizedMessage());
            }catch (Exception ex){
                logger.warn("Error while validating the record: "+valueString,ex);
                return  new StreamRecord(data,null,false,ex.getLocalizedMessage());

            }
            return  new StreamRecord(data,jsonString,true,null);

    }
}
