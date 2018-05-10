package x3.player.mru.common;

import java.lang.reflect.Field;

public class ShellUtil {

    public static Process runShell(String cmd) {
        Process p = null;
        try {
            String[] cmds = { "/bin/sh", "-c", cmd };
            p = Runtime.getRuntime().exec(cmds);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return p;
    }

    public static void waitShell(Process p) {
        if (p == null) {
            return;
        }

        try {
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void killShell(Process p) {
        if (p == null) {
            return;
        }

        try {
            int pid;
            Field f = p.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            pid = (Integer)f.get(p);
            f.setAccessible(false);

            if (pid > 0) {
                String killCmd = String.format("kill -9 %d", pid);
                runShell(killCmd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Process createNamedPipe(String filename) {
        if (filename == null) {
            return null;
        }

        String fifoCmd = String.format("exec mkfifo %s", filename);

        return runShell(fifoCmd);
    }

    public static Process startAMRTranscoding(String inputName, String outputName) {
        if (inputName == null || outputName == null) {
            return null;
        }

        String ffmpegCmd = String.format("exec ffmpeg -loglevel 0 -i %s -acodec pcm_s16le -f u16le pipe:1 > %s", inputName, outputName);

        return runShell(ffmpegCmd);
    }

    public static Process startAlawTranscoding(String inputName, String outputName) {
        if (inputName == null || outputName == null) {
            return null;
        }

        String ffmpegCmd = String.format("exec ffmpeg -f alaw -ar 8000 -ac 1 -i %s -acodec pcm_s16le -f u16le -ar 16000 -ac 1 pipe:1 > %s", inputName, outputName);

        return runShell(ffmpegCmd);
    }

    public static Process convertPcmToWav(String inputName, String outputName) {
        if (inputName == null || outputName == null) {
            return null;
        }

        String ffmpegCmd = String.format("exec ffmpeg -f s16le -ar 22050 -ac 1 -i %s -ar 8000 %s", inputName, outputName);

        return runShell(ffmpegCmd);
    }

    public static Process convertHlsToWav(String inputName, String outputName) {
        if (inputName == null || outputName == null) {
            return null;
        }

        String ffmpegCmd = String.format("exec ffmpeg -i \"%s\" -acodec pcm_s16le -ar 8000 -ac 1 -filter volume=0.2 %s", inputName, outputName);

        return runShell(ffmpegCmd);
    }
}
