package eu.oberon.oss.chess.pgn.tags.creators;

import eu.oberon.oss.chess.pgn.tags.PgnTag;
import eu.oberon.oss.chess.pgn.tags.TagCreator;
import eu.oberon.oss.chess.pgn.tags.TagCreatorImpl;
import lombok.extern.log4j.Log4j2;

import java.util.function.UnaryOperator;

/**
 * Handles tag creation for tags that have a {@link String} payload as data object.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
@Log4j2
public class TagCreatorStringValue extends TagCreatorImpl<String, String> {

    private TagCreatorStringValue(String tagName, boolean allowEmptyTagValue, boolean isRequired) {
        super(
                tagName,
                s -> {
                    if (allowEmptyTagValue && (s == null || s.isBlank())) {
                        return true;
                    }
                    return (s != null) && !s.isBlank();
                },
                (UnaryOperator<String>) s -> {
                    if (s == null || s.isBlank()) {
                        LOGGER.warn("No value specified for tag {}; returned an empty string", tagName);
                        return null;
                    }
                    return s;
                },
                isRequired
        );
    }

    /**
     * Constructs a new tag creator for {@link PgnTag} instances that have a value (payload) of type {@link String}.
     * <p>
     * When using the creator to create a tag when missing tag values are allowed, the tag value returned will be
     * {@literal <null>}.
     *
     * @param tagName            The name of PGN tag for which this creator is/will be applicable.
     * @param allowEmptyTagValue If the value for a tag of this type is allowed to be null, empty, or contain blanks
     *                           only
     * @param isRequired         Specifies if the tag is a required tag, i.e. if, when processing PGN data, this tag is
     *                           expected to be part of the tag section of the PGN data.
     *
     * @return An instance of the TagCreator interface.
     *
     * @since 1.0.0
     */
    public static TagCreator<String, String> getInstance(String tagName, boolean allowEmptyTagValue, boolean isRequired) {
        return new TagCreatorStringValue(tagName, allowEmptyTagValue, isRequired);
    }
}
