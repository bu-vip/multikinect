package edu.bu.vip.kinect.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.common.collect.ImmutableList;
import com.roeper.bu.kinect.Protos.Frame;

public class FrameReader implements FrameDataSource {
  private Frame next;
  private FileInputStream input;
  private File file;

  public FrameReader(String filePath) throws IOException {
    this.file = new File(filePath);
    this.reset();
  }

  public FrameReader(File file) throws IOException {
    this.file = file;
    this.reset();
  }

  public void close() throws IOException {
    if (this.input != null) {
      this.input.close();
    }
  }

  public boolean hasNext() {
    return (this.next != null);
  }

  public Frame getNext() {
    Frame last = this.next;
    try {
      readNext();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return last;
  }

  public void reset() throws IOException {
    if (this.input != null) {
      this.input.close();
    }
    this.input = new FileInputStream(this.file);
    readNext();
  }

  private void readNext() throws IOException {
    if (this.input != null) {
      this.next = Frame.parseDelimitedFrom(input);
      if (this.next == null) {
        this.input.close();
        this.input = null;
      }
    }
  }

  public static long numberOfFrames(String path) throws IOException {
    FileInputStream reader = new FileInputStream(path);
    long counter = 0;
    while (Frame.parseDelimitedFrom(reader) != null) {
      counter++;
    }
    reader.close();
    return counter;
  }

  public static ImmutableList<Frame> readAllFrames(String file) throws IOException {
    ImmutableList.Builder<Frame> builder = ImmutableList.builder();
    FrameReader reader = new FrameReader(file);
    while (reader.hasNext()) {
      builder.add(reader.getNext());
    }

    reader.close();

    return builder.build();
  }

  public static ImmutableList<Frame> readFramesInTimeInterval(String file, long startTime,
      long endTime) throws IOException {
    ImmutableList.Builder<Frame> frames = ImmutableList.builder();
    FrameReader reader = new FrameReader(file);

    // Seek to first frame
    while (reader.hasNext()) {
      Frame next = reader.getNext();
      if (next.getTime() >= startTime) {
        frames.add(next);
        break;
      }
    }

    // Read until end time is reached
    while (reader.hasNext()) {
      Frame next = reader.getNext();
      if (next.getTime() <= endTime) {
        frames.add(next);
      } else {
        break;
      }
    }

    reader.close();

    return frames.build();
  }
}
