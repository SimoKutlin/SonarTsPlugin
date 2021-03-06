package com.pablissimo.sonar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.measures.CoreMetrics;

public class CommentSensorImplTest {
    CommentSensor sensor;

    SensorContextTester sensorContext;

    @Before
    public void setUp() throws Exception {
        this.sensorContext = SensorContextTester.create(new File(""));
        this.sensor = new CommentSensorImpl();
    }

    @Test
    public void toStringReturnsClassName() {
        assertEquals("CommentSensorImpl", new CommentSensorImpl().toString());
    }

    @Test
    public void basicBlockCommentsDiscounted() throws FileNotFoundException {
        assertLineCount("blockcomments1", 1);
    }

    @Test
    public void blockCommentsNotConfusedWithNestedComments() throws FileNotFoundException {
        assertLineCount("blockcomments2", 6);
    }

    @Test
    public void linesEndingWithABlockCommentStillCounted() throws FileNotFoundException {
        assertLineCount("blockcomments3", 2);
    }

    @Test
    public void oneLineBlockCommentsDoNotConfuseCounting() throws FileNotFoundException {
        assertLineCount("blockcomments4", 1);
    }

    @Test
    public void oneLineBlockCommentAtEndOfRealLineShouldNotConsiderNextLinesAsComments() throws FileNotFoundException {
        assertLineCount("blockcomments5", 1);
    }

    @Test
    public void lineLevelCommentsAndWhitespaceHandledCorrectly() throws FileNotFoundException {
        assertLineCount("linecomments", 5);
    }

    private DefaultInputFile resource(String relativePath) {
        return new DefaultInputFile("", relativePath).setLanguage(TypeScriptLanguage.LANGUAGE_KEY);
    }

    private void assertLineCount(String testName, Integer expected) throws FileNotFoundException {
        DefaultInputFile resource = resource("src/test/resources/loc/" + testName + ".txt");
        this.sensorContext.fileSystem().add(resource);

        this.sensor.execute(this.sensorContext);

        assertEquals(expected, this.sensorContext.measure(resource.key(), CoreMetrics.COMMENT_LINES).value());
    }
}
