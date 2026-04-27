package com.scriptslab.core.module;

import java.util.*;

import com.scriptslab.api.module.ModuleDescriptor;

/**
 * Resolves module dependencies and determines load order.
 * Uses topological sorting to handle dependencies.
 */
public final class DependencyResolver {
    
    /**
     * Resolves load order for modules based on dependencies.
     * Uses Kahn's algorithm for topological sorting.
     * 
     * @param descriptors module descriptors
     * @return ordered list of module IDs
     * @throws CyclicDependencyException if circular dependency detected
     */
    public List<String> resolveLoadOrder(Collection<ModuleDescriptor> descriptors) {
        if (descriptors == null || descriptors.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Build dependency graph
        Map<String, Set<String>> graph = buildDependencyGraph(descriptors);
        Map<String, Integer> inDegree = calculateInDegree(graph);
        
        // Kahn's algorithm
        Queue<String> queue = new LinkedList<>();
        List<String> result = new ArrayList<>();
        
        // Add all nodes with no dependencies
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);
            
            // Reduce in-degree for dependent modules
            Set<String> dependents = graph.getOrDefault(current, Collections.emptySet());
            for (String dependent : dependents) {
                int newDegree = inDegree.get(dependent) - 1;
                inDegree.put(dependent, newDegree);
                
                if (newDegree == 0) {
                    queue.offer(dependent);
                }
            }
        }
        
        // Check for cycles
        if (result.size() != descriptors.size()) {
            List<String> remaining = new ArrayList<>();
            for (ModuleDescriptor desc : descriptors) {
                if (!result.contains(desc.id())) {
                    remaining.add(desc.id());
                }
            }
            throw new CyclicDependencyException(
                    "Circular dependency detected in modules: " + remaining);
        }
        
        return result;
    }
    
    /**
     * Builds a dependency graph.
     * Key: module ID, Value: set of modules that depend on it
     */
    private Map<String, Set<String>> buildDependencyGraph(Collection<ModuleDescriptor> descriptors) {
        Map<String, Set<String>> graph = new HashMap<>();
        
        // Initialize graph
        for (ModuleDescriptor desc : descriptors) {
            graph.putIfAbsent(desc.id(), new HashSet<>());
        }
        
        // Build edges (dependency -> dependent)
        for (ModuleDescriptor desc : descriptors) {
            for (String dependency : desc.dependencies()) {
                graph.computeIfAbsent(dependency, k -> new HashSet<>()).add(desc.id());
            }
            // Soft dependencies are optional, but if present, must load first
            for (String softDep : desc.softDependencies()) {
                if (graph.containsKey(softDep)) {
                    graph.get(softDep).add(desc.id());
                }
            }
        }
        
        return graph;
    }
    
    /**
     * Calculates in-degree (number of dependencies) for each module.
     */
    private Map<String, Integer> calculateInDegree(Map<String, Set<String>> graph) {
        Map<String, Integer> inDegree = new HashMap<>();
        
        // Initialize all to 0
        for (String module : graph.keySet()) {
            inDegree.put(module, 0);
        }
        
        // Count incoming edges
        for (Set<String> dependents : graph.values()) {
            for (String dependent : dependents) {
                inDegree.put(dependent, inDegree.getOrDefault(dependent, 0) + 1);
            }
        }
        
        return inDegree;
    }
    
    /**
     * Validates that all dependencies exist.
     * 
     * @param descriptors module descriptors
     * @throws MissingDependencyException if dependency not found
     */
    public void validateDependencies(Collection<ModuleDescriptor> descriptors) {
        Set<String> availableModules = new HashSet<>();
        for (ModuleDescriptor desc : descriptors) {
            availableModules.add(desc.id());
        }
        
        for (ModuleDescriptor desc : descriptors) {
            for (String dependency : desc.dependencies()) {
                if (!availableModules.contains(dependency)) {
                    throw new MissingDependencyException(
                            "Module '" + desc.id() + "' depends on '" + dependency + 
                            "' which is not available");
                }
            }
        }
    }
    
    /**
     * Exception thrown when circular dependency is detected.
     */
    public static class CyclicDependencyException extends RuntimeException {
        public CyclicDependencyException(String message) {
            super(message);
        }
    }
    
    /**
     * Exception thrown when required dependency is missing.
     */
    public static class MissingDependencyException extends RuntimeException {
        public MissingDependencyException(String message) {
            super(message);
        }
    }
}
