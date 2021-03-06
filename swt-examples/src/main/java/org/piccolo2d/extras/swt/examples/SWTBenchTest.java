/*
 * Copyright (c) 2008-2019, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.piccolo2d.extras.swt.examples;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.piccolo2d.extras.swt.SWTGraphics2D;


/**
 * Piccolo2D SWT benchmarking test suite.
 */
public class SWTBenchTest extends Canvas {

    // Paths
    private GeneralPath testShape = new GeneralPath();

    // Images
    private Image testImageOpaque;
    private Image testImageBitmask;
    private Image testImageTranslucent;
    private Image testImageARGB;

    // Transforms
    private AffineTransform transform = new AffineTransform();
    private static final AffineTransform IDENTITY = new AffineTransform();

    // Geometry
    private double pts[] = new double[20];

    // Colors
    private static final Color colors[] = { Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, Color.YELLOW, };

    // Flags
    private boolean offscreen;
    private boolean antialiased;

    // Statistics
    private int results[][] = new int[NUM_CONTEXTS][NUM_TESTS];

    // Constants

    private static final int CTX_NORMAL = 0;
    // private static final int CTX_CLIPPED = 1;
    private static final int CTX_TRANSFORMED = 1;
    // private static final int CTX_BLENDED = 3;
    private static final int NUM_CONTEXTS = 2;

    // private static String contextNames[] = {
    // "normal",
    // "clip",
    // "transform",
    // "alpha",
    // };

    private static String contextNames[] = { "normal", "transform" };

    //
    // TEST METHODS
    //

    private static final int DRAW_LINE = 0;
    private static final int DRAW_RECT = 1;
    private static final int FILL_RECT = 2;
    private static final int DRAW_OVAL = 3;
    private static final int FILL_OVAL = 4;
    private static final int DRAW_POLY = 5;
    private static final int FILL_POLY = 6;
    private static final int DRAW_TEXT = 7;
    private static final int DRAW_IMG1 = 8;
    private static final int DRAW_IMG2 = 9;
    private static final int DRAW_IMG3 = 10;
    private static final int DRAW_IMG4 = 11;
    private static final int DRAW_IMG5 = 12;
    private static final int NUM_TESTS = 13;

    private static String testNames[] = { "line", "rect", "fill rect", "oval", "fill oval", "poly", "fill poly", "text",
            "image", "scaled image", "mask image", "alpha image", "argb image", };

    private void testDrawLine(final SWTGraphics2D g, final Random r) {
        g.drawLine(rand(r), rand(r), rand(r), rand(r));
    }

    private void testDrawRect(final SWTGraphics2D g, final Random r) {
        g.drawRect(rand(r), rand(r), rand(r), rand(r));
    }

    private void testFillRect(final SWTGraphics2D g, final Random r) {
        g.fillRect(rand(r), rand(r), rand(r), rand(r));
    }

    private void testDrawOval(final SWTGraphics2D g, final Random r) {
        g.drawOval(rand(r), rand(r), rand(r), rand(r));
    }

    private void testFillOval(final SWTGraphics2D g, final Random r) {
        g.fillOval(rand(r), rand(r), rand(r), rand(r));
    }

    private void genPoly(final Random r) {
        for (int i = 0; i < pts.length / 2; i++) {
            pts[2 * i] = rand(r);
            pts[2 * i + 1] = rand(r);
        }
    }

    private void testDrawPoly(final SWTGraphics2D g, final Random r) {
        genPoly(r);
        g.drawPolyline(pts);
    }

    private void testFillPoly(final SWTGraphics2D g, final Random r) {
        genPoly(r);
        g.fillPolygon(pts);
    }

    private void testDrawText(final SWTGraphics2D g, final Random r) {
        g.drawString("Abcdefghijklmnop", rand(r), rand(r));
    }

    // Basic image
    private void testDrawImg1(final SWTGraphics2D g, final Random r) {
        g.drawImage(testImageOpaque, rand(r), rand(r));
    }

    // Scaled image
    private void testDrawImg2(final SWTGraphics2D g, final Random r) {
        final Rectangle rect = testImageOpaque.getBounds();
        g.drawImage(testImageOpaque, 0, 0, rect.width, rect.height, rand(r), rand(r), rand(r), rand(r));
    }

    // Bitmask image (unscaled)
    private void testDrawImg3(final SWTGraphics2D g, final Random r) {
        g.drawImage(testImageBitmask, rand(r), rand(r));
    }

    // Translucent image (unscaled)
    private void testDrawImg4(final SWTGraphics2D g, final Random r) {
        g.drawImage(testImageTranslucent, rand(r), rand(r));
    }

    // Buffered image (unscaled)
    private void testDrawImg5(final SWTGraphics2D g, final Random r) {
        g.drawImage(testImageARGB, rand(r), rand(r));
    }

    private static Image loadImage(final Display display, final String name) {
        InputStream stream = null;
        try {
            stream = SWTBenchTest.class.getResourceAsStream(name);
            if (stream != null) {
                final ImageData imageData = new ImageData(stream);
                return new Image(display, imageData);
                // if (imageData != null) {
                // ImageData mask = imageData.getTransparencyMask();
                // return new Image(display, imageData, mask);
                // }

            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Create a new Piccolo2D SWT benchmarking test suite with the specified parent and style.
     *
     * @param parent parent
     * @param style style
     */
    private SWTBenchTest(final Composite parent, final int style) {
        super(parent, style);

        testImageOpaque = loadImage(getDisplay(), "opaque.jpg");
        testImageBitmask = loadImage(getDisplay(), "bitmask.gif");
        testImageTranslucent = loadImage(getDisplay(), "translucent.png");
        testImageARGB = new Image(getDisplay(), 128, 128);

        final GC tmpGC = new GC(testImageARGB);
        tmpGC.drawImage(testImageTranslucent, 0, 0);
        tmpGC.dispose();

        addPaintListener(new PaintListener() {
            public void paintControl(final PaintEvent pe) {
                runAll(new SWTGraphics2D(pe.gc, getDisplay()));
            }
        });
    }

    private void setupTransform(final Graphics2D g, final Random r) {
        transform.setToIdentity();

        switch (abs(r.nextInt()) % 5) {
            default:
                // case 0: // UNIFORM SCALE
                final double s = r.nextDouble();
                transform.scale(5 * s + 0.1, 5 * s + 0.1);
                break;
            // case 1: // NON-UNIFORM SCALE
            // transform.scale(5 * r.nextDouble() + 0.1, 5 * r.nextDouble() +
            // 0.1);
            // break;
            // case 2: // ROTATION
            // transform.rotate(r.nextDouble() * Math.PI * 2);
            // break;
            // case 3: // TRANSLATION
            // transform.translate(r.nextDouble() * 500, r.nextDouble() * 500);
            // break;
            // case 4: // TRANSLATE + ROTATE + SCALE
            // s = r.nextDouble();
            // transform.translate(r.nextDouble() * 500, r.nextDouble() * 500);
            // transform.rotate(r.nextDouble() * Math.PI * 2);
            // transform.scale(5*s + 0.1, 5*s + 0.1);
            // break;
        }

        g.setTransform(transform);
    }

    private void setupClip(final Graphics2D g, final Random r) {
        // g.setClip(rand(r), rand(r), rand(r), rand(r));
    }

    private void setupBlend(final Graphics2D g, final Random r) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, r.nextFloat()));
    }

    private void setup(final int ctx, final Graphics2D g, final Random r) {
        switch (ctx) {
            case CTX_NORMAL:
                break;

            case CTX_TRANSFORMED:
                setupTransform(g, r);
                break;

            // case CTX_CLIPPED:
            // setupClip(g, r);
            // break;
            //
            // case CTX_BLENDED:
            // setupBlend(g, r);
            // break;
        }
    }

    private void test(final int testNum, final SWTGraphics2D g, final Random r) {

        g.setColor(colors[abs(r.nextInt()) % colors.length]);
        g.setBackground(colors[abs(r.nextInt()) % colors.length]);

        switch (testNum) {
            case DRAW_LINE:
                testDrawLine(g, r);
                break;
            case DRAW_RECT:
                testDrawRect(g, r);
                break;
            case FILL_RECT:
                testFillRect(g, r);
                break;
            case DRAW_OVAL:
                testDrawOval(g, r);
                break;
            case FILL_OVAL:
                testFillOval(g, r);
                break;
            case DRAW_POLY:
                testDrawPoly(g, r);
                break;
            case FILL_POLY:
                testFillPoly(g, r);
                break;
            case DRAW_TEXT:
                testDrawText(g, r);
                break;
            case DRAW_IMG1:
                testDrawImg1(g, r);
                break;
            case DRAW_IMG2:
                testDrawImg2(g, r);
                break;
            case DRAW_IMG3:
                testDrawImg3(g, r);
                break;
            case DRAW_IMG4:
                testDrawImg4(g, r);
                break;
            case DRAW_IMG5:
                testDrawImg5(g, r);
                break;
        }
    }

    private void runTest(final SWTGraphics2D g, final int ctx, final int testNum) {
        final Random r1 = new Random(1);
        final Random r2 = new Random(1);

        System.out.println("Test: " + testNames[testNum]);
        final long t1 = System.currentTimeMillis();
        int i = 0;
        while (true) {
            if (i % 10 == 0) {
                setup(ctx, g, r1);
            }
            test(testNum, g, r2);
            i++;
            final long t2 = System.currentTimeMillis();
            if (t2 - t1 >= 5000) {
                break;
            }
        }
        results[ctx][testNum] += i / 5;
        System.out.println("Shapes per second: " + results[ctx][testNum]);
    }

    private void runAll(final SWTGraphics2D g) {
        System.out.println("BENCHMARKING: " + g);

        if (antialiased) {
            System.out.println("ANTIALIASED");
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        for (int ctx = 0; ctx < NUM_CONTEXTS; ctx++) {
            System.out.println("Context: " + contextNames[ctx]);
            for (int i = 0; i < NUM_TESTS; i++) {
                g.setClip(null);
                g.setTransform(IDENTITY);
                runTest(g, ctx, i);
            }
        }

        if (offscreen) {
            g.dispose();
        }

        String fileName = g.getClass().getName().replace('.', '_');
        if (offscreen) {
            fileName += "-offscreen";
        }
        if (antialiased) {
            fileName += "-antialiased";
        }
        dumpResults(fileName + ".txt");
        System.exit(0);
    }

    private void dumpResults(final String fileName) {
        try {
            final FileOutputStream fout = new FileOutputStream(fileName);
            final PrintWriter out = new PrintWriter(fout);
            out.print('\t');
            for (int i = 0; i < NUM_TESTS; i++) {
                out.print(testNames[i]);
                out.print('\t');
            }
            out.println("");
            for (int ctx = 0; ctx < NUM_CONTEXTS; ctx++) {
                out.print(contextNames[ctx]);
                for (int i = 0; i < NUM_TESTS; i++) {
                    out.print('\t');
                    out.print(results[ctx][i]);
                }
                out.println("");
            }
            out.close();
            results = new int[NUM_CONTEXTS][NUM_TESTS];
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Point computeSize(final int wHint, final int hHint) {
        return new Point(512, 512);
    }

    public Point computeSize(final int wHint, final int hHint, final boolean changed) {
        return computeSize(wHint, hHint);
    }

    private static int abs(final int x) {
        return x < 0 ? -x : x;
    }

    private static double rand(final Random r) {
        return abs(r.nextInt()) % 500;
    }

    /**
     * Main.
     *
     * @param args command line arguments, ignored
     */
    public static void main(final String args[]) {
        // Create frame
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        // Add bench test
        final SWTBenchTest m = new SWTBenchTest(shell, SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND);
        m.setSize(512, 512);
        for (int i = 0; i < args.length; i++) {
            if (args[i].intern() == "-offscreen") {
                m.offscreen = true;
            }
            else if (args[i].intern() == "-anti") {
                m.antialiased = true;
            }
            else {
                System.out.println("Usage: java BenchTest [-anti] [-offscreen]");
                System.exit(1);
            }
        }

        shell.pack();
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}
