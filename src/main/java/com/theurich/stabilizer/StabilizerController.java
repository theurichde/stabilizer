package com.theurich.stabilizer;

import javax.annotation.Resource;
import java.io.IOException;

import com.theurich.stabilizer.configuration.service.StabilizerService;
import com.theurich.stabilizer.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class StabilizerController {

    @Resource
    StabilizerService stabilizerService;

    @Resource
    StorageService storageService;

    @GetMapping(value = "/stabilize")
    public ResponseEntity<String> stabilize() throws IOException {
        return ResponseEntity.ok("Hi there");
    }

    @PostMapping(value = "/stabilize/fileUpload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        return "";
    }

}
