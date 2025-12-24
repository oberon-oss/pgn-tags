package eu.oberon.oss.chess.pgn.tags;

import nl.altindag.log.LogCaptor;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class CreatorRegistryTest {
    LogCaptor logCaptor = LogCaptor.forClass(CreatorRegistry.class);

    private static final Set<String> SEVEN_TAG_ROSTER_NAMES = Set.of(
            "Event", "Site", "Date", "Round", "White", "Black", "Result"
    );

    private CreatorRegistry registry;

    @BeforeEach
    void init() {
        registry = CreatorRegistry.getDefaultInstance(SEVEN_TAG_ROSTER_NAMES, true);
    }


    @Test
    void testSevenTagRosterList() {
        assertEquals(List.of("Event", "Site", "Date", "Round", "White", "Black", "Result"), CreatorRegistry.getSevenTagRosterNames());
    }

    @Test
    void testIfRegistryIsEmptyAsExpected() {
        assertTrue(new CreatorRegistry().getKnownTagNames().isEmpty());
    }

    @Test
    void testRegisterTagCreator() {
        Predicate<Object> validator = Objects::nonNull;
        UnaryOperator<Object> converter = o -> "TEST-VALUE";
        TagCreatorImpl<Object, Object> creator;
        creator = new TagCreatorImpl<>("required-tag", validator, converter, true);

        registry.registerTagCreator(creator);

        assertNotNull(registry.getInstance("required-tag"));
    }

    @Test
    void testAttemptToRegisterExistingCreator() {
        Predicate<Object> validator = Objects::nonNull;
        UnaryOperator<Object> converter = o -> "TEST-VALUE";
        TagCreatorImpl<Object, Object> creator;
        creator = new TagCreatorImpl<>("event", validator, converter, true);

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> registry.registerTagCreator(creator));
        assertEquals("A tag creator already exists for tag name 'event'", e.getMessage());
    }

    @Test
    void testReplaceExistingTagCreator() {
        Predicate<Object> validator = Objects::nonNull;
        UnaryOperator<Object> converter = o -> "TEST-VALUE";
        TagCreatorImpl<Object, Object> creator;
        creator = new TagCreatorImpl<>("event", validator, converter, true);
        assertDoesNotThrow(() -> registry.replaceTagCreator(creator));
        assertTrue(logCaptor.getInfoLogs().contains("Replaced tag creator for tag 'event'"));
    }

    @Test
    void testReplaceNonExistentTagCreator() {
        Predicate<Object> validator = Objects::nonNull;
        UnaryOperator<Object> converter = o -> "TEST-VALUE";
        TagCreatorImpl<Object, Object> creator;
        creator = new TagCreatorImpl<>("a-new-tag", validator, converter, true);
        assertDoesNotThrow(() -> registry.replaceTagCreator(creator));

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