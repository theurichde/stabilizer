package com.theurich.stabilizer.configuration.service;

import java.io.IOException;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StabilizerService {

    @Value("${video.ffmpeg.location}")
    private String ffmpegPath;

    private final FFmpeg fFmpeg = getfFmpeg();

    private final String VIDSTABDETECT = "vidstabdetect";

    private final String VIDSTABTRANSFORM = "vidstabtransform";

    private FFmpeg getfFmpeg() {
        try {
            return new FFmpeg(ffmpegPath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean stabilize(final String fileLocation, final String output) throws IOException {
        final FFmpegBuilder fFmpegBuilder = fFmpeg.builder().setVideoFilter(VIDSTABTRANSFORM)
                .setVerbosity(FFmpegBuilder.Verbosity.DEBUG).addInput(fileLocation).addOutput(output)
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done();
        final FFmpegExecutor executor = new FFmpegExecutor(fFmpeg);
        executor.createJob(fFmpegBuilder).run();
        return true;
    }

}
