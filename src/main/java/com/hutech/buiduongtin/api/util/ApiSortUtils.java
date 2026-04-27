package com.hutech.buiduongtin.api.util;

import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.Set;

public final class ApiSortUtils {

    private static final String DEFAULT_SORT_PROPERTY = "name";
    private static final Set<String> ALLOWED_PRODUCT_SORT_FIELDS = Set.of("id", "name", "price", "stockQuantity");
    private static final Map<String, String> PRODUCT_SORT_ALIASES = Map.of(
            "stockquantity", "stockQuantity");

    private ApiSortUtils() {
    }

    public static Sort parseProductSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, DEFAULT_SORT_PROPERTY);
        }

        String[] parts = sort.split(",");
        String rawProperty = parts[0] == null ? "" : parts[0].trim();
        String normalizedProperty = normalizeProductSortProperty(rawProperty);
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(direction, normalizedProperty);
    }

    public static String normalizeProductSort(String sort) {
        Sort parsedSort = parseProductSort(sort);
        Sort.Order order = parsedSort.stream().findFirst()
                .orElse(Sort.Order.asc(DEFAULT_SORT_PROPERTY));
        return order.getProperty() + "," + order.getDirection().name().toLowerCase();
    }

    private static String normalizeProductSortProperty(String property) {
        if (property == null || property.isBlank()) {
            return DEFAULT_SORT_PROPERTY;
        }

        String trimmedProperty = property.trim();
        String aliasKey = trimmedProperty.replace("_", "").toLowerCase();
        String mappedProperty = PRODUCT_SORT_ALIASES.getOrDefault(aliasKey, trimmedProperty);
        return ALLOWED_PRODUCT_SORT_FIELDS.contains(mappedProperty) ? mappedProperty : DEFAULT_SORT_PROPERTY;
    }
}
