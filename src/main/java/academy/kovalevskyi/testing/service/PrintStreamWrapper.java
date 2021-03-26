package academy.kovalevskyi.testing.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

public class PrintStreamWrapper extends PrintStream {

  private static final byte NEW_LINE_BYTE = 10;
  private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
  private final PrintStream stream = new PrintStream(buffer);
  private final PrintStream defaultStream;
  private volatile boolean state = false;
  private volatile boolean alive = true;
  private volatile boolean content = false;

  public PrintStreamWrapper(final PrintStream defaultStream) {
    super(defaultStream);
    this.defaultStream = defaultStream;
  }

  @Override
  public boolean checkError() {
    return super.checkError() || stream.checkError();
  }

  @Override
  public void close() {
    checkInstance(false);
    throw new UnsupportedOperationException("You have not right to close this stream");
  }

  @Override
  public void flush() {
    checkInstance(false);
    super.flush();
    stream.flush();
  }

  @Override
  public void write(int b) {
    checkInstance(true);
    if (state) {
      super.write(b);
      stream.write(b);
    }
  }

  @Override
  public void write(byte[] buf, int off, int len) {
    checkInstance(true);
    if (state) {
      super.write(buf, off, len);
      stream.write(buf, off, len);
    }
  }

  @Override
  public void print(boolean b) {
    checkInstance(true);
    if (state) {
      super.print(b);
      stream.print(b);
    }
  }

  @Override
  public void print(char c) {
    checkInstance(true);
    if (state) {
      super.print(c);
      stream.print(c);
    }
  }

  @Override
  public void print(int i) {
    checkInstance(true);
    if (state) {
      super.print(i);
      stream.print(i);
    }
  }

  @Override
  public void print(long l) {
    checkInstance(true);
    if (state) {
      super.print(l);
      stream.print(l);
    }
  }

  @Override
  public void print(float f) {
    checkInstance(true);
    if (state) {
      super.print(f);
      stream.print(f);
    }
  }

  @Override
  public void print(double d) {
    checkInstance(true);
    if (state) {
      super.print(d);
      stream.print(d);
    }
  }

  @Override
  public void print(char[] s) {
    checkInstance(true);
    if (state) {
      super.print(s);
      stream.print(s);
    }
  }

  @Override
  public void print(String s) {
    checkInstance(true);
    if (state) {
      super.print(s);
      stream.print(s);
    }
  }

  @Override
  public void print(Object obj) {
    checkInstance(true);
    if (state) {
      super.print(obj);
      stream.print(obj);
    }
  }

  @Override
  public void println() {
    checkInstance(true);
    if (state) {
      super.println();
      stream.println();
    }
  }

  @Override
  public void println(boolean x) {
    checkInstance(true);
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(char x) {
    checkInstance(true);
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(int x) {
    checkInstance(true);
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(long x) {
    checkInstance(true);
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(float x) {
    checkInstance(true);
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(double x) {
    checkInstance(true);
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(char[] x) {
    checkInstance(true);
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(String x) {
    checkInstance(true);
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(Object x) {
    checkInstance(true);
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public PrintStream printf(String format, Object... args) {
    checkInstance(true);
    if (state) {
      super.printf(format, args);
      stream.printf(format, args);
    }
    return this;
  }

  @Override
  public PrintStream printf(Locale l, String format, Object... args) {
    checkInstance(true);
    if (state) {
      super.printf(l, format, args);
      stream.printf(l, format, args);
    }
    return this;
  }

  @Override
  public PrintStream format(String format, Object... args) {
    checkInstance(true);
    if (state) {
      super.format(format, args);
      stream.format(format, args);
    }
    return this;
  }

  @Override
  public PrintStream format(Locale l, String format, Object... args) {
    checkInstance(true);
    if (state) {
      super.format(l, format, args);
      stream.format(l, format, args);
    }
    return this;
  }

  @Override
  public PrintStream append(CharSequence csq) {
    checkInstance(true);
    if (state) {
      super.append(csq);
      stream.append(csq);
    }
    return this;
  }

  @Override
  public PrintStream append(CharSequence csq, int start, int end) {
    checkInstance(true);
    if (state) {
      super.append(csq, start, end);
      stream.append(csq, start, end);
    }
    return this;
  }

  @Override
  public PrintStream append(char c) {
    checkInstance(true);
    if (state) {
      super.append(c);
      stream.append(c);
    }
    return this;
  }

  boolean hasContent() {
    if (!alive) {
      return false;
    }
    return content;
  }

  void enable() {
    if (!alive) {
      return;
    }
    synchronized (this) {
      state = true;
    }
  }

  void disable() {
    if (!alive) {
      return;
    }
    synchronized (this) {
      transferToNewLine();
      state = false;
      content = false;
      buffer.reset();
    }
  }

  void destroy() {
    if (!alive) {
      return;
    }
    synchronized (this) {
      alive = false;
      stream.close();
    }
  }

  private void checkInstance(final boolean newLine) {
    if (!alive) {
      throw new NullPointerException("Stream has been destroyed");
    }
    synchronized (this) {
      if (newLine && state && buffer.size() == 0) {
        content = true;
        var twoDotsByte = 58;
        defaultStream.write(twoDotsByte);
        stream.write(twoDotsByte);
        appendNewLine();
      }
    }
  }

  private void appendNewLine() {
    defaultStream.write(NEW_LINE_BYTE);
    stream.write(NEW_LINE_BYTE);
  }

  private void transferToNewLine() {
    var size = buffer.size();
    if (size > 0 && buffer.toByteArray()[size - 1] != NEW_LINE_BYTE) {
      appendNewLine();
    }
  }
}
