package eu.oberon.oss.chess.pgn.tags;

import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;

/**
 * Default implementation of the {@link TagCreator}{@code <I,O>} interface.
 *
 * @param <I> Represents the parameter type of the data that is the input for the PGN Tag value
 * @param <O> Represents the parameter type of the data that is actually stored as the value of a PGN Tag.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
@Log4j2
public class DefaultTagCreator<I, O> implements TagCreator<I, O> {
    private final TagValueHandler<I, O> tagValueHandler;
    private final String tagName;
    private final boolean isRequired;

    protected DefaultTagCreator(@Nonnull String tagName, TagValueHandler<I, O> tagValueHandler, boolean isRequired) {
        if (tagName.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter: tagName");
        }
        this.tagValueHandler = tagValueHandler;
        this.tagName = tagName;
        this.isRequired = isRequired;
    }

    @Override
    public @Nonnull String getTagName() {
        return tagName;
    }

    @Override
    public PgnTag<O> createTag(I inputValue) {
        return new PgnTag<>() {
            private final O tagValue = tagValueHandler.getConverter().apply(inputValue);

            @Override
            public String getTagName() {
                return tagName;
            }

            @Override
            public O getTagValue() {
                return tagValue;
            }

            @Override
            public boolean isRequiredTag() {
                return isRequired;
            }
        };
    }

}
