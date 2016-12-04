package org.didelphis.toolbox.frontend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by samantha on 12/3/16.
 */
public final class ThemeManager {

	private static final Pattern SPACE = Pattern.compile("\\s+");

	private final static Map<String, String> THEMES = new LinkedHashMap<>();
	private final static List<String> THEME_LIST;

	private ThemeManager(){}

	public static String get(int index) {
		return THEME_LIST.get(index);
	}

	public static String getType(String key) {
		return THEMES.get(key);
	}

	public static String getNormalized(int index) {
		return SPACE.matcher(get(index).toLowerCase()).replaceAll("_");
	}

	public static Collection<String> listThemes() {
		return THEME_LIST;
	}

	static {
		THEMES.put("Chrome",                  "light");
		THEMES.put("Clouds",                  "light");
		THEMES.put("Crimson Editor",          "light");
		THEMES.put("Dawn",                    "light");
		THEMES.put("Dreamweaver",             "light");
		THEMES.put("Eclipse",                 "light");
		THEMES.put("GitHub",                  "light");
		THEMES.put("IPlastic",                "light");
		THEMES.put("Katzenmilch",             "light");
		THEMES.put("Kuroir",                  "light");
		THEMES.put("Solarized Light",         "light");
		THEMES.put("SQLServer",               "light");
		THEMES.put("Textmate",                "light");
		THEMES.put("Tomorrow",                "light");
		THEMES.put("XCode",                   "light");
		THEMES.put("Chaos",                   "dark");
		THEMES.put("Clouds Midnight",         "dark");
		THEMES.put("Cobalt",                  "dark");// blue
		THEMES.put("Idle Fingers",            "dark");
		THEMES.put("KR Theme",                "dark");
		THEMES.put("Merbivore",               "dark");
		THEMES.put("Merbivore Soft",          "dark");
		THEMES.put("Mono Industrial",         "dark");
		THEMES.put("Monokai",                 "dark");
		THEMES.put("Pastel On Dark",          "dark");
		THEMES.put("Solarized Dark",          "dark");// blue
		THEMES.put("Terminal",                "dark");
		THEMES.put("Tomorrow Night",          "dark");
		THEMES.put("Tomorrow Night Blue",     "dark");// blue
		THEMES.put("Tomorrow Night Bright",   "dark");
		THEMES.put("Tomorrow Night Eighties", "dark");
		THEMES.put("Twilight",                "dark");
		THEMES.put("Vibrant Ink",             "dark");
		THEME_LIST = new ArrayList<>(THEMES.keySet());
	}
}
