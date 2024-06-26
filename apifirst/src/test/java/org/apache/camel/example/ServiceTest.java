/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.example;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(WireMockTestResource.class)
class ServiceTest {

    @InjectWireMock
    WireMockServer wireMockServer;

    @BeforeEach
    void beforeEach() {
        // create mock endpoint
        wireMockServer.stubFor(
                post(urlEqualTo("/camel/individual/details"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/xml")
                                        .withStatus(200)
                                        .withBodyFile("individual.xml")
                        )
        );
    }

    @Test
    void should_get_response_for_individual() {
        given()
                .when()
                .get("/individual/details/123")
                .then()
                .statusCode(200)
                .body(
                        "fullName", is("Some One"),
                        "passportId", is("123456789-A"),
                        "addressLine1", is("1 Some Street"),
                        "addressLine2", is("Somewhere SOME C0D3"),
                        "addressLine3", is("UK")
                );
    }

    @Test
    void should_get_error_response_with_wrong_id_type() {
        given()
                .when()
                .get("/individual/details/a")
                .then()
                .statusCode(404);
    }


    @Test
    void should_get_anonymous_response_for_individual() {
        given()
                .when()
                .get("/individual/anonymous/details/123")
                .then()
                .statusCode(200)
                .body(
                        "fullName", is("**********"),
                        "passportId", is("**********"),
                        "addressLine1", is("1 Some Street"),
                        "addressLine2", is("Somewhere SOME C0D3"),
                        "addressLine3", is("UK")
                );
    }
}
