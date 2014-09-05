package org.fixprotocol.ui.icon;

import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	public List<ImageIcon> getIconList(String name, int... sizes) {
		ArrayList<ImageIcon> list = new ArrayList<ImageIcon>();
		for (int size : sizes) {
			ImageIcon icon = getIcon(name, size);
			if (icon != null) list.add(icon);
		}
		return list;
	}
	
	public List<Image> getIconImageList(String name, int... sizes) {
		ArrayList<Image> list = new ArrayList<Image>();
		for (int size : sizes) {
			ImageIcon icon = getIcon(name, size);
			if (icon != null) list.add(icon.getImage());
		}
		return list;
	}
	
	public ImageIcon getIcon(String name, int size) {
        String iconFilename = ICON_DIR + name + "_" + size + ".png";
		ImageIcon imageIcon = icons.get(iconFilename);
		if (imageIcon == null) {
			URL resource = IconCache.class.getResource(iconFilename);
			try {
				imageIcon = new ImageIcon(resource);
				icons.put(iconFilename, imageIcon);
			} catch (Exception e) {
				System.out.println("Could not load icon " + iconFilename);
			}
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
