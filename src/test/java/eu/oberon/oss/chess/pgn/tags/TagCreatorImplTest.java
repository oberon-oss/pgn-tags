package eu.oberon.oss.chess.pgn.tags;

import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class TagCreatorImplTest {

    @Test
    void testBasicFunctionalityCorrectWithRequiredTag() {
        Predicate<Object> validator = Objects::nonNull;
        UnaryOperator<Object> converter = o -> "REQ-TEST-STRING";
        TagCreatorImpl<Object, Object> creator;

        creator = assertDoesNotThrow(() -> new TagCreatorImpl<>("required-tag", validator, converter, true));
        PgnTag<Object> tag = assertDoesNotThrow(() -> creator.createTag("NOT-THE-STRING"));

        assertEquals("required-tag", tag.getTagName());
        assertEquals("REQ-TEST-STRING", tag.getTagValue().toString());

        assertTrue(tag.isRequiredTag());
    }

    @Test
    void testBasicFunctionalityCorrectWithOptionalTag() {
        Predicate<Object> validator = Objects::nonNull;
        UnaryOperator<Object> converter = o -> "OPT-TEST-STRING";
        TagCreatorImpl<Object, Object> creator;

        creator = assertDoesNotThrow(() -> new TagCreatorImpl<>("optional-tag", validator, converter, false));
        PgnTag<Object> tag = assertDoesNotThrow(() -> creator.createTag("NOT-THE-STRING"));

        assertEquals("optional-tag", tag.getTagName());
        assertEquals("OPT-TEST-STRING", tag.getTagValue().toString());

        assertFalse(tag.isRequiredTag());
    }

    @Test
    void testWithInvalidTagNames() {
        assertThrows(NullPointerException.class, () -> new TagCreatorImpl<>(null, null, null, false));
        assertThrows(IllegalArgumentException.class, () -> new TagCreatorImpl<>("", null, null, false));
        assertThrows(IllegalArgumentException.class, () -> new TagCreatorImpl<>("   ", null, null, false));
    }


    @Test
    void testWithAllowedMissingTagValue() {
        Predicate<Object> validator = object -> true;
        UnaryOperator<Object> converter = o -> null;

        TagCreatorImpl<Object, Object> creator;

        creator = assertDoesNotThrow(() -> new TagCreatorImpl<>("empty-1", validator, converter, false));
        PgnTag<Object> tag = assertDoesNotThrow(() -> creator.createTag(null));
        assertNull(tag.getTagValue());
    }

    @Test
    void testWithNotAllowedNullValue() {
        Predicate<Object> validator = object -> false;
        UnaryOperator<Object> converter = o -> null;

        TagCreatorImpl<Object, Object> creator;

        creator = assertDoesNotThrow(() -> new TagCreatorImpl<>("empty-1", validator, converter, false));

        Exception e;
        e = assertThrows(IllegalArgumentException.class, () -> creator.createTag(null));
        assertEquals("Invalid input value <null> for tag 'empty-1'. Input value class=N/A.", e.getMessage());
    }

    @Test
    void testWithNotAllowedNonNullValue() {
        Predicate<Object> validator = object -> false;
        UnaryOperator<Object> converter = o -> null;

        TagCreatorImpl<Object, Object> creator;

        creator = assertDoesNotThrow(() -> new TagCreatorImpl<>("empty-2", validator, converter, false));

        Exception e;
        e = assertThrows(IllegalArgumentException.class, () -> creator.createTag(100));
        assertEquals("Invalid input value '100' for tag 'empty-2'. Input value class=java.lang.Integer.", e.getMessage());
    }
}