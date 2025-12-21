package eu.oberon.oss.chess.pgn.tags;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TagCreatorRegistryTest {
    private static final Set<String> SEVEN_TAG_ROSTER_NAMES = Set.of(
            "Event", "Site", "Date", "Round", "White", "Black", "Result"
    );

    private static final Set<String> WELL_KNOWN_TAGS = Set.of("EventDate", "EventSponsor", "Section", "Stage",
            "Board", "Time", "UTCDate", "UTCTime", "WhiteElo", "WhiteUSCF", "BlackUSCF", "BlackElo", "WhiteTitle",
            "BlackTitle", "TimeControl", "Termination", "Setup", "FEN", "Opening", "Variation", "SubVariation", "ECO",
            "NIC", "Annotator", "Mode", "PlyCount");

    private TagCreatorRegistry registry;

    @BeforeEach
    void init() {
        registry = TagCreatorRegistry.getDefaultInstance(SEVEN_TAG_ROSTER_NAMES, true);
        registry.addDefaultCreators(WELL_KNOWN_TAGS, false);
    }


    @Test
    void testSevenTagRosterList() {
        assertEquals(List.of("Event", "Site", "Date", "Round", "White", "Black", "Result"), TagCreatorRegistry.getSevenTagRosterNames());
    }

    @Test
    void testRegisterTagCreator() {
        registry.registerTagCreator(new DefaultTagCreator<>("NewTag", TagValueHandler.getDefaultHandler(), false));

        TagCreator<String, String> creator = assertDoesNotThrow(() -> registry.getInstance("NewTag"));
        assertNotNull(creator);
        PgnTag<String> tag = assertDoesNotThrow(() -> creator.createTag("Some Value"));

        assertEquals("NewTag", tag.getTagName());
        assertEquals("Some Value", tag.getTagValue());
        assertFalse(tag.isRequiredTag());
    }

    @Test
    void testIfRegistryIsEmptyAsExpected() {
        assertTrue(new TagCreatorRegistry().getKnownTagNames().isEmpty());
    }

    @Test
    void testRegisterExistingTagHandler() {
        DefaultTagCreator<String, String> creator = new DefaultTagCreator<>("Event", TagValueHandler.getDefaultHandler(), false);
        assertThrows(IllegalStateException.class, () -> registry.registerTagCreator(creator));
    }


    @Test
    void testReplaceExistingTagCreator() {
        assertTrue(registry.replaceTagCreator(new DefaultTagCreator<>("Round",
                TagValueHandler.getInstance(
                        (TagValueValidator<String>) s -> Pattern.matches("\\d+", s),
                        (TagValueConverter<String, Integer>) Integer::parseInt
                ), true))
        );

        TagCreator<String, Integer> creator = registry.getInstance("Round");
        assertNotNull(creator);
        PgnTag<Integer> tag = assertDoesNotThrow(() -> creator.createTag("1234"));
        assertEquals(1234, tag.getTagValue());
        assertTrue(tag.isRequiredTag());

    }

    @Test
    void testReplaceNonExistentTagCreator() {
        assertFalse(registry.replaceTagCreator(new DefaultTagCreator<>("This-does-not-exist",
                TagValueHandler.getInstance(
                        (TagValueValidator<String>) s -> Pattern.matches("\\d+", s),
                        (TagValueConverter<String, Integer>) Integer::parseInt
                ), true))
        );
    }

    @Test
    void testRegisteringAnInvalidCreator() {
        TagCreator<String, String> tagCreator = new TagCreator<>() {
            @Override
            public @NonNull String getTagName() {
                return "";
            }

            @Override
            public @NonNull PgnTag<String> createTag(String inputValue) {
                return new PgnTag<>() {
                    @Override
                    public String getTagName() {
                        return "";
                    }

                    @Override
                    public String getTagValue() {
                        return "";
                    }

                    @Override
                    public boolean isRequiredTag() {
                        return false;
                    }
                };
            }
        };

        assertThrows(IllegalArgumentException.class, () -> registry.registerTagCreator(tagCreator));
    }
}