package com.scriptslab.api.module;

import java.util.List;
import java.util.Set;

/**
 * Immutable descriptor containing module metadata.
 * Loaded from module.yml.
 */
public record ModuleDescriptor(
    String id,
    String name,
    String version,
    String description,
    List<String> authors,
    Set<String> dependencies,
    Set<String> softDependencies,
    LoadOrder loadOrder,
    boolean enabled
) {
    
    /**
     * Load order for modules.
     */
    public enum LoadOrder {
        STARTUP,  // Load at plugin startup
        POSTWORLD, // Load after worlds are loaded
        LAZY      // Load on demand
    }
    
    /**
     * Validates the descriptor.
     * 
     * @throws IllegalArgumentException if invalid
     */
    public void validate() {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Module ID cannot be null or blank");
        }
        if (!id.matches("[a-z0-9_]+")) {
            throw new IllegalArgumentException("Module ID must be lowercase alphanumeric with underscores");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Module name cannot be null or blank");
        }
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Module version cannot be null or blank");
        }
    }
    
    /**
     * Checks if this module depends on another module.
     * 
     * @param moduleId module to check
     * @return true if depends on it
     */
    public boolean dependsOn(String moduleId) {
        return dependencies.contains(moduleId);
    }
    
    /**
     * Checks if this module soft-depends on another module.
     * 
     * @param moduleId module to check
     * @return true if soft-depends on it
     */
    public boolean softDependsOn(String moduleId) {
        return softDependencies.contains(moduleId);
    }
    
    /**
     * Gets all dependencies (hard + soft).
     * 
     * @return all dependencies
     */
    public Set<String> getAllDependencies() {
        var all = new java.util.HashSet<>(dependencies);
        all.addAll(softDependencies);
        return all;
    }
}
