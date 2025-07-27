package novus;

import java.io.InputStream;

public class Demo2 {
	public static void main(String[] args) {
		try {
			// Test if files exist
			ClassLoader classLoader = Demo2.class.getClassLoader();

			String[] configs = { "config/web.json", "config/run_config.json", "config/application.json" };

			for (String config : configs) {
				InputStream is = classLoader.getResourceAsStream(config);
				if (is != null) {
					System.out.println("✓ Found: " + config);
					// Read content
					java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
					String content = s.hasNext() ? s.next() : "";
					System.out.println("Content length: " + content.length());
					System.out.println("First 100 chars: " + content.substring(0, Math.min(100, content.length())));
					is.close();
				} else {
					System.out.println("✗ Missing: " + config);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
