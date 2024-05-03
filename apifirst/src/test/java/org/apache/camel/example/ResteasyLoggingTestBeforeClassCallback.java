package org.apache.camel.example;

import io.quarkus.test.junit.callback.QuarkusTestBeforeClassCallback;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

public class ResteasyLoggingTestBeforeClassCallback implements QuarkusTestBeforeClassCallback
{
    @Override
    public void beforeClass(Class<?> testClass)
    {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}
