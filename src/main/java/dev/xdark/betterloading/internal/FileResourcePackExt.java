package dev.xdark.betterloading.internal;

import java.io.IOException;
import java.io.InputStream;

public interface FileResourcePackExt extends ResourcePackExt {

  InputStream tryOpenFile(String name) throws IOException;
}
