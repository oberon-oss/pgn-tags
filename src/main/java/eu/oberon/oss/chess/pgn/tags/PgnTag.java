package eu.oberon.oss.chess.pgn.tags;

/**
 * Defines a PGN tag.
 *
 * @param <I> Defines the data type of data stored with the tag.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface PgnTag<I> {
    /**
     * Returns the name of the tag.
     *
     * @return The tag's name
     *
     * @since 1.0.0
     */
    String getTagName();

    /**
     * Returns the value associated with the tag instance.
     *
     * @return The tag's value.
     *
     * @since 1.0.0
     */
    I getTagValue();

    /**
     * Returns a flag indicating if the PGN tag is required. A required tag is a tag that is expected to exist in a PGN
     * format game.
     *
     * @return <b>True</b> if required, false otherwise
     *
     * @since 1.0.0
     */
    boolean isRequiredTag();

    /**
     * Returns the tag name and value as it should appear in a PGN representation of a chess game.
     *
     * @return The formatted PGN tag.
     *
     * @since 1.0.0
     */
    default String getFormattedTag() {
        return String.format("[%s \"%s\"]", getTagName(), getTagValue());
    }
    
}
