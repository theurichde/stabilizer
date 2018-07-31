package com.theurich.stabilizer.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import com.theurich.stabilizer.util.PathUtil;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StabilizerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static String ffmpegPath = "/home/theurich/TempX/ffmpeg-git-20180208-64bit-static/ffmpeg";

    private final FFmpeg ffmpeg = getFfmpeg();

    private static final String VIDSTABDETECT = "vidstabdetect";

    private static final String VIDSTABTRANSFORM = "vidstabtransform";

    private final String INPUT = "=input=";

    private final String TRANSFORM_TRF = "transform.trf";

    //    @PostConstruct
    private FFmpeg getFfmpeg() {
        try {
            return new FFmpeg(ffmpegPath);
        } catch (IOException e) {
            logger.error("Could not get FFMpeg!", e);
            return null;
        }
    }

    public boolean stabilize(final String fileLocation, final String outputLocation, Map<String, String> parameterMap)
            throws IOException {

        processFirstPass(fileLocation);
        processSecondPass(fileLocation, outputLocation, parameterMap);
        processSideBySideVideo(fileLocation, outputLocation);

        return true;
    }

    private void processFirstPass(final String fileLocation) throws IOException {

        final FFmpegBuilder firstPassBuilder = Objects.requireNonNull(ffmpeg).builder().addInput(fileLocation) //
                .setAudioFilter(VIDSTABDETECT + "=result=" + getResolve(TRANSFORM_TRF)) //
                .setVerbosity(FFmpegBuilder.Verbosity.DEBUG) //
                .addOutput(getResolve("empty.mp4").toString()) //
                .done();

        final FFmpegExecutor firstPassExecutor = new FFmpegExecutor(ffmpeg);
        firstPassExecutor.createJob(firstPassBuilder).run();
    }

    private void processSecondPass(final String fileLocation, final String outputLocation,
            final Map<String, String> parameterMap) throws IOException {
        final FFmpegBuilder secondPassBuilder = Objects.requireNonNull(ffmpeg).builder().addInput(fileLocation) //
                .setAudioFilter(generateFilter(parameterMap)) //
                .setVerbosity(FFmpegBuilder.Verbosity.DEBUG).addOutput(outputLocation) //
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) //
                .done();
        final FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
        executor.createJob(secondPassBuilder).run();
    }

    private String generateFilter(final Map<String, String> parameterMap) {

        if (Objects.isNull(parameterMap) || parameterMap.isEmpty()) {
            return VIDSTABTRANSFORM + INPUT + getResolve(TRANSFORM_TRF);
        }

        final StringBuilder builder = new StringBuilder();

        builder.append(VIDSTABTRANSFORM);
        builder.append(INPUT);
        builder.append(getResolve(TRANSFORM_TRF));

        for (final Map.Entry<String, String> entry : parameterMap.entrySet()) {

            if (entry.getKey().isEmpty() || entry.getValue().isEmpty()) {
                continue;
            }

            builder.append(":");
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
        }

        return builder.toString();

    }

    private void processSideBySideVideo(final String fileLocationOne, final String fileLocationTwo) throws IOException {

        final String[] split = fileLocationOne.split("\\.");
        final String outputLocation = split[0] + "_sbs." + split[1];

        final FFmpegBuilder sideBySideBuilder = Objects.requireNonNull(ffmpeg).builder().addInput(fileLocationOne)
                .addInput(fileLocationTwo).setComplexFilter(
                        "[0:v]setpts=PTS-STARTPTS, pad=iw*2:ih[bg]; [1:v]setpts=PTS-STARTPTS[fg]; [bg][fg]overlay=w")
                .addOutput(outputLocation).done();

        final FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
        executor.createJob(sideBySideBuilder).run();

        //        ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex "[0:v]setpts=PTS-STARTPTS, pad=iw*2:ih[bg]; [1:v]setpts=PTS-STARTPTS[fg]; [bg][fg]overlay=w" side_by_side.mp4

    }

    private Path getResolve(final String other) {
        return PathUtil.ROOT_LOCATION.resolve(other);
    }

}
