package io.github.stack07142.sample_shapedrawable;

import android.graphics.Color;

import java.util.HashMap;

public class LanguageColors {

    private HashMap<String, String> colorMap;

    public LanguageColors() {

        colorMap = new HashMap<>();

        setColors();
    }

    private void setColors() {

        colorMap.put("java", "#b07219");
        colorMap.put("objective-c", "#438eff");
        colorMap.put("swift", "#ffac45");
        colorMap.put("groovy", "#e69f56");
        colorMap.put("python", "#3572A5");
        colorMap.put("ruby", "#701516");
        colorMap.put("c", "#555555");
    }


    public int getColor(String language) {

        return Color.parseColor(colorMap.get(language));
    }
}
