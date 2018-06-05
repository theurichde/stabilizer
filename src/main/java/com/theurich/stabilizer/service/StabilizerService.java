package com.theurich.stabilizer.service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import com.theurich.stabilizer.util.PathUtil;
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

        final FFmpegBuilder firstPassBuilder = Objects.requireNonNull(fFmpeg).builder()
                //                .setVideoFilter("acopy")
                .addInput(fileLocation).setAudioFilter(VIDSTABDETECT + "=result=" + getResolve("transform.trf"))
                .setVerbosity(FFmpegBuilder.Verbosity.DEBUG).addOutput(getResolve("empty.mp4").toString()).done();

        final FFmpegExecutor firstPassExecutor = new FFmpegExecutor(fFmpeg);
        firstPassExecutor.createJob(firstPassBuilder).run();

        final FFmpegBuilder secondPassBuilder = fFmpeg.builder().addInput(fileLocation)
                .setAudioFilter(VIDSTABTRANSFORM + "=input=" + getResolve("transform.trf"))
                //                .setFormat("mp4")
                .setVerbosity(FFmpegBuilder.Verbosity.DEBUG).addOutput(outputLocation)
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                //                .setVideoBitRate(10000)
                //                .setFormat("mp4")
                .done();
        final FFmpegExecutor executor = new FFmpegExecutor(fFmpeg);
        executor.createJob(secondPassBuilder).run();
        return outputLocation;
    }

    private Path getResolve(final String other) {
        return PathUtil.ROOT_LOCATION.resolve(other);
    }

}
