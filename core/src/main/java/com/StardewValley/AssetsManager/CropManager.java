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
    private final Map<String, Texture> seedTextureCache = new HashMap<>();
    private final Map<String, Texture> fruitTextureCache = new HashMap<>();
    private final Map<String, List<Texture>> cropStageTextureCache = new HashMap<>();
    private boolean cropsLoaded = false;
    private CropManager() {}
    public static synchronized CropManager getInstance() {
        if (instance == null) {
            instance = new CropManager();
        }
        return instance;
    }

    public Texture getSeedTexture(String imageFileName) {
        if (imageFileName == null || imageFileName.isEmpty()) return null;
        String key = imageFileName.toLowerCase(Locale.ROOT);
        if (!seedTextureCache.containsKey(key)) {
            String path = "assets/Map/FruitsAndVegetables/Seed/" + imageFileName;
            if (Gdx.files.internal(path).exists()) {
                seedTextureCache.put(key, new Texture(Gdx.files.internal(path)));
            } else {
                String altPath = "Map/FruitsAndVegetables/Seed/" + imageFileName;
                if (Gdx.files.internal(altPath).exists()) {
                    seedTextureCache.put(key, new Texture(Gdx.files.internal(altPath)));
                } else {
                    return null;
                }
            }
        }
        return seedTextureCache.get(key);
    }

    public Texture getSeedTexture(Seeds seed) {
        return (seed == null) ? null : getSeedTexture(seed.getImagePath());
    }

    public Texture getFruitTexture(String cropName) {
        if (cropName == null) return null;
        String key = cropName.toLowerCase(Locale.ROOT);
        return fruitTextureCache.get(key);
    }
    public Texture getCropStageTexture(String cropName, int frameIndex) {
        if (cropName == null) return null;
        List<Texture> frames = cropStageTextureCache.get(cropName.toLowerCase(Locale.ROOT));
        if (frames == null || frameIndex < 0 || frameIndex >= frames.size()) return null;
        return frames.get(frameIndex);
    }
    public int getCropStageFrameCount(String cropName) {
        List<Texture> frames = cropStageTextureCache.get(cropName.toLowerCase(Locale.ROOT));
        return frames == null ? 0 : frames.size();
    }
    public void loadAllCropGraphics(boolean forceReload) {
        if (cropsLoaded && !forceReload) return;
        for (FruitsAndVegetables crop : FruitsAndVegetablesRepository.crops) {
            loadSingleCropGraphics(crop, forceReload);
        }
        cropsLoaded = true;
    }
    public void loadAllCropGraphics() {
        loadAllCropGraphics(false);
    }
    public void loadSingleCropGraphics(FruitsAndVegetables crop, boolean forceReload) {
        if (crop == null) return;
        String cropKey = crop.getName().toLowerCase(Locale.ROOT);
        if (!forceReload && fruitTextureCache.containsKey(cropKey) && cropStageTextureCache.containsKey(cropKey)) {
            return;
        }
        if (forceReload) {
            disposeCrop(cropKey);
        }
        List<String> folderCandidates = buildFolderNameCandidates(crop.getName());
        List<Texture> stageFrames = loadStageFrames(folderCandidates);
        if (!stageFrames.isEmpty()) {
            cropStageTextureCache.put(cropKey, stageFrames);
            crop.clearStageImagePaths();
            int idx = 1;
            for (Texture ignored : stageFrames) {
                String effectiveFolder = lastSuccessfulStageFolder;
                String effectiveBase = lastSuccessfulStageBase;
                String storedPath = effectiveFolder + "/" + effectiveBase + "_Stage_" + idx + ".png";
                crop.addStageImagePath(storedPath);
                idx++;
            }
        }
        Texture fruitTexture = loadFruitTexture(folderCandidates);
        if (fruitTexture != null) {
            fruitTextureCache.put(cropKey, fruitTexture);
            crop.setFruitImagePath(lastSuccessfulFruitPathRelative);
        }
    }

    private String lastSuccessfulStageFolder = null;
    private String lastSuccessfulStageBase = null;
    private String lastSuccessfulFruitPathRelative = null;

    private List<Texture> loadStageFrames(List<String> folderCandidates) {
        List<Texture> frames = new ArrayList<>();
        String[] stageRoots = {
            "assets/Map/FruitsAndVegetables/Seed/",
            "assets/Map/FruitsAndVegetables/"
        };

        for (String folderCandidate : folderCandidates) {
            for (String root : stageRoots) {
                FileHandle dir = Gdx.files.internal(root + folderCandidate);
                if (!dir.exists() || !dir.isDirectory()) continue;
                List<String> baseNameCandidates = buildBaseNameCandidates(folderCandidate);
                for (String baseName : baseNameCandidates) {
                    List<Texture> tmp = tryLoadSequentialStageFrames(root + folderCandidate + "/", baseName);
                    if (!tmp.isEmpty()) {
                        frames = tmp;
                        lastSuccessfulStageFolder = root + folderCandidate;
                        lastSuccessfulStageBase = baseName;
                        return frames;
                    }
                }
            }
        }
        return frames;
    }

    private List<Texture> tryLoadSequentialStageFrames(String folderPath, String baseName) {
        List<Texture> list = new ArrayList<>();
        int maxFrames = 60;
        boolean anyLoaded = false;
        for (int i = 1; i <= maxFrames; i++) {
            String filename = folderPath + baseName + "_Stage_" + i + ".png";
            FileHandle fh = Gdx.files.internal(filename);
            if (!fh.exists()) {
                if (i == 1) {
                    return new ArrayList<>();
                }
                else {
                    break;
                }
            }
            try {
                Texture t = new Texture(fh);
                list.add(t);
                anyLoaded = true;
            } catch (Exception e) {
                Gdx.app.error("CropManager", "Failed to load stage frame: " + filename, e);
            }
        }
        if (!anyLoaded) {
            list.clear();
        }
        return list;
    }

    private Texture loadFruitTexture(List<String> folderCandidates) {
        String fruitRoot = "assets/Map/FruitsAndVegetables/Crop/";
        for (String folderCandidate : folderCandidates) {
            String folderPath = fruitRoot + folderCandidate;
            FileHandle dir = Gdx.files.internal(folderPath);
            if (!dir.exists() || !dir.isDirectory()) continue;
            List<String> fruitFileNames = new ArrayList<>();
            fruitFileNames.add(folderCandidate + ".png");
            fruitFileNames.add(folderCandidate + "_Fruit.png");
            String noUnderscore = folderCandidate.replace("_", "");
            fruitFileNames.add(noUnderscore + ".png");
            for (String file : fruitFileNames) {
                String fullPath = folderPath + "/" + file;
                FileHandle fh = Gdx.files.internal(fullPath);
                if (fh.exists()) {
                    try {
                        Texture t = new Texture(fh);
                        lastSuccessfulFruitPathRelative = folderPath + "/" + file;
                        return t;
                    } catch (Exception e) {
                        Gdx.app.error("CropManager", "Failed to load fruit texture: " + fullPath, e);
                    }
                }
            }
        }
        return null;
    }

    private List<String> buildFolderNameCandidates(String cropName) {
        List<String> list = new ArrayList<>();
        if (cropName == null) return list;
        String trimmed = cropName.trim();
        list.add(trimmed);
        String underscored = trimmed.replace(' ', '_');
        if (!list.contains(underscored)) list.add(underscored);
        String simplified = trimmed.replaceAll("[^A-Za-z0-9 ]", "").replace(' ', '_');
        if (!list.contains(simplified)) list.add(simplified);
        return list;
    }

    private List<String> buildBaseNameCandidates(String folderCandidate) {
        List<String> list = new ArrayList<>();
        list.add(folderCandidate);
        // Without underscores
        String noUnderscore = folderCandidate.replace("_", "");
        if (!list.contains(noUnderscore)) list.add(noUnderscore);
        return list;
    }

    public void dispose() {
        for (Texture t : seedTextureCache.values()) {
            if (t != null) t.dispose();
        }
        seedTextureCache.clear();
        for (Texture t : fruitTextureCache.values()) {
            if (t != null) t.dispose();
        }
        fruitTextureCache.clear();
        for (List<Texture> list : cropStageTextureCache.values()) {
            for (Texture t : list) {
                if (t != null) t.dispose();
            }
        }
        cropStageTextureCache.clear();
        cropsLoaded = false;
    }

    private void disposeCrop(String cropKey) {
        Texture ft = fruitTextureCache.remove(cropKey);
        if (ft != null) ft.dispose();
        List<Texture> frames = cropStageTextureCache.remove(cropKey);
        if (frames != null) {
            for (Texture t : frames) {
                if (t != null) t.dispose();
            }
        }
    }
}
