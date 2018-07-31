package com.theurich.stabilizer.controller;

import static com.google.common.base.Throwables.getRootCause;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Objects;

import com.theurich.stabilizer.service.StabilizerService;
import com.theurich.stabilizer.service.StorageService;
import com.theurich.stabilizer.util.PathUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
@RequestMapping
@Qualifier("stabilizerController")
public class StabilizerController {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizerController.class);

    private static final String STABIL_SUFFIX = "_stabil.mp4";

    @Resource
    private StabilizerService stabilizerService;

    @Resource
    private StorageService storageService;

    @GetMapping(value = "/stabilize")
    public String stabilize() throws IOException {
        return "stabilize";
    }

    @GetMapping(value = "/stabilize/uploaded")
    public String getUploadedPage(final ModelMap modelMap, //
            @ModelAttribute("fileName") final String fileName, //
            @ModelAttribute("sbsFileName") final String sbsFileName) {
        return "uploaded";
    }

    @GetMapping(value = "/show/{fileName}", produces = "video/mp4")
    public void getFileUploadScreen(final HttpServletResponse response, @PathVariable("fileName") final String fileName)
            throws IOException {
        response.setContentType("video/mp4");
        IOUtils.copy(getFile(fileName), response.getOutputStream());
    }

    @PostMapping(value = "/stabilize/fileUpload", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ModelAndView handleFileUpload(@RequestParam("file") final MultipartFile file, //
            @RequestParam final Map<String, String> parameters) {

        storageService.init();

        try {
            final URI location = storageService.store(file);
            final String output = PathUtil.ROOT_LOCATION.resolve(generateOutputFileName(file)).toString();
            stabilizerService.stabilize(location.getPath(), output, parameters);
        } catch (final IOException e) {
            LOG.error("Could not stabilize video. Root Cause: {0}", getRootCause(e));
        }

        final ModelAndView modelAndView = new ModelAndView("redirect:uploaded");
        Objects.requireNonNull(file);
        modelAndView.addObject("fileName", getStableVersionName(file));
        modelAndView.addObject("sbsFileName", getSideBySideVersionName(file));
        return modelAndView;
    }

    private String getSideBySideVersionName(final MultipartFile file) {
        Objects.requireNonNull(file.getOriginalFilename());
        return file.getOriginalFilename().replace(".mp4", "_sbs");
    }

    private String getStableVersionName(final MultipartFile file) {
        Objects.requireNonNull(file.getOriginalFilename());
        return file.getOriginalFilename().replace(".mp4", "_stabil");
    }

    private String generateOutputFileName(final MultipartFile input) {
        if (Objects.equals(input.getContentType(), "video/mp4")) {
            if (Objects.requireNonNull(input.getOriginalFilename()).contains("_sbs.mp4") || input.getOriginalFilename()
                    .contains(STABIL_SUFFIX)) {
                return input.getOriginalFilename();
            }
            return Objects.requireNonNull(input.getOriginalFilename()).replace(".mp4", STABIL_SUFFIX);
        }
        return "";
    }

    private InputStream getFile(final String input) throws IOException {
        return storageService.loadAsResource(getVideoName(input)).getInputStream();
    }

    private String getVideoName(final String input) {
        if (input.endsWith("_sbs") || input.endsWith("_stabil")) {
            return "file:///tmp/stabilizer/" + input + ".mp4";
        }
        return "file:///tmp/stabilizer/" + input + STABIL_SUFFIX;
    }

}
