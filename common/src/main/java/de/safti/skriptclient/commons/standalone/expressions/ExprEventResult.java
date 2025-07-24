package de.safti.skriptclient.commons.standalone.expressions;

import de.safti.skriptclient.api.SkriptRegistry;
import de.safti.skriptclient.api.event.ResultingEventContext;
import de.safti.skriptclient.api.exceptions.SyntaxRuntimeException;
import de.safti.skriptclient.api.pattern.PatternBundle;
import de.safti.skriptclient.api.pattern.PatternBundleBuilder;
import de.safti.skriptclient.api.synatxes.AbstractExpression;
import de.safti.skriptclient.api.synatxes.expression.Settable;
import de.safti.skriptclient.api.synatxes.generated.GeneratedResultingEvent;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import io.github.syst3ms.skriptparser.parsing.SkriptParserException;
import io.github.syst3ms.skriptparser.types.conversions.Converters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ExprEventResult extends AbstractExpression<Object> implements Settable {

    public static final PatternBundle PATTERNS =
            PatternBundleBuilder.builder()
                    .newPattern()
                        .literal("event[-| ]result")
                        .build()
                    .build();
    private static final Logger log = LoggerFactory.getLogger(ExprEventResult.class);

    static {
        SkriptRegistry.registerExpression(ExprEventResult.class, Object.class, false, PATTERNS);
    }


    private Class<?> personallySeenClass;

    @Override
    public boolean validate(int matchedPattern, ParseContext context) {
        SkriptLogger logger = context.getLogger();
        ParserState state = context.getParserState();
        SkriptEvent currentEvent = state.getCurrentEvent();
        if(currentEvent == null)
            throw new SkriptParserException("Could not get current event.");

        // validate instance of event
        if(!(currentEvent instanceof GeneratedResultingEvent<?> resultingEvent)) {
            String eventStr = currentEvent.toString(null, logger.isDebug());
            logger.error(eventStr + " cannot have an event-result set!", ErrorType.SEMANTIC_ERROR);
            return false;
        }

        personallySeenClass = resultingEvent.getResultClass();
        return true;
    }

    @Override
    public boolean isSingle() {
        return !personallySeenClass.isArray();
    }


    @Override
    public void set(Object[] delta, TriggerContext context) {
        if(!(context instanceof ResultingEventContext<?, ?> resultContext)) {
            throw new SyntaxRuntimeException("Invalid trigger context provided to ExprEventResult! Expected: EventContext");
        }
        Class<?> classExpectedByEvent = resultContext.getResultClass();
        if(isSingle()) {
            safeSetSingleHelper(resultContext, delta, classExpectedByEvent);
            return;
        }
        safeSetPluralHelper(resultContext, delta, classExpectedByEvent);
    }

    public <T> void safeSetSingleHelper(ResultingEventContext<?, ?> context, Object[] delta, Class<T> resultClass) {
        //noinspection unchecked
        safeSetSingle((ResultingEventContext<?, T>) context, delta, resultClass);
    }

    public <T> void safeSetSingle(ResultingEventContext<?, T> context, Object[] delta, Class<T> resultClass) {
        // cannot escape the unchecked casts
        T tArr = Converters.convert(delta[0], resultClass).orElse(null);
        context.setResult(tArr);
    }


    public <T> void safeSetPluralHelper(ResultingEventContext<?, ?> context, Object[] delta, Class<?> resultClass) {
        //noinspection unchecked
        safeSetPlural((ResultingEventContext<?, T[]>) context, delta, resultClass);
    }

    public <T> void safeSetPlural(ResultingEventContext<?, T[]> context, Object[] delta, Class<?> resultClass) {
        // cannot escape the unchecked casts
        //noinspection unchecked
        T[] tArr = (T[]) Converters.convertArray(delta, resultClass).orElse(null);
        context.setResult(tArr);
    }



    @Override
    public Object[] getValues(@NotNull TriggerContext context) {
        if(!(context instanceof ResultingEventContext<?, ?> resultContext)) {
            throw new SyntaxRuntimeException("Invalid trigger context provided to ExprEventResult! Expected: EventContext");
        }

        Class<?> classExpectedByEvent = resultContext.getResultClass();
        return safeGetPluralHelper(resultContext, personallySeenClass, classExpectedByEvent).orElse(null);
    }

    // this method is required.
    // it simply casts the first argument to E
    // so that safeGetPlural can be run
    public <P, E> Optional<P[]> safeGetPluralHelper(@NotNull ResultingEventContext<?, ?> context, Class<P> personallyExpectedClass, Class<E> classExpectedByEvent) {
        //noinspection unchecked
        return safeGetPlural((ResultingEventContext<?, E[]>) context, personallyExpectedClass, classExpectedByEvent);
    }

    /**
     * @param context the trigger context
     * @param personallyExpectedClass The class of the object we should return
     * @param classExpectedByEvent The class of the ResultingEventContext's result field
     * @param <P> the Personally expected type - what we should return
     * @param <E> the Externally expected type - what we should expect the ResultingEventContext's result type to be
     * @return a from E to P converted value
     */
    public <P, E> Optional<P[]> safeGetPlural(@NotNull ResultingEventContext<?, E[]> context, Class<P> personallyExpectedClass, Class<E> classExpectedByEvent) {
        var converter = Converters.getConverter(classExpectedByEvent, personallyExpectedClass);

        if(converter.isEmpty()) {
            // TODO: replace with RuntimeLogger once available
            log.error("Failed to provide plural event-result!");
            log.error("Could not convert arrays {} to {}!", classExpectedByEvent,  personallyExpectedClass);
            return Optional.empty();
        }

        Object[] result = context.getResult();

        // cannot escape the unchecked casts
        return Converters.convertArray(result, personallyExpectedClass);
    }

    @Override
    public Optional<?> getSingle(@NotNull TriggerContext context) {
        if(!(context instanceof ResultingEventContext<?, ?> resultContext)) {
            throw new SyntaxRuntimeException("Invalid trigger context provided to ExprEventResult! Expected: EventContext");
        }

        Class<?> classExpectedByEvent = resultContext.getResultClass();
        return safeGetSingleHelper(resultContext, personallySeenClass, classExpectedByEvent);
    }

    // this method is required.
    // it simply casts the first argument to E
    // so that safeGetSingle can be run
    public <P, E> Optional<P> safeGetSingleHelper(@NotNull ResultingEventContext<?, ?> context, Class<P> personallyExpectedClass, Class<E> classExpectedByEvent) {
        //noinspection unchecked
        return safeGetSingle((ResultingEventContext<?, E>) context, personallyExpectedClass, classExpectedByEvent);
    }

    /**
     * @param context the trigger context
     * @param personallyExpectedClass The class of the object we should return
     * @param classExpectedByEvent The class of the ResultingEventContext's result field
     * @param <P> the Personally expected type - what we should return
     * @param <E> the Externally expected type - what we should expect the ResultingEventContext's result type to be
     * @return a from E to P converted value
     */
    public <P, E> Optional<P> safeGetSingle(@NotNull ResultingEventContext<?, E> context, Class<P> personallyExpectedClass, Class<E> classExpectedByEvent) {
        var converter = Converters.getConverter(classExpectedByEvent, personallyExpectedClass);

        if(converter.isEmpty()) {
            // TODO: replace with RuntimeLogger once available
            log.error("Failed to provide event-result!");
            log.error("Could not convert {} to {}!", classExpectedByEvent,  personallyExpectedClass);
            return Optional.empty();
        }

        // cannot escape the unchecked casts
        //noinspection unchecked
        return (Optional<P>) converter.get().apply(context.getResult());
    }



    @Override
    protected @Nullable Class<?>[] getSupportedChangeValues() {
        return new Class[] {personallySeenClass};
    }

    @Override
    public @NotNull PatternBundle getPatternBundle() {
        return PATTERNS;
    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        return "event-result";
    }
}
