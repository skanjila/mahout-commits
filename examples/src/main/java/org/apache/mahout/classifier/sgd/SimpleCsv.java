/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.mahout.classifier.sgd;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.list.IntArrayList;
import org.apache.mahout.math.stats.OnlineSummarizer;
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder;
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

/**
 * Shows how different encoding choices can make big speed differences.
 * <p/>
 * Run with command line options --generate 1000000 test.csv to generate a million data lines in
 * test.csv.
 * <p/>
 * Run with command line options --parser test.csv to time how long it takes to parse and encode
 * those million data points
 * <p/>
 * Run with command line options --fast test.csv to time how long it takes to parse and encode those
 * million data points using byte-level parsing and direct value encoding.
 * <p/>
 * This doesn't demonstrate text encoding which is subject to somewhat different tricks.  The basic
 * idea of caching hash locations and byte level parsing still very much applies to text, however.
 */
public class SimpleCsv {
  public static final int SEPARATOR_CHAR = '\t';
  public static final String SEPARATOR = "\t";
  private static final int FIELDS = 100;

  public static void main(String[] args) throws IOException {
    FeatureVectorEncoder[] encoder = new FeatureVectorEncoder[FIELDS];
    for (int i = 0; i < FIELDS; i++) {
      encoder[i] = new ConstantValueEncoder("v" + 1);
    }

    OnlineSummarizer[] s = new OnlineSummarizer[FIELDS];
    for (int i = 0; i < FIELDS; i++) {
      s[i] = new OnlineSummarizer();
    }
    long t0 = System.currentTimeMillis();
    Vector v = new DenseVector(1000);
    if (args[0].equals("--generate")) {
      PrintWriter out = new PrintWriter(new File(args[2]));
      int n = Integer.parseInt(args[1]);
      for (int i = 0; i < n; i++) {
        Line x = Line.generate();
        out.println(x);
      }
      out.close();
    } else if ("--parse".equals(args[0])) {
      BufferedReader in = new BufferedReader(new FileReader(args[1]));
      String line = in.readLine();
      while (line != null) {
        v.assign(0);
        Line x = new Line(line);
        for (int i = 0; i < FIELDS; i++) {
          s[i].add(x.getDouble(i));
          encoder[i].addToVector(x.get(i), v);
        }
        line = in.readLine();
      }
      String separator = "";
      for (int i = 0; i < FIELDS; i++) {
        System.out.printf("%s%.3f", separator, s[i].getMean());
        separator = ",";
      }
    } else if ("--fast".equals(args[0])) {
      FastLineReader in = new FastLineReader(new FileInputStream(args[1]));
      FastLine line = in.read();
      while (line != null) {
        v.assign(0);
        for (int i = 0; i < FIELDS; i++) {
          double z = line.getDouble(i);
          s[i].add(z);
          encoder[i].addToVector((byte[]) null, z, v);
        }
        line = in.read();
      }
      String separator = "";
      for (int i = 0; i < FIELDS; i++) {
        System.out.printf("%s%.3f", separator, s[i].getMean());
        separator = ",";
      }
    }
    System.out.printf("\nElapsed time = %.3f\n", (System.currentTimeMillis() - t0) / 1000.0);
  }


  private static class Line {
    private static final Splitter onTabs = Splitter.on(SEPARATOR).trimResults();
    public static final Joiner withCommas = Joiner.on(SEPARATOR);

    public static final Random rand = new Random(1);

    private List<String> data;

    private Line(String line) {
      data = Lists.newArrayList(onTabs.split(line));
    }

    public Line() {
      data = Lists.newArrayList();
    }

    public double getDouble(int field) {
      return Double.parseDouble(data.get(field));
    }

    /**
     * Generate a random line with 20 fields each with integer values.
     *
     * @return A new line with data.
     */
    public static Line generate() {
      Line r = new Line();
      for (int i = 0; i < FIELDS; i++) {
        double mean = ((i + 1) * 257) % 50 + 1;
        r.data.add(Integer.toString(randomValue(mean)));
      }
      return r;
    }

    /**
     * Returns a random exponentially distributed integer with a particular mean value.  This is
     * just a way to create more small numbers than big numbers.
     *
     * @param mean
     * @return
     */
    private static int randomValue(double mean) {
      return (int) (-mean * Math.log(1 - rand.nextDouble()));
    }

    @Override
    public String toString() {
      return withCommas.join(data);
    }

    public String get(int field) {
      return data.get(field);
    }
  }

  private static class FastLine {

    private ByteBuffer base;
    private IntArrayList start = new IntArrayList();
    private IntArrayList length = new IntArrayList();

    public FastLine(ByteBuffer base) {
      this.base = base;
    }

    public static FastLine read(ByteBuffer buf) {
      FastLine r = new FastLine(buf);
      r.start.add(buf.position());
      int offset = buf.position();
      while (offset < buf.limit()) {
        int ch = buf.get();
        switch (ch) {
          case '\n':
            r.length.add(offset - r.start.get(r.length.size()) - 1);
            return r;
          case SEPARATOR_CHAR:
            r.length.add(offset - r.start.get(r.length.size()) - 1);
            r.start.add(offset);
            break;
          default:
            // nothing to do for now
        }
      }
      throw new IllegalArgumentException("Not enough bytes in buffer");
    }

    public double getDouble(int field) {
      int offset = start.get(field);
      int size = length.get(field);
      switch (size) {
        case 1:
          return base.get(offset) - '0';
        case 2:
          return (base.get(offset) - '0') * 10 + base.get(offset + 1) - '0';
        default:
          double r = 0;
          for (int i = 0; i < size; i++) {
            r = 10 * r + base.get(offset + i);
          }
          return r;
      }
    }
  }

  private static class FastLineReader {
    private InputStream in;
    private ByteBuffer buf = ByteBuffer.allocate(100000);

    public FastLineReader(InputStream in) throws IOException {
      this.in = in;
      buf.limit(0);
      fillBuffer();
    }

    public FastLine read() throws IOException {
      fillBuffer();
      if (buf.remaining() > 0) {
        return FastLine.read(buf);
      } else {
        return null;
      }
    }

    private void fillBuffer() throws IOException {
      if (buf.remaining() < 10000) {
        buf.compact();
        int n = in.read(buf.array(), buf.position(), buf.remaining());
        if (n != -1) {
          buf.limit(buf.position() + n);
          buf.position(0);
        } else {
          buf.flip();
        }
      }
    }
  }
}
