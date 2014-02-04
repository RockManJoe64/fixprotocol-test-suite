package org.fixprotocol.test.ui.icon;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class IconCache {

    public static final String ICON_DIR = "/icon/";
    public static final String IMG_DIR = "/image/";

	private static IconCache instance;
	
	private Map<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
	
	public IconCache() {}
	
	public static IconCache getInstance() {
		if (instance == null) {
			instance = new IconCache();
		}
		
		return instance;
	}
	
	public ImageIcon getIcon(String name, int size) {
        String iconFilename = ICON_DIR + name + "_" + size + ".png";
		ImageIcon imageIcon = icons.get(iconFilename);
		if (imageIcon == null) {
			URL resource = IconCache.class.getResource(iconFilename);
			imageIcon = new ImageIcon(resource);
			icons.put(iconFilename, imageIcon);
		}
		
		return imageIcon;
	}
	
    public ImageIcon getImage(String name) {
        String iconFilename = IMG_DIR + name + ".png";
        ImageIcon imageIcon = icons.get(iconFilename);
        if (imageIcon == null) {
            URL resource = IconCache.class.getResource(iconFilename);
            imageIcon = new ImageIcon(resource);
            icons.put(iconFilename, imageIcon);
        }

        return imageIcon;
    }

}
