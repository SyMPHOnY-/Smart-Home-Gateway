package WMBusBundle;

public class GwUtils {

	public static byte[] hexStringToByteArray(String s) {

		if (s.length() % 2 != 0)
			return null;

		try {
			int len = s.length();
			byte[] data = new byte[len / 2];
			for (int i = 0; i < len; i += 2) {
				data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
						.digit(s.charAt(i + 1), 16));
			}
			return data;
		} catch (Exception e) {
			return null;
		}
	}

	public static String byteArrayToHexString(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a)
			sb.append(String.format("%02X", b & 0xff));
		return sb.toString();
	}

}
