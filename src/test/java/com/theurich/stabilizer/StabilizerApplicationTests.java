package com.theurich.stabilizer;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = StabilizerConfiguration.class)
public class StabilizerApplicationTests {

    private static final String VIDEO_PATH = "video/";

    private static final String FILE_UPLOAD_URL = "http://localhost:8080/stabilize/fileUpload";

    private static final String SMALL_MP4_PATH = VIDEO_PATH + "small.mp4";

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testStabilizationEndpoint() {

        final Resource videoResource = new ClassPathResource(SMALL_MP4_PATH);
        final WebTestClient client = WebTestClient.bindToServer().build();

        final MultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();
        valueMap.put("file", Collections.singletonList(videoResource));

        final BodyInserters.MultipartInserter multipartInserter = BodyInserters.fromMultipartData(valueMap);

        final WebTestClient.RequestHeadersSpec<?> body = client.post().uri(FILE_UPLOAD_URL).body(multipartInserter);

        body.exchange().expectStatus().is3xxRedirection();

    }

}


