package models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import text.Text;

public class ModelBatch {
	
	private static Map<Mesh, List<Entity>> entities = new LinkedHashMap<>();
	private static Map<Mesh, List<Entity>> text = new LinkedHashMap<>();
	
	public static void addEntities(Entity...entities) {
		for(Entity entity: entities) {
			addEntity(entity);
		}
	}
	
	public static void addEntity(Entity e) {
		if(e.getMesh() instanceof Text) {
			if(text.containsKey(e.getMesh())) {
				text.get(e.getMesh()).add(e);
			} else {
				List<Entity> entitiesOfType = new ArrayList<>();
				entitiesOfType.add(e);
				text.put(e.getMesh(), entitiesOfType);
			}
		} else {
			if(entities.containsKey(e.getMesh())) {
				entities.get(e.getMesh()).add(e);
			} else {
				List<Entity> entitiesOfType = new ArrayList<>();
				entitiesOfType.add(e);
				entities.put(e.getMesh(), entitiesOfType);
			}
		}
	}
	
	public static void removeEntity(Mesh mesh, Entity e) {
		entities.get(mesh).remove(e);
	}
	
	public static Map<Mesh, List<Entity>> getText() {
		return text;
	}
	
	public static Map<Mesh, List<Entity>> getEntities() {
		return entities;
	}
	
}
