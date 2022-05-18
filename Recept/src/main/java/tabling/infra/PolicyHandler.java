package tabling.infra;

import javax.naming.NameParser;

import javax.naming.NameParser;

import tabling.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tabling.domain.*;


@Service
public class PolicyHandler{
    @Autowired ReceptRepository receptRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void afterReceptAdded(){System.out.println("Recept policy handler");};
}


