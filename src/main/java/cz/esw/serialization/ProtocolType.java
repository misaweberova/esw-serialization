package cz.esw.serialization;

/**
 * @author Marek Cuchý (CVUT)
 */
public enum ProtocolType {
	JSON, AVRO, PROTO;

	public static ProtocolType parseType(String typeName) {
		try {
			return ProtocolType.valueOf(typeName.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unknown protocol: " + typeName);
		}
	}
}
