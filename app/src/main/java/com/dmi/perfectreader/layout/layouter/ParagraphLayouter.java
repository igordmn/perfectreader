package com.dmi.perfectreader.layout.layouter;

import com.carrotsearch.hppc.FloatArrayList;
import com.carrotsearch.hppc.IntArrayList;
import com.dmi.perfectreader.layout.LayoutObject;
import com.dmi.perfectreader.layout.LayoutParagraph;
import com.dmi.perfectreader.layout.config.HangingConfig;
import com.dmi.perfectreader.layout.config.LayoutArea;
import com.dmi.perfectreader.layout.config.LayoutChars;
import com.dmi.perfectreader.layout.config.TextMetrics;
import com.dmi.perfectreader.layout.liner.Liner;
import com.dmi.perfectreader.layout.run.ObjectRun;
import com.dmi.perfectreader.layout.run.Run;
import com.dmi.perfectreader.layout.run.TextRun;
import com.dmi.perfectreader.render.RenderChild;
import com.dmi.perfectreader.render.RenderLine;
import com.dmi.perfectreader.render.RenderObject;
import com.dmi.perfectreader.render.RenderParagraph;
import com.dmi.perfectreader.render.RenderSpace;
import com.dmi.perfectreader.render.RenderText;
import com.dmi.perfectreader.style.FontStyle;
import com.dmi.perfectreader.style.TextAlign;
import com.dmi.util.cache.ReuseCache.Reuser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import static com.dmi.util.cache.ReuseCache.reuseCollection;
import static com.dmi.util.cache.ReuseCache.reuseFloatArrayList;
import static com.dmi.util.cache.ReuseCache.reuseIntArrayList;
import static com.dmi.util.cache.ReuseCache.reuseStringBuilder;
import static com.dmi.util.cache.ReuseCache.reuser;
import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.max;

public class ParagraphLayouter implements Layouter<LayoutParagraph, RenderParagraph> {
    private static final CharSequence HYPHEN_STRING = new String(new char[] {LayoutChars.HYPHEN});

    private final Layouter<LayoutObject, RenderObject> childrenLayouter;
    private final TextMetrics textMetrics;
    private final Liner liner;

    public ParagraphLayouter(
            Layouter<LayoutObject, RenderObject> childrenLayouter,
            TextMetrics textMetrics,
            Liner liner
    ) {
        this.childrenLayouter = childrenLayouter;
        this.textMetrics = textMetrics;
        this.liner = liner;
    }

    @Override
    public RenderParagraph layout(LayoutParagraph object, LayoutArea area) {
        return new Callable<RenderParagraph>() {
            List<Run> runs = object.runs();
            Locale locale = object.locale();

            @Override
            public RenderParagraph call() {
                PrerenderedText text = new PrerenderedText();
                List<Liner.Line> lines = liner.makeLines(text, lineConfig());
                float width = object.fillAreaWidth() ? area.width() : computeWidth(lines);

                ParagraphBuilder paragraph = new ParagraphBuilder();
                paragraph.reset(width);

                for (Liner.Line line : lines) {
                    paragraph.addLine(renderLine(text, line, width));
                }

                return paragraph.build();
            }

            private Liner.Config lineConfig() {
                HangingConfig hangingConfig = object.hangingConfig();
                return new Liner.Config() {
                    @Override
                    public float firstLineIndent() { return object.firstLineIndent(); }

                    @Override
                    public float maxWidth() { return area.width(); }

                    @Override
                    public float leftHangFactor(char ch) {
                        return hangingConfig.leftHangFactor(ch); }

                    @Override
                    public float rightHangFactor(char ch) {
                        return hangingConfig.rightHangFactor(ch);
                    }
                };
            }

            private float computeWidth(List<Liner.Line> lines) {
                float maxWidth = 0;
                for (Liner.Line line : lines) {
                    float lineRight = line.left() + line.width();
                    if (lineRight > maxWidth) {
                        maxWidth = lineRight;
                    }
                }
                return maxWidth;
            }
            private RenderLine renderLine(PrerenderedText text, Liner.Line line, float width) {
                LineBuilder renderLine = new LineBuilder();
                renderLine.reset(width);
                renderLine.addOffset(line.left());

                float lineRight = line.left() + line.width();
                float freeSpace = width - lineRight;

                float midspaceScale;
                if (!line.isLast() && object.textAlign() == TextAlign.JUSTIFY) {
                    midspaceScale = computeMidspaceScale(text, line, freeSpace);
                } else {
                    midspaceScale = 1.0F;
                }

                if (object.textAlign() == TextAlign.RIGHT) {
                    renderLine.addOffset(freeSpace);
                } else if (object.textAlign() == TextAlign.CENTER) {
                    renderLine.addOffset(freeSpace / 2);
                }

                List<Liner.Token> tokens = line.tokens();
                for (int i = 0; i < tokens.size(); i++) {
                    Liner.Token token = tokens.get(i);
                    boolean hasHyphenAfter = line.hasHyphenAfter() && i == tokens.size() - 1;
                    boolean isMidspace = token.isSpace() && i > 0 && i < tokens.size() - 1;
                    float scaleX = isMidspace ? midspaceScale : 1.0F;
                    text.render(token.beginIndex(), token.endIndex(), hasHyphenAfter, token.isSpace(), scaleX, renderLine);
                }

                return renderLine.build();
            }

            /**
             * midspace - пробелы между словами (но не в конце или в начале строке).
             * возвращает множитель, на который нужно умножить их ширину, чтобы строка стала выровненной по ширине параграфа
             */
            private float computeMidspaceScale(PrerenderedText text, Liner.Line line, float freeSpace) {
                float totalExpansion = max(0, freeSpace);

                float totalMidspace = 0;
                List<Liner.Token> tokens = line.tokens();
                for (int i = 1; i < tokens.size() - 1; i++) {
                    Liner.Token token = tokens.get(i);
                    if (token.isSpace()) {
                        totalMidspace += text.widthOf(token.beginIndex(), token.endIndex());
                    }
                }

                return totalMidspace > 0 ? (totalMidspace + totalExpansion) / totalMidspace : 1;
            }

            class PrerenderedText implements Liner.MeasuredText {
                private final StringBuilder plainText = Reusables.plainText();
                private final IntArrayList plainIndexToRunIndex = Reusables.plainIndexToRunIndex();
                private final FloatArrayList plainIndexToWidth = Reusables.plainIndexToWidth();
                private final FloatArrayList plainIndexToTotalWidth = Reusables.plainIndexToTotalWidth();
                private final IntArrayList runIndexToPlainBeginIndex = Reusables.runIndexToPlainBeginIndex();
                private final ArrayList<RenderObject> runIndexToObject = Reusables.runIndexToObject();
                private final FloatArrayList runIndexToHeight = Reusables.runIndexToHeight();
                private final FloatArrayList runIndexToBaseline = Reusables.runIndexToBaseline();
                private final FloatArrayList runIndexToHyphenWidth = Reusables.runIndexToHyphenWidth();

                private final LayoutArea childrenArea = new LayoutArea(area.width(), 0);

                {
                    for (int r = 0; r < runs.size(); r++) {
                        Run run = runs.get(r);
                        if (run instanceof TextRun) {
                            prerenderTextRun(r, (TextRun) run);
                        } else if (run instanceof ObjectRun) {
                            prerenderObject(r, (ObjectRun) run);
                        } else {
                            throw new UnsupportedOperationException();
                        }
                    }
                }

                private void prerenderTextRun(int runIndex, TextRun run) {
                    CharSequence text = run.text();
                    TextMetrics.VerticalMetrics verticalMetrics = textMetrics.verticalMetrics(run.style());

                    plainText.append(text);

                    float[] charWidths = textMetrics.charWidths(text, run.style());
                    for (int i = 0; i < text.length(); i++) {
                        plainIndexToRunIndex.add(runIndex);
                        addWidth(charWidths[i]);
                    }

                    runIndexToPlainBeginIndex.add(plainText.length() - text.length());
                    runIndexToObject.add(null);
                    runIndexToHeight.add(-verticalMetrics.ascent() + verticalMetrics.descent());
                    runIndexToBaseline.add(-verticalMetrics.ascent());
                    runIndexToHyphenWidth.add(hyphenWidth(run));
                }

                private float hyphenWidth(TextRun run) {
                    return textMetrics.charWidths(HYPHEN_STRING, run.style())[0];
                }

                private void prerenderObject(int runIndex, ObjectRun run) {
                    RenderObject object = childrenLayouter.layout(run.object(), childrenArea);

                    plainText.append(LayoutChars.OBJECT_REPLACEMENT_CHARACTER);
                    plainIndexToRunIndex.add(runIndex);
                    addWidth(object.width());

                    runIndexToPlainBeginIndex.add(plainText.length() - 1);
                    runIndexToObject.add(object);
                    runIndexToHeight.add(object.height());
                    runIndexToBaseline.add(object.height());
                    runIndexToHyphenWidth.add(0);
                }

                private void addWidth(float width) {
                    int size = plainIndexToTotalWidth.size();
                    float currentWidth = size > 0 ? plainIndexToTotalWidth.get(size - 1) : 0;
                    plainIndexToWidth.add(width);
                    plainIndexToTotalWidth.add(currentWidth + width);
                }

                @Override
                public CharSequence plainText() {
                    return plainText;
                }

                @Override
                public Locale locale() { return locale; }

                @Override
                public float widthOf(int index) {
                    return plainIndexToWidth.get(index);
                }

                @Override
                public float widthOf(int beginIndex, int endIndex) {
                    float widthToBegin = beginIndex > 0 ? plainIndexToTotalWidth.get(beginIndex - 1) : 0;
                    float widthToEnd = endIndex > 0 ? plainIndexToTotalWidth.get(endIndex - 1) : 0;
                    return widthToEnd - widthToBegin;
                }

                @Override
                public float hyphenWidthAfter(int index) {
                    int runIndex = plainIndexToRunIndex.get(index);
                    return runIndexToHyphenWidth.get(runIndex);
                }

                public void render(int beginIndex, int endIndex, boolean hasHyphenAfter, boolean isSpace, float scaleX, LineBuilder line) {
                    checkArgument(beginIndex < plainText.length() && beginIndex < endIndex);

                    renderRuns(beginIndex, endIndex, isSpace, scaleX, line);

                    if (hasHyphenAfter) {
                        renderHyphen(endIndex - 1, line);
                    }
                }

                private void renderRuns(int beginIndex, int endIndex, boolean isSpace, float scaleX, LineBuilder line) {
                    int begin = beginIndex;
                    for (int end = beginIndex + 1; end <= endIndex; end++) {
                        int runIndex = plainIndexToRunIndex.get(begin);
                        boolean isEndOfRun = end == endIndex || runIndex != plainIndexToRunIndex.get(end);
                        if (isEndOfRun) {
                            renderRun(begin, end, runIndex, isSpace, scaleX, line);
                            begin = end;
                        }
                    }
                }

                private void renderRun(int beginIndex, int endIndex, int runIndex, boolean isSpace, float scaleX, LineBuilder line) {
                    Run run = runs.get(runIndex);
                    if (isSpace) {
                        renderSpace(beginIndex, endIndex, runIndex, (TextRun) run, scaleX, line);
                    } else if (run instanceof TextRun) {
                        renderTextRun(beginIndex, endIndex, runIndex, (TextRun) run, line);
                    } else if (run instanceof ObjectRun) {
                        renderObjectRun(runIndex, line);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }

                private void renderSpace(int beginIndex, int endIndex, int runIndex, TextRun run, float scaleX, LineBuilder line) {
                    CharSequence runText = run.text();
                    FontStyle style = run.style();

                    int runBegin = runIndexToPlainBeginIndex.get(runIndex);
                    CharSequence text = runText.subSequence(beginIndex - runBegin, endIndex - runBegin);
                    float width = widthOf(beginIndex, endIndex) * scaleX;
                    float height = runIndexToHeight.get(runIndex);
                    float baseline = runIndexToBaseline.get(runIndex);

                    RenderObject renderObject = new RenderSpace(width, height, text, locale, baseline, scaleX, style);
                    line.addObject(renderObject, baseline);
                }

                private void renderTextRun(int beginIndex, int endIndex, int runIndex, TextRun run, LineBuilder line) {
                    CharSequence runText = run.text();
                    FontStyle style = run.style();

                    int runBegin = runIndexToPlainBeginIndex.get(runIndex);
                    CharSequence text = runText.subSequence(beginIndex - runBegin, endIndex - runBegin);
                    float width = widthOf(beginIndex, endIndex);
                    float height = runIndexToHeight.get(runIndex);
                    float baseline = runIndexToBaseline.get(runIndex);

                    RenderObject renderObject = new RenderText(width, height, text, locale, baseline, style);
                    line.addObject(renderObject, baseline);
                }

                private void renderObjectRun(int runIndex, LineBuilder line) {
                    float baseline = runIndexToBaseline.get(runIndex);
                    RenderObject renderObject = runIndexToObject.get(runIndex);
                    line.addObject(renderObject, baseline);
                }

                private void renderHyphen(int plainIndex, LineBuilder line) {
                    int runIndex = plainIndexToRunIndex.get(plainIndex);
                    Run run = runs.get(runIndex);
                    if (run instanceof TextRun) {
                        TextRun textRun = (TextRun) run;
                        FontStyle style = textRun.style();

                        float width = runIndexToHyphenWidth.get(runIndex);
                        float height = runIndexToHeight.get(runIndex);
                        float baseline = runIndexToBaseline.get(runIndex);

                        RenderObject renderObject = new RenderText(width, height, HYPHEN_STRING, locale, baseline, style);
                        line.addObject(renderObject, baseline);
                    }
                }
            }
        }.call();
    }

    private static class LineBuilder {
        private float width;

        private final List<RenderObject> objects = new ArrayList<>(32);
        private final FloatArrayList baselines = new FloatArrayList(32);
        private final FloatArrayList lefts = new FloatArrayList(32);
        private float offset = 0;

        public void reset(float width) {
            this.width = width;
            objects.clear();
            baselines.elementsCount = 0;
            lefts.elementsCount = 0;
            offset = 0;
        }

        public void addOffset(float offset) {
            this.offset += offset;
        }

        public void addObject(RenderObject object, float baseline) {
            objects.add(object);
            baselines.add(baseline);
            lefts.add(offset);
            offset += object.width();
        }

        public RenderLine build() {
            List<RenderChild> children = new ArrayList<>();

            float lineBaseline = 0;
            for (int i = 0; i < objects.size(); i++) {
                float baseline = baselines.get(i);
                if (baseline > lineBaseline) {
                    lineBaseline = baseline;
                }
            }

            float lineHeight = 0;
            for (int i = 0; i < objects.size(); i++) {
                float x = lefts.get(i);
                float y = lineBaseline - baselines.get(i);
                RenderObject object = objects.get(i);
                if (y + object.height() > lineHeight) {
                    lineHeight = y + object.height();
                }
                children.add(new RenderChild(x, y, object));
            }

            return new RenderLine(width, lineHeight, children);
        }
    }

    private static class ParagraphBuilder {
        private final ArrayList<RenderChild> children = new ArrayList<>();
        private float width = 0;
        private float height = 0;

        public void reset(float width) {
            this.width = width;
        }

        public void addLine(RenderLine line) {
            children.add(new RenderChild(0, height, line));
            height += line.height();
        }

        public RenderParagraph build() {
            return new RenderParagraph(width, height, children);
        }
    }

    private static class Reusables {
        private static final int INITIAL_CHARS_CAPACITY = 4000;
        private static final int INITIAL_RUNS_CAPACITY = 16;

        private static final Reuser<StringBuilder> plainText = reuser(() -> new StringBuilder(INITIAL_CHARS_CAPACITY));
        private static final Reuser<IntArrayList> plainIndexToRunIndex = reuser(() -> new IntArrayList(INITIAL_CHARS_CAPACITY));
        private static final Reuser<FloatArrayList> plainIndexToWidth = reuser(() -> new FloatArrayList(INITIAL_CHARS_CAPACITY));
        private static final Reuser<FloatArrayList> plainIndexToTotalWidth = reuser(() -> new FloatArrayList(INITIAL_CHARS_CAPACITY));
        private static final Reuser<IntArrayList> runIndexToPlainBeginIndex = reuser(() -> new IntArrayList(INITIAL_RUNS_CAPACITY));
        private static final Reuser<ArrayList<RenderObject>> runIndexToObject = reuser(() -> new ArrayList<>(INITIAL_RUNS_CAPACITY));
        private static final Reuser<FloatArrayList> runIndexToHeight = reuser(() -> new FloatArrayList(INITIAL_RUNS_CAPACITY));
        private static final Reuser<FloatArrayList> runIndexToBaseline = reuser(() -> new FloatArrayList(INITIAL_RUNS_CAPACITY));
        private static final Reuser<FloatArrayList> runIndexToHyphenWidth = reuser(() -> new FloatArrayList(INITIAL_RUNS_CAPACITY));

        public static StringBuilder plainText() {
            return reuseStringBuilder(plainText);
        }

        public static IntArrayList plainIndexToRunIndex() { return reuseIntArrayList(plainIndexToRunIndex); }

        public static FloatArrayList plainIndexToWidth() { return reuseFloatArrayList(plainIndexToWidth); }

        public static FloatArrayList plainIndexToTotalWidth() { return reuseFloatArrayList(plainIndexToTotalWidth); }

        public static IntArrayList runIndexToPlainBeginIndex() { return reuseIntArrayList(runIndexToPlainBeginIndex); }

        public static ArrayList<RenderObject> runIndexToObject() { return reuseCollection(runIndexToObject); }

        public static FloatArrayList runIndexToHeight() { return reuseFloatArrayList(runIndexToHeight); }

        public static FloatArrayList runIndexToBaseline() { return reuseFloatArrayList(runIndexToBaseline); }

        public static FloatArrayList runIndexToHyphenWidth() { return reuseFloatArrayList(runIndexToHyphenWidth); }
    }
}
