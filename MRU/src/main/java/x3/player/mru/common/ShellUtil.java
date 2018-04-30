package x3.player.mru.common;

public class ShellUtil {

    public static boolean runShell(String cmd) {
        boolean result = false;

        try {
            String[] cmds = { "/bin/sh", "-c", cmd };
            Process p = Runtime.getRuntime().exec(cmds);
            p.waitFor();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean createNamedPipe(String filename) {
        if (filename == null) {
            return false;
        }

        String fifoCmd = String.format("mkfifo %s", filename);

        return runShell(fifoCmd);
    }

    public static boolean startAMRTranscoding(String inputName, String outputName) {
        if (inputName == null || outputName == null) {
            return false;
        }

        String ffmpegCmd = String.format("ffmpeg -loglevel 0 -i %s -acodec pcm_s16le -f u16le pipe:1 > %s", inputName, outputName);

        return runShell(ffmpegCmd);
    }

    public static boolean startAlawTranscoding(String inputName, String outputName) {
        if (inputName == null || outputName == null) {
            return false;
        }

        String ffmpegCmd = String.format("ffmpeg -f alaw -i %s -acodec pcm_s16le -f u16le pipe:1 > %s", inputName, outputName);

        return runShell(ffmpegCmd);
    }

    public static boolean convertPcmToWav(String inputName, String outputName) {
        if (inputName == null || outputName == null) {
            return false;
        }

        String ffmpegCmd = String.format("ffmpeg -f s16le -ar 22050 -ac 1 -i %s -ar 8000 %s", inputName, outputName);

        return runShell(ffmpegCmd);
    }
}
