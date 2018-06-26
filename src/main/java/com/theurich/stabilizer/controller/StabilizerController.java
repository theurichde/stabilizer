package com.theurich.stabilizer.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import com.theurich.stabilizer.service.StabilizerService;
import com.theurich.stabilizer.service.StorageService;
import com.theurich.stabilizer.util.PathUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
@Qualifier("stabilizerController")
public class StabilizerController {

    @Resource
    private StabilizerService stabilizerService;

    @Resource
    private StorageService storageService;

    @GetMapping(value = "/stabilize")
    public String stabilize() throws IOException {
        return "stabilize";
    }

    @PostMapping(value = "/stabilize")
    public String postStabilize() {
        return "test";
    }

    @PostMapping(value = "/stabilize/fileUpload", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ModelAndView handleFileUpload(@RequestParam("file") final MultipartFile file,
            @RequestParam(value = "hidden", defaultValue = "test") final String hidden,
            RedirectAttributes redirectAttributes) throws IOException {

        storageService.init();
        try {
            final URI location = storageService.store(file);
            final String output = PathUtil.ROOT_LOCATION.resolve(generateOutputFileName(file)).toString();
            stabilizerService.stabilize(location.getPath(), output);
            redirectAttributes.addFlashAttribute("File created: ", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ModelAndView modelAndView = new ModelAndView("redirect:uploaded");
        modelAndView.addObject("fileName", Objects.requireNonNull(file.getOriginalFilename()));
        return modelAndView;
    }

    private String generateOutputFileName(final MultipartFile input) {
        if (Objects.equals(input.getContentType(), "video/mp4")) {
            if (Objects.requireNonNull(input.getOriginalFilename()).contains("_sbs.mp4") || input.getOriginalFilename()
                    .contains("_stabil.mp4")) {
                return input.getOriginalFilename();
            }
            return Objects.requireNonNull(input.getOriginalFilename()).replace(".mp4", "_stabil.mp4");
        }
        return "";
    }

    @GetMapping(value = "/stabilize/uploaded")
    public String getUploadedPage(final ModelMap modelMap, @ModelAttribute("fileName") final String fileName) {
        return "uploaded";
    }

    private InputStream getFile(final String input) throws IOException {
        return storageService.loadAsResource(getVideoName(input)).getInputStream();
    }

    private String getVideoName(final String input) {
        if (input.endsWith("_sbs") || input.endsWith("_stabil")) {
            return "file:///tmp/stabilizer/" + input + ".mp4";
        }
        return "file:///tmp/stabilizer/" + input + "_stabil.mp4";
    }

    @GetMapping(value = "/show/{fileName}", produces = "video/mp4")
    public void getFileUploadScreen(final HttpServletResponse response, @PathVariable("fileName") String fileName)
            throws IOException {
        response.setContentType("video/mp4");
        IOUtils.copy(getFile(fileName), response.getOutputStream());
    }

}
