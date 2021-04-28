package com.keisuki.reactive.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Parameters {
  private static final Parameters EMPTY = new Parameters(Map.of());

  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  private final Map<String, List<String>> parameters;

  private Parameters(final Map<String, List<String>> parameters) {
    this.parameters = parameters;
  }
  
  public Optional<String> get(final String key) {
    return Optional.ofNullable(parameters.get(key))
        .filter(list -> !list.isEmpty())
        .map(list -> list.get(0));
  }
  
  public List<String> getAll(final String key) {
    return Optional.ofNullable(parameters.get(key))
        .orElseGet(Collections::emptyList);
  }

  Map<String, List<String>> getParameters() {
    return parameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Parameters that = (Parameters) o;
    return Objects.equals(parameters, that.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parameters);
  }

  @Override
  public String toString() {
    return "Parameters{" +
        "parameters=" + parameters +
        '}';
  }

  public Builder toBuilder() {
    return newBuilder().withValueLists(parameters);
  }
  
  public static Parameters empty() {
    return EMPTY;
  }

  public static Builder newBuilder() {
    return new Builder();
  }
  
  public static class Builder {
    private final Map<String, List<String>> values = new LinkedHashMap<>();

    private Builder() {}

    public Builder withValue(final String key, final String value) {
      values.computeIfAbsent(key, k -> new LinkedList<>()).add(value);
      return this;
    }

    public Builder withValue(final String key, final List<String> value) {
      values.computeIfAbsent(key, k -> new LinkedList<>()).addAll(value);
      return this;
    }

    public Builder withValues(final Map<String, String> values) {
      values.forEach(this::withValue);
      return this;
    }

    public Builder withValueLists(final Map<String, List<String>> values) {
      values.forEach(this::withValue);
      return this;
    }

    public Parameters build() {
      return new Parameters(values.entrySet()
          .stream()
          .collect(Collectors.toUnmodifiableMap(
              Entry::getKey,
              entry -> Collections.unmodifiableList(entry.getValue()))));
    }
  }
}
