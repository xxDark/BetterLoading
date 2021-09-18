package dev.xdark.betterloading.internal;

import java.io.IOException;
import java.io.InputStream;

public interface IFileResourcePackMixin extends IResourcePackMixin {

  InputStream tryOpenFile(String name) throws IOException;
}
