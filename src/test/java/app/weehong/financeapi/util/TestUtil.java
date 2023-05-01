package app.weehong.financeapi.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.util.ResourceUtils;

public class TestUtil {

  public static String readFile(String path) throws IOException {
    File resourceFile = ResourceUtils.getFile("classpath:" + path);
    return Files.readString(Path.of(resourceFile.getAbsolutePath()));
  }
}
