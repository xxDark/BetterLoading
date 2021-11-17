package dev.xdark.betterloading.internal;

import java.util.List;
import java.util.function.Predicate;

public final class Predicates {

  private static final Predicate<?> TRUE = __ -> true;
  private static final Predicate<?> FALSE = __ -> false;

  private Predicates() {}

  public static <T> Predicate<T> alwaysTrue() {
    return (Predicate<T>) TRUE;
  }

  public static <T> Predicate<T> alwaysFalse() {
    return (Predicate<T>) FALSE;
  }

  public static <T> Predicate<T> or(Predicate<T> a, Predicate<? super T> b) {
    return a.or(b);
  }

  public static <T> Predicate<T> or(
      Predicate<? super T> a, Predicate<? super T> b, Predicate<? super T> c) {
    return t -> a.test(t) || b.test(t) || c.test(t);
  }

  public static <T> Predicate<T> or(
      Predicate<? super T> a, Predicate<T> b, Predicate<? super T> c, Predicate<? super T> d) {
    return t -> a.test(t) || b.test(t) || c.test(t) || d.test(t);
  }

  public static <T> Predicate<T> or(
      Predicate<? super T> a,
      Predicate<? super T> b,
      Predicate<? super T> c,
      Predicate<? super T> d,
      Predicate<? super T> e) {
    return t -> a.test(t) || b.test(t) || c.test(t) || d.test(t) || e.test(t);
  }

  public static <T> Predicate<T> or(List<Predicate<T>> predicates) {
    int j = predicates.size();
    if (j == 0) {
      return alwaysTrue();
    }
    Predicate<T> first = predicates.get(0);
    if (--j == 0) {
      return first;
    }
    int i = 1;
    while (j >= 4) {
      first =
          or(
              first,
              predicates.get(i),
              predicates.get(i + 1),
              predicates.get(i + 2),
              predicates.get(i + 3));
      j -= 4;
      i += 4;
    }
    while (j >= 3) {
      first = or(first, predicates.get(i), predicates.get(i + 1), predicates.get(i + 2));
      j -= 3;
      i += 3;
    }
    while (j >= 2) {
      first = or(first, predicates.get(i), predicates.get(i + 1));
      j -= 2;
      i += 2;
    }
    if (j != 0) {
      first = or(first, predicates.get(i));
    }
    return first;
  }

  public static <T> Predicate<T> and(Predicate<T> a, Predicate<? super T> b) {
    return a.and(b);
  }

  public static <T> Predicate<T> and(
      Predicate<? super T> a, Predicate<? super T> b, Predicate<? super T> c) {
    return t -> a.test(t) && b.test(t) && c.test(t);
  }

  public static <T> Predicate<T> and(
      Predicate<? super T> a,
      Predicate<? super T> b,
      Predicate<? super T> c,
      Predicate<? super T> d) {
    return t -> a.test(t) && b.test(t) && c.test(t) && d.test(t);
  }

  public static <T> Predicate<T> and(
      Predicate<? super T> a,
      Predicate<? super T> b,
      Predicate<? super T> c,
      Predicate<? super T> d,
      Predicate<? super T> e) {
    return t -> a.test(t) && b.test(t) && c.test(t) && d.test(t) && e.test(t);
  }

  public static <T> Predicate<T> and(List<Predicate<T>> predicates) {
    int j = predicates.size();
    if (j == 0) {
      return alwaysTrue();
    }
    Predicate<T> first = predicates.get(0);
    if (--j == 0) {
      return first;
    }
    int i = 1;
    while (j >= 4) {
      first =
          and(
              first,
              predicates.get(i),
              predicates.get(i + 1),
              predicates.get(i + 2),
              predicates.get(i + 3));
      j -= 4;
      i += 4;
    }
    while (j >= 3) {
      first = and(first, predicates.get(i), predicates.get(i + 1), predicates.get(i + 2));
      j -= 3;
      i += 3;
    }
    while (j >= 2) {
      first = and(first, predicates.get(i), predicates.get(i + 1));
      j -= 2;
      i += 2;
    }
    if (j != 0) {
      first = and(first, predicates.get(i));
    }
    return first;
  }
}
