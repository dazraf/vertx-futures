package io.dazraf.vertx.futures.processors;

import java.util.function.Function;

import io.dazraf.vertx.futures.Futures;
import io.vertx.core.Future;

/**
 * A unit that processes the state of a {@link Futures} chain; returning another {@link Future}
 * @param <T1> the type expected from the {@link Futures} chain
 * @param <T2> the type emitted as a {@link Future} from the processor
 */
@FunctionalInterface
public interface FutureProcessor<T1, T2> extends Function<Future<T1>, Future<T2>> {
}
