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
  
  private final Map<String, String[]> parameters;

  private Parameters(final Map<String, String[]> parameters) {
    this.parameters = parameters;
  }
  
  public Optional<String> get(final String key) {
    return Optional.ofNullable(parameters.get(key))
        .filter(array -> array.length > 0)
        .map(array -> array[0]);
  }
  
  public List<String> getAll(final String key) {
    return Optional.ofNullable(parameters.get(key))
        .map(Arrays::asList)
        .orElseGet(Collections::emptyList);
  }

  Map<String, String[]> getParameters() {
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
    return newBuilder().withValueArrays(parameters);
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

    public Builder withValue(final String key, final String[] value) {
      values.computeIfAbsent(key, k -> new LinkedList<>()).addAll(Arrays.asList(value));
      return this;
    }

    public Builder withValues(final Map<String, String> values) {
      values.forEach(this::withValue);
      return this;
    }

    public Builder withValueArrays(final Map<String, String[]> values) {
      values.forEach(this::withValue);
      return this;
    }

    public Parameters build() {
      return new Parameters(values.entrySet()
          .stream()
          .collect(Collectors.toUnmodifiableMap(
              Entry::getKey,
              entry -> entry.getValue().toArray(EMPTY_STRING_ARRAY))));
    }
  }
}
