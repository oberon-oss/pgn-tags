package eu.oberon.oss.chess.pgn.tags;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generic class for handling PGN Tag values.
 *
 * @param <I> Represents the parameter type of the data that is the input for the PGN Tag value
 * @param <O> Represents the parameter type of the data that is actually stored as the value of a PGN Tag.
 *
 * @author TigerLilly64
 */
@SuppressWarnings("JavadocDeclaration")
@Getter
public final class TagValueHandler<I, O> {
    private static final TagValueValidator<String> STRING_VALIDATOR = s -> s != null && !s.isBlank();
    private static final TagValueValidator<Object> OBJECT_VALIDATOR = Objects::nonNull;

    private static final TagValueConverter<String, String> _CONVERTER = s -> {
        if (!STRING_VALIDATOR.test(s)) {
            throw new IllegalArgumentException("Value cannot be <null>, empty or white-space only.");
        }
        return s;
    };

    private static final TagValueHandler<String, String> _HANDLER = new TagValueHandler<>(STRING_VALIDATOR, _CONVERTER);


    /**
     * -- GETTER -- Returns validation predicate, to check if the input value is valid and/or can be converted in the
     * target data object for a PGN tag.
     *
     * @return The validator for this tag value handler.
     * @since 1.0.0
     */
    private final Predicate<I> validator;

    /**
     * -- GETTER -- Returns the actual convertor that will convert the input value into the appropriate data for the PGN
     * tag.
     *
     * @return The converter for the handler instance.
     * @since 1.0.0
     */
    private final Function<I, O> converter;

    @SuppressWarnings("unused")
    private TagValueHandler() {
        throw new IllegalStateException("Should never have happened...");
    }

    private TagValueHandler(Predicate<I> validator, Function<I, O> converter) {
        this.validator = Objects.requireNonNull(validator);
        this.converter = Objects.requireNonNull(converter);
    }


    /**
     * Creates a new Tag value handler instance.
     * <p>
     * This is a special method for those rare occasions where conversion of the object is possible, but there is no
     * specific validator that can be used. This method will provide a default validator, ensuring the object specified
     * for the conversion is not-null
     *
     * @param converter The actual converter to apply when converting the input {@code <I>} into the desired output
     *                  value {@code <O>}/
     * @param <O>       Represents the parameter type of the data that is actually stored as the value of a PGN Tag.
     *
     * @return An instance of the tag value for the provided validator and converter.
     */
    public static <O> TagValueHandler<Object, O> getInstance(@Nonnull Function<Object, O> converter) {
        return getInstance(OBJECT_VALIDATOR, converter);
    }

    /**
     * Creates a new Tag value handler instance, using a user specified validator
     *
     * @param validator The validator for the value handler.
     * @param converter The actual converter to apply when converting the input {@code <I>} into the desired output
     *                  value {@code <O>}/
     * @param <I>       Represents the parameter type of the data that is the input for the PGN Tag value
     * @param <O>       Represents the parameter type of the data that is actually stored as the value of a PGN Tag.
     *
     * @return An instance of the tag value for the provided validator and converter.
     */
    public static <I, O> TagValueHandler<I, O> getInstance(@Nonnull Predicate<I> validator, @Nonnull Function<I, O> converter) {
        return new TagValueHandler<>(validator, converter);
    }

    /**
     * Returns a handler for standard tag values, where input and output are both Strings.
     * <p>
     * The validator used will only check if the string is non-null, is not empty or contains white space only.
     *
     * @return A Value handler instance that works for Strings as in- and output typ.
     */
    public static TagValueHandler<String, String> getDefaultHandler() {
        return _HANDLER;
    }
}
