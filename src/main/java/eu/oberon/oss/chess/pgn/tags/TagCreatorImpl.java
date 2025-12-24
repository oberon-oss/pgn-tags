package eu.oberon.oss.chess.pgn.tags;

import jakarta.annotation.Nonnull;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;
import java.util.function.Predicate;


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
public class TagCreatorImpl<I, O> implements TagCreator<I, O> {
    private final String tagName;
    private final boolean isRequired;
    private final Predicate<I> validator;
    private final Function<I, O> converter;

    /**
     * Creates a new, generic tag creator.
     *
     * @param tagName    The name of PGN tag for which this creator is/will be applicable.
     * @param validator  The validator to use. The input value of type {@code <I>}  will be validated before calling the
     *                   converter.
     * @param converter  The converter to transform objects of type {@code <I>} into objects of type {@code <O>}
     * @param isRequired Specifies if the tag is a required tag, i.e. if, when processing PGN data, this tag is expected
     *                   to be part of the tag section of the PGN data.
     *
     * @since 1.0.0
     */
    public TagCreatorImpl(@Nonnull String tagName, @Nonnull Predicate<I> validator, @Nonnull Function<I, O> converter, boolean isRequired) {
        if (tagName.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter: tagName");
        }
        this.validator = validator;
        this.converter = converter;
        this.isRequired = isRequired;
        this.tagName = tagName;
    }

    @Override
    public @Nonnull String getTagName() {
        return tagName;
    }

    private static final String ARG_ERROR_FORMAT = "Invalid input value %s for tag '%s'. Input value class=%s.";

    @Override
    public @Nonnull PgnTag<O> createTag(I inputValue) {
        if (!validator.test(inputValue)) {
            String msg;
            if (inputValue == null) {
                msg = String.format(ARG_ERROR_FORMAT, "<null>", tagName, "N/A");
            } else {
                msg = String.format(ARG_ERROR_FORMAT, "'" + inputValue + "'", tagName, inputValue.getClass().getName());
            }
            throw new IllegalArgumentException(msg);
        }

        return new PgnTag<>() {
            private final O tagValue = converter.apply(inputValue);

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
