package eu.oberon.oss.chess.pgn.tags;

import jakarta.annotation.Nonnull;

/**
 * Allows the creation of specific tags.
 *
 * @param <I> Represents the parameter type of the data that is the input for the PGN Tag value
 * @param <O> Represents the parameter type of the data that is actually stored as the value of a PGN Tag.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface TagCreator<I, O> {
    /**
     * Returns the name of the tag that this creator is intended for.
     *
     * @return The tag name
     *
     * @since 1.0.0
     */
    @Nonnull
    String getTagName();

    /**
     * Creates a tag for the provided input value.
     *
     * @param inputValue The input value for the tag value.
     *
     * @return The created tag.
     *
     * @throws IllegalArgumentException if the specified 'inputValue' is not acceptable.
     * @since 1.0.0
     */
    @Nonnull
    PgnTag<O> createTag(I inputValue);
}
