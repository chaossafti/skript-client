package de.safti.skriptclient.utils;

import io.github.syst3ms.skriptparser.lang.CodeSection;
import io.github.syst3ms.skriptparser.parsing.ParserState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ParserUtils {

    public static <S extends CodeSection> Optional<? extends S> getLinkedSection(ParserState parserState,
                                                                          Class<? extends S> sectionClass) {
        return getLinkedSection(parserState, sectionClass, l -> l.stream().findFirst());
    }

    public static <S extends CodeSection> Optional<? extends S> getLinkedSection(ParserState parserState,
                                                                          Class<? extends S> sectionClass,
                                                                          Function<? super List<? extends S>, Optional<? extends S>> selector) {
        return selector.apply(getMatchingSections(parserState, sectionClass));
    }

    static <S extends CodeSection> List<? extends S> getMatchingSections(ParserState parserState,
                                                                         Class<? extends S> sectionClass) {
        List<S> result = new ArrayList<>();
        for (var section : parserState.getCurrentSections()) {
            if (sectionClass.isAssignableFrom(section.getClass())) {
                result.add((S) section);
            }
        }
        return result;
    }

}
