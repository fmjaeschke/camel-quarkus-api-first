package org.apache.camel.example;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.example.beans.IndividualDetails;

public class IndividualResourceImpl implements IndividualResource {

    private final ProducerTemplate producer;

    public IndividualResourceImpl(ProducerTemplate producerTemplate) {
        this.producer = producerTemplate;
    }

    @Override
    public IndividualDetails getDetails(int id) {
        return producer.requestBody("direct:getDetails", id, IndividualDetails.class);
    }

    @Override
    public IndividualDetails getAnonymousDetails(int id) {
        return producer.requestBody("direct:getAnonymousDetails", id, IndividualDetails.class);
    }
}
