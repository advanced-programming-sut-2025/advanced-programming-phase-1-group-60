package com.StardewValley.AssetsManager;

import com.StardewValley.models.FruitsAndVegetables;
import com.StardewValley.models.Seeds;
import com.StardewValley.repository.FruitsAndVegetablesRepository;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import java.util.*;

public class CropManager {
    private static CropManager instance;

    // Caches
    private final Map<String, Texture> seedTextureCache = new HashMap<>();
    private final Map<String, List<Texture>> stageTextureCache = new HashMap<>();
    private final Map<String, Texture> fruitTextureCache = new HashMap<>();
    private final Map<String, Texture> multiHarvestBaseCache = new HashMap<>();
    private final Map<String, Texture> giantCropTextureCache = new HashMap<>();
    private boolean allLoaded = false;

    // NEW: Crow texture
    private Texture crowTexture;

    private CropManager() {}

    public static synchronized CropManager getInstance() {
        if (instance == null) {
            instance = new CropManager();
        }
        return instance;
    }

    // NEW
    public Texture getCrowTexture() {
        if (crowTexture == null) {
            String path = "assets/Map/FruitsAndVegetables/Crow.png";
            if (Gdx.files.internal(path).exists()) {
                crowTexture = new Texture(Gdx.files.internal(path));
            }
        }
        return crowTexture;
    }

    // Seed packet icons
    public Texture getSeedTexture(Seeds seed) {
        if (seed == null) return null;
        return getSeedTexture(seed.getImagePath());
    }

    public Texture getGiantCropTexture(String cropName) {
        if (cropName == null) return null;
        String key = cropName.toLowerCase(Locale.ROOT);
        if (giantCropTextureCache.containsKey(key)) return giantCropTextureCache.get(key);

        List<String> variants = buildNameVariants(cropName);
        for (String v : variants) {
            String path = "assets/Map/FruitsAndVegetables/GiantCrops/Giant_" + v + ".png";
            if (Gdx.files.internal(path).exists()) {
                try {
                    Texture tex = new Texture(Gdx.files.internal(path));
                    giantCropTextureCache.put(key, tex);
                    return tex;
                } catch (Exception e) {
                    Gdx.app.error("CropManager", "Failed loading giant crop texture: " + path, e);
                    return null;
                }
            }
        }
        return null;
    }

    public Texture getSeedTexture(String imageFileName) {
        if (imageFileName == null || imageFileName.isEmpty()) return null;
        String key = imageFileName.toLowerCase(Locale.ROOT);
        if (seedTextureCache.containsKey(key)) return seedTextureCache.get(key);

        String primary = "assets/Map/FruitsAndVegetables/Seed/" + imageFileName;
        if (Gdx.files.internal(primary).exists()) {
            seedTextureCache.put(key, new Texture(Gdx.files.internal(primary)));
            return seedTextureCache.get(key);
        }
        String alt = "Map/FruitsAndVegetables/Seed/" + imageFileName;
        if (Gdx.files.internal(alt).exists()) {
            seedTextureCache.put(key, new Texture(Gdx.files.internal(alt)));
            return seedTextureCache.get(key);
        }
        return null;
    }

    /**
     * Get a daily stage texture (1-based dayNumber).
     * Clamps to last stage if dayNumber exceeds loaded count.
     */
    public Texture getStageTextureForDay(String cropName, int dayNumber) {
        if (cropName == null) return null;
        String key = cropName.toLowerCase(Locale.ROOT);
        ensureCropStagesLoaded(key, cropName);
        List<Texture> frames = stageTextureCache.get(key);
        if (frames == null || frames.isEmpty()) return null;
        if (dayNumber <= 0) dayNumber = 1;
        if (dayNumber > frames.size()) dayNumber = frames.size();
        return frames.get(dayNumber - 1);
    }

    public int getLoadedStageCount(String cropName) {
        if (cropName == null) return 0;
        List<Texture> frames = stageTextureCache.get(cropName.toLowerCase(Locale.ROOT));
        return frames == null ? 0 : frames.size();
    }

    public Texture getFruitTexture(String cropName) {
        if (cropName == null) return null;
        return fruitTextureCache.get(cropName.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the "naked" base texture for multi-harvest crops after harvest.
     * Path: assets/Map/FruitsAndVegetables/MultipleTime/<CropName>_Stage_0.png
     */
    public Texture getMultiHarvestBaseTexture(String cropName) {
        if (cropName == null) return null;
        String key = cropName.toLowerCase(Locale.ROOT);
        if (multiHarvestBaseCache.containsKey(key)) return multiHarvestBaseCache.get(key);

        // Naming variants (match stage loading)
        List<String> variants = buildNameVariants(cropName);
        for (String v : variants) {
            String path = "assets/Map/FruitsAndVegetables/MultipleTime/" + v + "_Stage_0.png";
            if (Gdx.files.internal(path).exists()) {
                try {
                    Texture tex = new Texture(Gdx.files.internal(path));
                    multiHarvestBaseCache.put(key, tex);
                    return tex;
                } catch (Exception e) {
                    Gdx.app.error("CropManager", "Failed loading multi-harvest base: " + path, e);
                    return null;
                }
            }
        }
        return null;
    }

    public void loadAllCropGraphics(boolean forceReload) {
        if (allLoaded && !forceReload) return;
        for (FruitsAndVegetables fv : FruitsAndVegetablesRepository.crops) {
            loadSingleCropGraphics(fv, forceReload);
        }
        allLoaded = true;
    }

    public void loadAllCropGraphics() {
        loadAllCropGraphics(false);
    }

    public void loadSingleCropGraphics(FruitsAndVegetables crop, boolean forceReload) {
        if (crop == null) return;
        String key = crop.getName().toLowerCase(Locale.ROOT);
        if (!forceReload && stageTextureCache.containsKey(key)) return;
        if (forceReload) disposeCrop(key);

        List<Texture> stageFrames = loadDailyStagesForCrop(crop.getName());
        if (!stageFrames.isEmpty()) {
            stageTextureCache.put(key, stageFrames);
        }
        Texture fruit = loadFruitTexture(crop.getName());
        if (fruit != null) fruitTextureCache.put(key, fruit);
        if (!crop.isOneTime()) getMultiHarvestBaseTexture(crop.getName());
    }

    private void ensureCropStagesLoaded(String keyLower, String originalName) {
        if (stageTextureCache.containsKey(keyLower)) return;
        FruitsAndVegetables crop = FruitsAndVegetablesRepository.getCropByName(originalName);
        if (crop != null) {
            loadSingleCropGraphics(crop, false);
        } else {
            List<Texture> stageFrames = loadDailyStagesForCrop(originalName);
            if (!stageFrames.isEmpty()) {
                stageTextureCache.put(keyLower, stageFrames);
            }
        }
    }

    private List<Texture> loadDailyStagesForCrop(String cropName) {
        List<Texture> frames = new ArrayList<>();
        if (cropName == null) return frames;
        List<String> folderCandidates = buildNameVariants(cropName);
        final String stagesRoot = "assets/Map/FruitsAndVegetables/Stages/";

        for (String folder : folderCandidates) {
            String folderPath = stagesRoot + folder;
            FileHandle dir = Gdx.files.internal(folderPath);
            if (!dir.exists() || !dir.isDirectory()) continue;
            List<Texture> loaded = tryLoadSequential(folderPath + "/", folder + "_Stage_");
            if (!loaded.isEmpty()) {
                return loaded;
            }
        }
        return frames;
    }

    private List<Texture> tryLoadSequential(String prefixPath, String fileBase) {
        List<Texture> list = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            String name = prefixPath + fileBase + i + ".png";
            FileHandle fh = Gdx.files.internal(name);
            if (!fh.exists()) break;
            try {
                list.add(new Texture(fh));
            } catch (Exception e) {
                Gdx.app.error("CropManager", "Failed to load stage texture: " + name, e);
                break;
            }
        }
        return list;
    }

    private Texture loadFruitTexture(String cropName) {
        List<String> variants = buildNameVariants(cropName);
        String root = "assets/Map/FruitsAndVegetables/Crop/";
        for (String folder : variants) {
            String folderPath = root + folder;
            FileHandle dir = Gdx.files.internal(folderPath);
            if (!dir.exists() || !dir.isDirectory()) continue;

            String base = folder;
            String[] files = {
                base + ".png",
                base + "_Fruit.png",
                base.replace("_", "") + ".png"
            };
            for (String f : files) {
                FileHandle fh = Gdx.files.internal(folderPath + "/" + f);
                if (fh.exists()) {
                    try {
                        return new Texture(fh);
                    } catch (Exception e) {
                        Gdx.app.error("CropManager", "Failed to load fruit texture: " + fh.path(), e);
                    }
                }
            }
        }
        return null;
    }

    private List<String> buildNameVariants(String name) {
        List<String> list = new ArrayList<>();
        String trimmed = name.trim();
        if (!list.contains(trimmed)) list.add(trimmed);
        String underscored = trimmed.replace(' ', '_');
        if (!list.contains(underscored)) list.add(underscored);
        String stripped = trimmed.replaceAll("[^A-Za-z0-9_]", "").replace(' ', '_');
        if (!list.contains(stripped)) list.add(stripped);
        return list;
    }

    public void dispose() {
        for (Texture t : seedTextureCache.values()) if (t != null) t.dispose();
        seedTextureCache.clear();
        for (Texture t : fruitTextureCache.values()) if (t != null) t.dispose();
        fruitTextureCache.clear();
        for (List<Texture> list : stageTextureCache.values())
            for (Texture t : list) if (t != null) t.dispose();
        stageTextureCache.clear();
        for (Texture t : multiHarvestBaseCache.values()) if (t != null) t.dispose();
        multiHarvestBaseCache.clear();
        for (Texture t : giantCropTextureCache.values()) if (t != null) t.dispose();
        giantCropTextureCache.clear();
        if (crowTexture != null) {
            crowTexture.dispose();
            crowTexture = null;
        }
        allLoaded = false;
    }

    public String resolveProduceInventoryPath(String cropName) {
        if (cropName == null) return null;
        List<String> variants = buildNameVariants(cropName);
        final String root = "assets/Map/FruitsAndVegetables/Crop/";
        for (String v : variants) {
            String path = root + v + "/" + v + ".png";
            if (Gdx.files.internal(path).exists()) {
                return path;
            }
        }
        return null;
    }

    private void disposeCrop(String key) {
        List<Texture> frames = stageTextureCache.remove(key);
        if (frames != null)
            for (Texture t : frames) if (t != null) t.dispose();
        Texture fruit = fruitTextureCache.remove(key);
        if (fruit != null) fruit.dispose();
        Texture base = multiHarvestBaseCache.remove(key);
        if (base != null) base.dispose();
    }
}
