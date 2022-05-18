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
    @Autowired OrderMngRepository orderMngRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReceptAdded_SendRecept(@Payload ReceptAdded receptAdded){

        if(!receptAdded.validate()) return;
        ReceptAdded event = receptAdded;
        System.out.println("\n\n##### listener SendRecept : " + receptAdded.toJson() + "\n\n");

        // Sample Logic //
        OrderMng.sendRecept(event);
    
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReceptCanceled_CancelRecept(@Payload ReceptCanceled receptCanceled){

        if(!receptCanceled.validate()) return;
        ReceptCanceled event = receptCanceled;
        System.out.println("\n\n##### listener CancelRecept : " + receptCanceled.toJson() + "\n\n");

        // Sample Logic //
        OrderMng.cancelRecept(event);
        
    }


}


