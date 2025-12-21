package eu.oberon.oss.chess.pgn.tags;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class DefaultTagCreatorTest {


    @Test
    void testInvalidTagNames() {
        String msg = "Parameter: tagName";

        TagValueHandler<String, String> handler = TagValueHandler.getDefaultHandler();
        //noinspection DataFlowIssue - Provoke a NPE intentinonally
        assertThrows(NullPointerException.class, () -> new DefaultTagCreator<>(null, handler, true));

        IllegalArgumentException e;

        e = assertThrows(IllegalArgumentException.class, () -> new DefaultTagCreator<>("", handler, true));
        assertEquals(msg, e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> new DefaultTagCreator<>("     ", handler, true));
        assertEquals(msg, e.getMessage());
    }

    @Test
    void testInvalidTagValues() {
        TagValueHandler<String, String> handler = TagValueHandler.getDefaultHandler();
        DefaultTagCreator<String, String> creator = new DefaultTagCreator<>("TAG-VALUE-TEST", handler, true);

        assertThrows(IllegalArgumentException.class, () -> creator.createTag(null));
        assertThrows(IllegalArgumentException.class, () -> creator.createTag(""));
        assertThrows(IllegalArgumentException.class, () -> creator.createTag("     "));
    }

    @Test
    void testTagWithObjectPayload() {
        var handler = TagValueHandler.getInstance((Function<Object, Object>) Object::toString);
        Object test = new Object();
        String toString = test.toString();

        assertTrue(handler.getValidator().test(test));
        assertEquals(toString, handler.getConverter().apply(test));
    }

    @Test
    void testRequiredTagCreator() {
        DefaultTagCreator<String, String> creator = new DefaultTagCreator<>(
                "REQ-TEST", TagValueHandler.getDefaultHandler(), true
        );

        PgnTag<String> tag = creator.createTag("converted-value");
        assertTrue(tag.isRequiredTag());
        assertEquals("REQ-TEST", tag.getTagName());
        assertEquals(tag.getTagName(), creator.getTagName());
        assertEquals("converted-value", tag.getTagValue());
        assertEquals("[REQ-TEST \"converted-value\"]", tag.getFormattedTag());
    }

    @Test
    void testNonRequiredTagCreator() {
        DefaultTagCreator<String, Integer> creator = new DefaultTagCreator<>("OPTIONAL",
                TagValueHandler.getInstance(_ -> true, _ -> 1234), false
        );

        PgnTag<Integer> tag = creator.createTag("value");
        assertFalse(tag.isRequiredTag());
        assertEquals("OPTIONAL", tag.getTagName());
        assertEquals(tag.getTagName(), creator.getTagName());
        assertEquals(1234, tag.getTagValue());
        assertEquals("[OPTIONAL \"1234\"]", tag.getFormattedTag());
    }
}