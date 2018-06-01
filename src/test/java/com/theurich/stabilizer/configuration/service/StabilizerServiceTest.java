package com.theurich.stabilizer.configuration.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class StabilizerServiceTest {

    StabilizerService stabilizerService = new StabilizerService();

    @Test
    public void stabilize() throws IOException {
        assertTrue(stabilizerService.stabilize("/home/theurich/TempX/DJI_0006.MOV", "/home/theurich/TempX/DJI_0006.MP4"));

    }
}
