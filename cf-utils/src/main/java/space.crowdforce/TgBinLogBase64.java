package space.crowdforce;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class TgBinLogBase64 {
    public static void main(String[] args) throws IOException {
        String path = "telegram/";
        String tgBinLogBase64 = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(path + "td.binlog")));

        Files.write(Paths.get(path + "td.binlog.base64"), tgBinLogBase64.getBytes());
    }
}
