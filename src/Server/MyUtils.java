package Server;

import java.io.Closeable;
import java.io.IOException;

public class MyUtils {
	public static void closeAll(Closeable... closes) {
		for (Closeable close : closes) {
			try {
				close.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
