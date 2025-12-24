package eu.oberon.oss.chess.pgn.tags.creators;

import eu.oberon.oss.chess.pgn.tags.PgnTag;
import eu.oberon.oss.chess.pgn.tags.TagCreator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagCreatorIntegerValueTest {

    @Test
    void testWithValidValuesNoAllowingMissingValues() {
        TagCreator<String, Integer> creator = TagCreatorIntegerValue.getInstance("int-1", false, false);
        PgnTag<Integer> tag;
        
        tag = assertDoesNotThrow(() -> creator.createTag("1234"));
        assertEquals(1234,tag.getTagValue());
        
        tag = assertDoesNotThrow(() -> creator.createTag("+10"));
        assertEquals(10,tag.getTagValue());
        
        tag = assertDoesNotThrow(() -> creator.createTag("-20"));
        assertEquals(-20,tag.getTagValue());
        
        assertThrows(IllegalArgumentException.class, () -> creator.createTag("not-an-integer"));
        assertThrows(IllegalArgumentException.class, () -> creator.createTag(null));
    }

    @Test
    void testWithValidValuesAllowMissingValues() {
        TagCreator<String, Integer> creator = TagCreatorIntegerValue.getInstance("int-1", true, false);
        PgnTag<Integer> tag;
        
        tag = assertDoesNotThrow(() -> creator.createTag(null));
        assertNull(tag.getTagValue());
        
        tag = assertDoesNotThrow(() -> creator.createTag(""));
        assertNull(tag.getTagValue());
        
        tag = assertDoesNotThrow(() -> creator.createTag("   "));
        assertNull(tag.getTagValue());
        
        tag = assertDoesNotThrow(() -> creator.createTag(null));
        assertNull(tag.getTagValue());

        assertThrows(IllegalArgumentException.class, () -> creator.createTag("not-an-integer"));
    }
}