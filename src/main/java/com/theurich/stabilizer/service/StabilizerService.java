package com.theurich.stabilizer.service;

import javax.annotation.PostConstruct;
import java.io.IOException;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.stereotype.Component;

@Component
public class StabilizerService {

    //    @Value("${stabilizer.video.ffmpeg.location}")
    private String ffmpegPath = "/home/theurich/TempX/ffmpeg-git-20180208-64bit-static/ffmpeg";

    private final FFmpeg fFmpeg = getfFmpeg();

    private final String VIDSTABDETECT = "vidstabdetect";

    private final String VIDSTABTRANSFORM = "vidstabtransform";

    @PostConstruct
    private FFmpeg getfFmpeg() {
        try {
            return new FFmpeg(ffmpegPath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String stabilize(final String fileLocation, final String outputLocation) throws IOException {
        final FFmpegBuilder fFmpegBuilder = fFmpeg.builder().setVideoFilter(VIDSTABTRANSFORM)
                .setVerbosity(FFmpegBuilder.Verbosity.DEBUG).addInput(fileLocation).addOutput(outputLocation)
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done();
        final FFmpegExecutor executor = new FFmpegExecutor(fFmpeg);
        executor.createJob(fFmpegBuilder).run();
        return outputLocation;
    }

}
