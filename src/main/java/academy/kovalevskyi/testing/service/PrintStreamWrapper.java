package academy.kovalevskyi.testing.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import javax.annotation.Nonnull;

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
    if (!alive) {
      return false;
    }
    return super.checkError() || stream.checkError();
  }

  @Override
  public void close() {
    if (!alive) {
      return;
    }
    throw new UnsupportedOperationException("You have not right to close this stream");
  }

  @Override
  public void flush() {
    if (!alive) {
      return;
    }
    super.flush();
    stream.flush();
  }

  @Override
  public void write(int b) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.write(b);
      stream.write(b);
    }
  }

  @Override
  public void write(@Nonnull byte[] buf, int off, int len) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.write(buf, off, len);
      stream.write(buf, off, len);
    }
  }

  @Override
  public void print(boolean b) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.print(b);
      stream.print(b);
    }
  }

  @Override
  public void print(char c) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.print(c);
      stream.print(c);
    }
  }

  @Override
  public void print(int i) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.print(i);
      stream.print(i);
    }
  }

  @Override
  public void print(long l) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.print(l);
      stream.print(l);
    }
  }

  @Override
  public void print(float f) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.print(f);
      stream.print(f);
    }
  }

  @Override
  public void print(double d) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.print(d);
      stream.print(d);
    }
  }

  @Override
  public void print(@Nonnull char[] s) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.print(s);
      stream.print(s);
    }
  }

  @Override
  public void print(String s) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.print(s);
      stream.print(s);
    }
  }

  @Override
  public void print(Object obj) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.print(obj);
      stream.print(obj);
    }
  }

  @Override
  public void println() {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.println();
      stream.println();
    }
  }

  @Override
  public void println(boolean x) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(char x) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(int x) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(long x) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(float x) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(double x) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(@Nonnull char[] x) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(String x) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public void println(Object x) {
    if (!alive) {
      return;
    }
    checkBuffer();
    if (state) {
      super.println(x);
      stream.println(x);
    }
  }

  @Override
  public PrintStream printf(@Nonnull String format, Object... args) {
    if (!alive) {
      return this;
    }
    checkBuffer();
    if (state) {
      super.printf(format, args);
      stream.printf(format, args);
    }
    return this;
  }

  @Override
  public PrintStream printf(Locale l, @Nonnull String format, Object... args) {
    if (!alive) {
      return this;
    }
    checkBuffer();
    if (state) {
      super.printf(l, format, args);
      stream.printf(l, format, args);
    }
    return this;
  }

  @Override
  public PrintStream format(@Nonnull String format, Object... args) {
    if (!alive) {
      return this;
    }
    checkBuffer();
    if (state) {
      super.format(format, args);
      stream.format(format, args);
    }
    return this;
  }

  @Override
  public PrintStream format(Locale l, @Nonnull String format, Object... args) {
    if (!alive) {
      return this;
    }
    checkBuffer();
    if (state) {
      super.format(l, format, args);
      stream.format(l, format, args);
    }
    return this;
  }

  @Override
  public PrintStream append(CharSequence csq) {
    if (!alive) {
      return this;
    }
    checkBuffer();
    if (state) {
      super.append(csq);
      stream.append(csq);
    }
    return this;
  }

  @Override
  public PrintStream append(CharSequence csq, int start, int end) {
    if (!alive) {
      return this;
    }
    checkBuffer();
    if (state) {
      super.append(csq, start, end);
      stream.append(csq, start, end);
    }
    return this;
  }

  @Override
  public PrintStream append(char c) {
    if (!alive) {
      return this;
    }
    checkBuffer();
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

  private void checkBuffer() {
    synchronized (this) {
      if (state && buffer.size() == 0) {
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
