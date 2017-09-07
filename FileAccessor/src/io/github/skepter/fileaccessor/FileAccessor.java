package io.github.skepter.fileaccessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class to store and retrieve information from a file
 * 
 * @author Skepter
 *
 * @param <T>
 *            The type of object to store to a file
 */
public abstract class FileAccessor<T> {

	private final File file;
	private final JavaPlugin plugin;
	private final String errorDescription;

	/**
	 * Creates an instance of a FileAccessor
	 * 
	 * @param plugin
	 *            - Your plugin
	 * @param fileName
	 *            - The name of the file to store, for example "file.txt"
	 * @param errorDescription
	 *            - A useful error description for the name of the file. For
	 *            example "file"
	 */
	public FileAccessor(JavaPlugin plugin, String fileName, String errorDescription) {
		this(plugin, new File(plugin.getDataFolder(), fileName), errorDescription);
	}

	/**
	 * Creates an instance of a FileAccessor
	 * 
	 * @param plugin
	 *            - Your plugin
	 * @param file
	 *            - The file to store information to
	 * @param errorDescription
	 *            - A useful error description for the name of the file. For
	 *            example "file"
	 */
	public FileAccessor(JavaPlugin plugin, File file, String errorDescription) {
		if (plugin == null) {
			throw new IllegalArgumentException("Plugin cannot be null!");
		}
		this.plugin = plugin;

		if (file == null) {
			throw new IllegalArgumentException("File cannot be null!");
		}

		if (errorDescription == null) {
			errorDescription = file.getName();
		}
		this.errorDescription = errorDescription;

		this.file = file;
		if (!this.file.exists()) {
			plugin.getDataFolder().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().severe("Could not create " + errorDescription + " file!");
			}
		}
	}

	/**
	 * Retrieves a set of all of the elements stored in the file
	 * 
	 * @return
	 */
	public Set<T> get() {

		List<String> fileResults;
		try {
			fileResults = Files.readAllLines(file.toPath());
		} catch (IOException e1) {
			plugin.getLogger().severe("Could not get " + errorDescription + " from file!");
			return new HashSet<T>();
		}

		Set<T> set = new HashSet<T>();
		for (String str : fileResults) {
			set.add(deserialize(str));
		}
		return set;
	}

	/**
	 * Converts the <T> to a String. For example, to store a block's location,
	 * you'd store the worldname, x, y, z coordinates <br>
	 * <br>
	 * For example:<br>
	 * return type.getWorld().getName() + ":" + type.getLocation().getBlockX() +
	 * ":" + type.getLocation().getBlockY() + ":" +
	 * type.getLocation().getBlockZ();
	 * 
	 * @param type
	 *            The object to serialize
	 * @return A serialized string
	 */
	public abstract String serialize(T object);

	/**
	 * Converts the String version of your object back to an object. In short,
	 * the opposite of serialize(T). For example, retrieving a block from a
	 * location in the form world:x:y:z<br>
	 * <br>
	 * 
	 * For example:<br>
	 * String[] args = s.split(":");<br>
	 * return Bukkit.getWorld(args[0]).getBlockAt(Integer.parseInt(args[1]),
	 * Integer.parseInt(args[2]), Integer.parseInt(args[3]));
	 * 
	 * @param s
	 *            The String to deserialize
	 * @return A deserialized object
	 */
	public abstract T deserialize(String s);

	/**
	 * Stores a set of objects to a file
	 * 
	 * @param types
	 *            A set of objects to store
	 */
	public void store(Set<T> types) {
		file.delete();
		try {
			file.createNewFile();

			StringBuilder builder = new StringBuilder();
			types.forEach(e -> {
				builder.append(serialize(e));
				builder.append("\n");
			});
			builder.deleteCharAt(builder.lastIndexOf("\n"));

			Files.write(file.toPath(), builder.toString().getBytes());
		} catch (IOException e) {
			plugin.getLogger().severe("Could not store " + errorDescription + " to file!");
		}
	}
}
