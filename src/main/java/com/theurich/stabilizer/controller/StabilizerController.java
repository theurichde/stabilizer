package com.theurich.stabilizer.controller;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.theurich.stabilizer.service.StabilizerService;
import com.theurich.stabilizer.service.StorageService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Qualifier("stabilizerController")
public class StabilizerController {

    @Resource
    StabilizerService stabilizerService;

    @Resource
    StorageService storageService;

    @GetMapping(value = "/stabilize")
    public String stabilize() throws IOException {
        return "stabilize";
    }

    @PostMapping(value = "/stabilize")
    public String postStabilize() {
        return "test";
    }

    @PostMapping(value = "/stabilize/fileUpload", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] handleFileUpload(@RequestParam("file") final MultipartFile file,
            @RequestParam(value = "hidden", defaultValue = "test") final String hidden,
            RedirectAttributes redirectAttributes) {

        storageService.init();
        try {
            final byte[] bytes = file.getBytes();
            final URI location = storageService.store(file);
            final String output = stabilizerService.stabilize(location.getPath(), "/tmp/stabilizer/test.mp4");
            redirectAttributes.addFlashAttribute("File created: ", output);
            return getFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getFile(String input) throws IOException {
        // TODO: java.io.FileNotFoundException: tmp/stabilizer/tmp/stabilizer/test.mp4
        // Missing trailing slash
        InputStream inputStream = storageService.loadAsResource(input).getInputStream();
        return IOUtils.toByteArray(inputStream);
    }

    @GetMapping(value = "/stabilize/fileUpload")
    public String getFileUploadScreen() {
        return "";
    }

}
