package com.afroant.atlasmanager;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class AnimationList {
	
	private List<String> list;

	public AnimationList(final Model model, ListStyle style) {
		list = new List<String>(style);
		
//		this.setItems("Grass", "Water", "Wall");
//		this.setItems("Grass", "Water", "Wall", "A", "b", "c", "d", "e", "q", "w", "r", "t", "y", "Grasssdf", "Watera", "Walla", "Aa", "ba", "ca", "da", "ea", "qa", "wa", "ra", "ta", "ysa");
		
		list.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				model.selectSprite(list.getSelected());
			}
		});
	}
	
	public void add(String entry) {
		Array<String> items = list.getItems();
		
		items.add(entry);
		
//		this.setItems(items);
	}
	
	public void add(ArrayList<Sprite> entries) {
		Array<String> array = new Array<String>();
		for (Sprite datum: entries) {
			String title = datum.key.name;
			int index = datum.key.index;
			if (index != -1) title += " (" + index + ")";
			array.add(title);
		}
		list.setItems(array);
	}
	
	private void bubbleSort(Array<String> array) {
		boolean isSwapped = true;
		int j = 0;
		String tmp;
		
		while (isSwapped) {
			isSwapped = false;
			j++;
			
			for (int i = 0; i < array.size - j; i++) {
				int result = array.get(i).compareTo(array.get(i + 1));
				if (result > 0) {
					tmp = array.get(i);
					array.set(i, array.get(i + 1));
					array.set(i + 1, tmp);
					isSwapped = true;
				}
			}
		}
	}

	public void sort() {
		Array<String> items = list.getItems();
		
		bubbleSort(items);
	}
	
	public List<String> getList() {
		return list;
	}
	
	public int getSelectedIndex() {
		return list.getSelectedIndex();
	}
}
