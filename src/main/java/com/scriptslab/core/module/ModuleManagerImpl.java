package com.scriptslab.core.module;

import com.scriptslab.api.module.Module;
import com.scriptslab.api.module.ModuleManager;
import com.scriptslab.api.module.ModuleDescriptor;
import com.scriptslab.core.module.BaseModule;
import com.scriptslab.core.module.DependencyResolver;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe implementation of ModuleManager.
 */
public final class ModuleManagerImpl implements ModuleManager {
    
    private final Path modulesDirectory;
    private final Logger logger;
    private final Map<String, Module> modules;
    private final Map<String, ModuleDescriptor> descriptors;
    private final DependencyResolver dependencyResolver;
    
    public ModuleManagerImpl(Path modulesDirectory) {
        this.modulesDirectory = modulesDirectory;
        this.logger = Logger.getLogger("ModuleManager");
        this.modules = new ConcurrentHashMap<>();
        this.descriptors = new ConcurrentHashMap<>();
        this.dependencyResolver = new DependencyResolver();
        
        // Create modules directory
        modulesDirectory.toFile().mkdirs();
    }
    
    @Override
    public CompletableFuture<Integer> loadAllModules() {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Loading modules from " + modulesDirectory);
            
            File[] moduleFiles = modulesDirectory.toFile().listFiles();
            if (moduleFiles == null || moduleFiles.length == 0) {
                logger.info("No modules found");
                return 0;
            }
            
            // Load all module descriptors first
            List<ModuleDescriptor> allDescriptors = new ArrayList<>();
            for (File file : moduleFiles) {
                if (file.isDirectory()) {
                    try {
                        ModuleDescriptor descriptor = loadDescriptor(file.toPath());
                        allDescriptors.add(descriptor);
                        descriptors.put(descriptor.id(), descriptor);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Failed to load descriptor from " + file.getName(), e);
                    }
                }
            }
            
            // Validate dependencies
            try {
                dependencyResolver.validateDependencies(allDescriptors);
            } catch (DependencyResolver.MissingDependencyException e) {
                logger.severe(e.getMessage());
                return 0;
            }
            
            // Resolve load order
            List<String> loadOrder;
            try {
                loadOrder = dependencyResolver.resolveLoadOrder(allDescriptors);
            } catch (DependencyResolver.CyclicDependencyException e) {
                logger.severe(e.getMessage());
                return 0;
            }
            
            logger.info("Module load order: " + loadOrder);
            
            // Load modules in order
            int loaded = 0;
            for (String moduleId : loadOrder) {
                ModuleDescriptor descriptor = descriptors.get(moduleId);
                if (descriptor != null && descriptor.enabled()) {
                    try {
                        Path modulePath = modulesDirectory.resolve(moduleId);
                        loadModule(modulePath).join();
                        loaded++;
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Failed to load module " + moduleId, e);
                    }
                }
            }
            
            logger.info("Loaded " + loaded + " modules");
            return loaded;
        });
    }
    
    @Override
    public CompletableFuture<Module> loadModule(Path modulePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Load descriptor
                ModuleDescriptor descriptor = loadDescriptor(modulePath);
                descriptor.validate();
                
                // Check if already loaded
                if (modules.containsKey(descriptor.id())) {
                    logger.warning("Module " + descriptor.id() + " is already loaded");
                    return modules.get(descriptor.id());
                }
                
                // Create module instance
                Module module = createModuleInstance(descriptor, modulePath);
                
                // Load the module
                module.onLoad().join();
                
                modules.put(descriptor.id(), module);
                logger.info("Loaded module: " + descriptor.name() + " v" + descriptor.version());
                
                return module;
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to load module from " + modulePath, e);
                throw new RuntimeException(e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Void> enableModule(String moduleId) {
        return CompletableFuture.runAsync(() -> {
            Module module = modules.get(moduleId);
            if (module == null) {
                throw new IllegalArgumentException("Module not found: " + moduleId);
            }
            
            if (module.isEnabled()) {
                logger.warning("Module " + moduleId + " is already enabled");
                return;
            }
            
            try {
                module.onEnable().join();
                logger.info("Enabled module: " + module.getName());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to enable module " + moduleId, e);
                throw new RuntimeException(e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Void> disableModule(String moduleId) {
        return CompletableFuture.runAsync(() -> {
            Module module = modules.get(moduleId);
            if (module == null) {
                throw new IllegalArgumentException("Module not found: " + moduleId);
            }
            
            if (!module.isEnabled()) {
                logger.warning("Module " + moduleId + " is already disabled");
                return;
            }
            
            try {
                module.onDisable().join();
                logger.info("Disabled module: " + module.getName());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to disable module " + moduleId, e);
                throw new RuntimeException(e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Void> reloadModule(String moduleId) {
        return CompletableFuture.runAsync(() -> {
            Module module = modules.get(moduleId);
            if (module == null) {
                throw new IllegalArgumentException("Module not found: " + moduleId);
            }
            
            try {
                module.onReload().join();
                logger.info("Reloaded module: " + module.getName());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to reload module " + moduleId, e);
                throw new RuntimeException(e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Void> unloadModule(String moduleId) {
        return CompletableFuture.runAsync(() -> {
            Module module = modules.get(moduleId);
            if (module == null) {
                throw new IllegalArgumentException("Module not found: " + moduleId);
            }
            
            try {
                if (module.isEnabled()) {
                    module.onDisable().join();
                }
                modules.remove(moduleId);
                descriptors.remove(moduleId);
                logger.info("Unloaded module: " + module.getName());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to unload module " + moduleId, e);
                throw new RuntimeException(e);
            }
        });
    }
    
    @Override
    public Optional<Module> getModule(String moduleId) {
        return Optional.ofNullable(modules.get(moduleId));
    }
    
    @Override
    public Collection<Module> getModules() {
        return Collections.unmodifiableCollection(modules.values());
    }
    
    @Override
    public Collection<Module> getEnabledModules() {
        return modules.values().stream()
                .filter(Module::isEnabled)
                .toList();
    }
    
    @Override
    public boolean isLoaded(String moduleId) {
        return modules.containsKey(moduleId);
    }
    
    @Override
    public boolean isEnabled(String moduleId) {
        Module module = modules.get(moduleId);
        return module != null && module.isEnabled();
    }
    
    @Override
    public Path getModulesDirectory() {
        return modulesDirectory;
    }
    
    @Override
    public List<String> resolveLoadOrder(Collection<ModuleDescriptor> moduleDescriptors) {
        return dependencyResolver.resolveLoadOrder(moduleDescriptors);
    }
    
    /**
     * Loads module descriptor from module.yml.
     */
    private ModuleDescriptor loadDescriptor(Path modulePath) {
        File descriptorFile = modulePath.resolve("module.yml").toFile();
        
        if (!descriptorFile.exists()) {
            throw new IllegalStateException("module.yml not found in " + modulePath);
        }
        
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(descriptorFile);
        
        String id = yaml.getString("id");
        String name = yaml.getString("name", id);
        String version = yaml.getString("version", "1.0.0");
        String description = yaml.getString("description", "");
        List<String> authors = yaml.getStringList("authors");
        Set<String> dependencies = new HashSet<>(yaml.getStringList("dependencies"));
        Set<String> softDependencies = new HashSet<>(yaml.getStringList("soft-dependencies"));
        
        String loadOrderStr = yaml.getString("load-order", "STARTUP");
        ModuleDescriptor.LoadOrder loadOrder = ModuleDescriptor.LoadOrder.valueOf(loadOrderStr.toUpperCase());
        
        boolean enabled = yaml.getBoolean("enabled", true);
        
        return new ModuleDescriptor(
                id, name, version, description, authors,
                dependencies, softDependencies, loadOrder, enabled
        );
    }
    
    /**
     * Creates a module instance.
     * For now, uses reflection to instantiate built-in modules.
     * In production, this would load from JAR files.
     */
    private Module createModuleInstance(ModuleDescriptor descriptor, Path modulePath) {
        // Try to load built-in module class
        String className = "com.myplugin.modules." + descriptor.id() + "." + 
                          capitalize(descriptor.id()) + "Module";
        
        try {
            Class<?> moduleClass = Class.forName(className);
            return (Module) moduleClass
                    .getConstructor(ModuleDescriptor.class, Path.class)
                    .newInstance(descriptor, modulePath);
        } catch (ClassNotFoundException e) {
            // Module class not found, create generic module
            logger.warning("Module class not found: " + className + ", using generic module");
            return new GenericModule(descriptor, modulePath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to instantiate module " + descriptor.id(), e);
            throw new RuntimeException(e);
        }
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * Generic module implementation for modules without custom class.
     */
    private static class GenericModule extends BaseModule {
        
        GenericModule(ModuleDescriptor descriptor, Path dataFolder) {
            super(descriptor, dataFolder);
        }
    }
}
