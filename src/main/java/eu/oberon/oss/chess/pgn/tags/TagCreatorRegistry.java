package eu.oberon.oss.chess.pgn.tags;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static eu.oberon.oss.chess.pgn.tags.TagValueHandler.getDefaultHandler;

/**
 * A registry for PGN Tag creators.
 */
@Log4j2
public class TagCreatorRegistry {

    private final Map<String, TagCreator<?, ?>> tagCreatorMap = new ConcurrentHashMap<>();

    /**
     * Returns a set of tag names for which currently a tag created is defined.
     *
     * @return A set of 0 or more names.
     *
     * @since 1.0.0
     */
    public Set<String> getKnownTagNames() {
        return Set.copyOf(tagCreatorMap.keySet());
    }

    /**
     * Looks up the tag creator for the specified tag name.
     * <p>
     * The lookup is performed case-insensitive.
     *
     * @param tagName The tagname to retrieve the tag creator for.
     * @param <I>     Represents the parameter type of the data that is the input for the PGN Tag value
     * @param <O>     Represents the parameter type of the data that is actually stored as the value of a PGN Tag.
     *
     * @return The {@link TagCreator}{@code <I,O>}  that was found, or {@code <null>} if no creator was found for the
     * specified name.
     *
     * @since 1.0.0
     */
    public <I, O> @Nullable TagCreator<I, O> getInstance(String tagName) {
        //noinspection unchecked
        return (TagCreator<I, O>) tagCreatorMap.get(tagName.toLowerCase());
    }

    /**
     * Registers a new tag creator.
     * <p>
     * The tag creator is registered for the name specified by the {@link TagCreator#getTagName()} method.
     * <p>
     * The lookup is performed case-insensitive.
     *
     * @param tagCreator The tag creator to register.
     * @param <I>        Represents the parameter type of the data that is the input for the PGN Tag value
     * @param <O>        Represents the parameter type of the data that is actually stored as the value of a PGN Tag.
     *
     * @throws IllegalStateException if an attempt is made to store a tag creator that has already been registered for
     *                               the tag name
     * @since 1.0.0
     */
    public <I, O> void registerTagCreator(@Nonnull TagCreator<I, O> tagCreator) {
        if (tagCreator.getTagName().isEmpty()) {
            throw new IllegalArgumentException("tagCreator.getTagName()");
        }

        if (tagCreatorMap.containsKey(tagCreator.getTagName().toLowerCase())) {
            throw new IllegalStateException("A tag creator already exists for tag name '" + tagCreator.getTagName() + "'");
        }
        tagCreatorMap.put(tagCreator.getTagName().toLowerCase(), tagCreator);
        LOGGER.debug("Registered tag creator for tag '{}'", tagCreator.getTagName());
    }

    /**
     * Stores/Replaces an existing tag with the provided one.
     * <p>
     * If there was no tag creator registered for the name, this method will function the same as a call to the
     * {@link #registerTagCreator(TagCreator)} method would.
     * <p>
     * The difference between this method and {@link #registerTagCreator(TagCreator)} lies in the fact that
     * <b><u>this</u></b> method  will not throw an exception if an attempt is made to replace an existing tag creator.
     * <p>
     * The lookup is performed case-insensitive.
     *
     * @param tagCreator The creator to be replaced.
     * @param <I>        Represents the parameter type of the data that is the input for the PGN Tag value
     * @param <O>        Represents the parameter type of the data that is actually stored as the value of a PGN Tag.
     *
     * @return <b>True</b> if an existing method was replaced, or <b>false</b> if it was added as a new method.
     *
     * @since 1.0.0
     */
    public <I, O> boolean replaceTagCreator(@Nonnull TagCreator<I, O> tagCreator) {
        boolean replaced = tagCreatorMap.replace(tagCreator.getTagName().toLowerCase(), tagCreator) != null;
        if (replaced) {
            LOGGER.info("Replaced tag creator for tag '{}'", tagCreator.getTagName());
        }
        return replaced;
    }

    private static final List<String> SEVEN_TAG_ROSTER_NAMES = List.of("Event", "Site", "Date", "Round", "White", "Black", "Result");

    /**
     * List of the minimum required tags for a PGN chess game representation.
     *
     * @return An immutable list of the seven tags that are required for a valid PGN representation of a chess game.
     *
     * @since 1.0.0
     */
    public static List<String> getSevenTagRosterNames() {
        return SEVEN_TAG_ROSTER_NAMES;
    }

    /**
     * Creates a {@link DefaultTagCreator}{@code <String,String>} for all tag names specified in the provided set of
     * names.
     *
     * @param tagNames    A set of 0 or more tag names for which to create a default tag creator.
     * @param areRequired Specifies if the tag creators are for required tags (<b>true</b>) or not (<b>false</b>)
     *
     * @return The initialized TagCreatorRegistry
     *
     * @since 1.0.0
     */
    public static TagCreatorRegistry getDefaultInstance(Set<String> tagNames, boolean areRequired) {
        TagCreatorRegistry registry = new TagCreatorRegistry();
        registry.addDefaultCreators(tagNames, areRequired);
        return registry;
    }

    /**
     * Adds 0 or more {@link DefaultTagCreator}{@code <String,String>} for all tag names specified in the provided set
     * of names.
     *
     * @param tagNames    A set of 0 or more tag names for which to create a default tag creator.
     * @param areRequired Specifies if the tag creators are for required tags (<b>true</b>) or not (<b>false</b>)
     *
     * @since 1.0.0
     */
    public void addDefaultCreators(Set<String> tagNames, boolean areRequired) {
        for (String name : tagNames) {
            registerTagCreator(new DefaultTagCreator<>(name, getDefaultHandler(), areRequired));
        }
    }
}
